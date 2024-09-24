package com.estudos.catalog.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;
import com.estudos.catalog.entity.Category;
import com.estudos.catalog.entity.Product;
import com.estudos.catalog.infra.aws.s3.json.MessageJson;
import com.estudos.catalog.infra.aws.sqs.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mapper
public interface CatalogMapper {

    CatalogMapper INSTANCE = Mappers.getMapper(CatalogMapper.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "parts", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    ProductResponseDTO toDto(Product entity);

    @Mapping(target = "categoryId", ignore = true)
    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toDto(Category entity);

    @Mapping(source = "correlationId", target = "correlationId")
    @Mapping(source = "ownerId", target = "ownerId")
    @Mapping(source = "createdAt", target = "createdAt")
    MessageDTO map(MessageJson messageJson);

    default MessageDTO tDto(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MessageJson messageJson = objectMapper.readValue(jsonString, MessageJson.class);
            return map(messageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
