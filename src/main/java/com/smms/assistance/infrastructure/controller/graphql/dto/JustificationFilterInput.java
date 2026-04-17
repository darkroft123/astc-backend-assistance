package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustificationFilterInput {
    private String status;
    private String from;
    private String to;
    private int page = 0;
    private int size = 20;
}
