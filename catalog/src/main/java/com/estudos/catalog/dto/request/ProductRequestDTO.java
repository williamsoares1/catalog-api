package com.estudos.catalog.dto.request;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record ProductRequestDTO(String title, String description, BigDecimal price, String categoryId, String ownerId) {

}
