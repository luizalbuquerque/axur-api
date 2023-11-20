package axururlapi.service.impl;

import axururlapi.dto.CrawlResult;
import axururlapi.entity.SearchEntity;
import axururlapi.enun.SearchStatus;
import axururlapi.repository.SearchRepository;
import axururlapi.service.WebCrawlerService;
import jakarta.annotation.PreDestroy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class WebCrawlerServiceImpl implements WebCrawlerService {

    @Autowired
    private SearchRepository searchRepository;

    @Value("${base.url}")
    private String baseUrl;

    private static final int MAX_DEPTH = 5; // Definir um limite para a profundidade do rastreamento
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public String startCrawling(String keyword) {
        if (keyword == null || keyword.length() < 4 || keyword.length() > 32) {
            throw new IllegalArgumentException("Keyword must be between 4 and 32 characters");
        }
        String searchId = generateUniqueId();
        SearchEntity searchEntity = new SearchEntity();
        searchEntity.setId(searchId);
        searchEntity.setKeyword(keyword);
        searchEntity.setSearchStatus(SearchStatus.ACTIVE);
        searchRepository.save(searchEntity);
        executorService.submit(() -> performCrawling(searchId, keyword));
        return searchId;
    }

    @Async
    protected void performCrawling(String searchId, String keyword) {
        try {
            crawlPage(baseUrl, keyword, searchId);
        } catch (Exception e) {
            e.printStackTrace();
            updateSearchStatusWithError(searchId);
        }
    }

    private void updateSearchStatusWithError(String searchId) {
        SearchEntity searchEntity = searchRepository.findById(searchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid search ID"));
        searchEntity.setSearchStatus(SearchStatus.ERROR);
        searchRepository.save(searchEntity);
    }


    private void crawlPage(String url, String keyword, String searchId) throws IOException {
        Set<String> visitedUrls = new HashSet<>();
        crawlPageRecursive(url, keyword, searchId, 0, visitedUrls);
    }

    private void crawlPageRecursive(String url, String keyword, String searchId, int depth, Set<String> visitedUrls) throws IOException {

        if (depth > MAX_DEPTH || visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        Document doc = Jsoup.connect(url).get();
        if (doc.text().toLowerCase().contains(keyword.toLowerCase())) {
            addUrlToSearch(searchId, url);
        }

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String nextUrl = link.attr("abs:href");
            if (shouldCrawlUrl(url, nextUrl) && !visitedUrls.contains(nextUrl)) {
                crawlPageRecursive(nextUrl, keyword, searchId, depth + 1, visitedUrls);
            }
        }
    }

    private boolean shouldCrawlUrl(String baseUrl, String nextUrl) {
        // Verificar se o próximo URL está no mesmo domínio que a URL base
        return getDomainName(baseUrl).equals(getDomainName(nextUrl));
    }

    private String getDomainName(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }

    private synchronized void addUrlToSearch(String searchId, String url) {
        SearchEntity searchEntity = searchRepository.findById(searchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid search ID"));
        List<String> urls = searchEntity.getUrls();
        if (urls == null) {
            urls = new ArrayList<>();
        }
        urls.add(url);
        searchEntity.setUrls(urls);
        searchRepository.save(searchEntity);
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

    @Override
    public CrawlResult getCrawlResults(String id) {
        SearchEntity search = searchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid search ID"));
        return new CrawlResult(
                search.getId(),
                search.getSearchStatus().toString(),
                search.getUrls() // As URLs encontradas até agora
        );
    }

    @PreDestroy
    public void onDestroy() {
        executorService.shutdown(); // Fecha o ExecutorService quando a aplicação for encerrada
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
