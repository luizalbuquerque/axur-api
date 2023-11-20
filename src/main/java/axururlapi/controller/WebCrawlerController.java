package axururlapi.controller;

import axururlapi.dto.CrawlResponse;
import axururlapi.dto.CrawlResult;
import axururlapi.dto.KeywordRequest;
import axururlapi.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebCrawlerController {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @PostMapping("/crawl")
    public ResponseEntity<?> initiateCrawl(@RequestBody KeywordRequest keywordRequest) {
        String crawlId = webCrawlerService.startCrawling(keywordRequest.getKeyword());
        return ResponseEntity.ok(new CrawlResponse(crawlId));
    }

    @GetMapping("/crawl/{id}")
    public ResponseEntity<?> getCrawlResults(@PathVariable String id) {
        CrawlResult result = webCrawlerService.getCrawlResults(id);
        return ResponseEntity.ok(result);
    }

}
