package gomobi.io.forex.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private long totalElements;

    public PageResponse(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    // Getters and setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
