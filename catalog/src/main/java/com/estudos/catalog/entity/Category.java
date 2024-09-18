package com.estudos.catalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "category")
@Data
public class Category {
    @Id
    private String categoryId;
    private String title;
    private String description;
    private String ownerId;
}
