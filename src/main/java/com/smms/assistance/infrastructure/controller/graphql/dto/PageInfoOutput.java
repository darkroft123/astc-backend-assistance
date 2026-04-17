package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PageInfoOutput {
    private long totalItems;
    private int page;
    private int size;
    private int totalPages;
}
