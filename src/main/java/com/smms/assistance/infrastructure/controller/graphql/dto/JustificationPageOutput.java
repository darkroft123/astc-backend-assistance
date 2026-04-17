package com.smms.assistance.infrastructure.controller.graphql.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JustificationPageOutput {
    private List<JustificationOutput> items;
    private PageInfoOutput pageInfo;
}
