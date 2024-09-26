package com.estudos.catalog.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record ProductResponseDTO(String productId, String title, String description, BigDecimal price, String category, String ownerId) {

}
