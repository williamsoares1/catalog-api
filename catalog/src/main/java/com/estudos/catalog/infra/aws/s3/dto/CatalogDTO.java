package com.estudos.catalog.infra.aws.s3.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogDTO {
    private String categoryTitle;
    private String categoryDescription;
    private List<ProductDTO> itens;

    @Data
    @Builder
    public static class ProductDTO {
        private String title;
        private String description;
        private Double price;
    }
}
