package com.estudos.catalog.dto.request;

import lombok.Builder;

@Builder
public record CategoryRequestDTO(String title, String description, String ownerId) {

}
