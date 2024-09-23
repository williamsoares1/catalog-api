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

@Mapper
public interface CatalogMapper {

    CatalogMapper INSTANCE = Mappers.getMapper(CatalogMapper.class);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "parts", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    @Mapping(target = "category", ignore = true)
    ProductResponseDTO toDto(Product entity);

    @Mapping(target = "categoryId", ignore = true)
    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toDto(Category entity);
}
