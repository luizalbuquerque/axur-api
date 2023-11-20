package axururlapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class CrawlResult {

    private String id;
    private String status;
    private List<String> urls;

    // Construtores, getters e setters

    public CrawlResult(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }
}