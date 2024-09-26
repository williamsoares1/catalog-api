package com.estudos.catalog.dto.response;

import lombok.Builder;

@Builder
public record CategoryResponseDTO(String categoryId, String title, String description, String ownerId) {

}
