package com.estudos.catalog.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.entity.Product;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "parts", ignore = true)
    Product toEntity(ProductRequestDTO dto);
}
