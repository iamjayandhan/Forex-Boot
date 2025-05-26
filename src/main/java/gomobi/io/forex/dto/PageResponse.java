package gomobi.io.forex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int pageNumber;
    private int pageSize;

    public PageResponse(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }
    
    public PageResponse(List<T> content, long totalElements, int pageNumber, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
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
    
    public int getPageNumber() {
    	return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
    	this.pageNumber = pageNumber;
    }
    
    public int getPageSize() {
    	return pageSize;
    }
    
    public void setPageSize(int pageSize) {
    	this.pageSize = pageSize;
    }
}
