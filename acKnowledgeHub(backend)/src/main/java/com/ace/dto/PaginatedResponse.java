package com.ace.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PaginatedResponse<T> {

    private Page<T> data;
    private boolean hasMore;

    public PaginatedResponse(Page<T> data) {
        this.data = data;
        this.hasMore = data.hasNext();
    }

    // Getters and Setters
}
