package com.estudos.catalog.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "product")
@Data
public class Product {
    @Id
    private String productId;
    private String title;
    private String description;
    private BigDecimal price;
    @DBRef
    private Category parts;
    private String ownerId;
}
