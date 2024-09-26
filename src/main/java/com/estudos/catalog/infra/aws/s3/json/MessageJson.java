package com.estudos.catalog.infra.aws.s3.json;

import java.util.Date;

import lombok.Data;

@Data
public class MessageJson {
    private String correlationId;
    private String ownerId;
    private Date createdAt;
}
