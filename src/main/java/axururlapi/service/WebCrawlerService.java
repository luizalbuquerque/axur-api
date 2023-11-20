package axururlapi.service;

import axururlapi.dto.CrawlResult;

public interface WebCrawlerService {

    String startCrawling(String keyword);

    CrawlResult getCrawlResults(String id);

}
