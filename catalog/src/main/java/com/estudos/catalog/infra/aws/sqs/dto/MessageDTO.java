package com.estudos.catalog.infra.aws.sqs.dto;

import java.util.Date;

import lombok.Builder;

@Builder
public record MessageDTO(String correlationId, String ownerId,
        Date createdAt) {

}
