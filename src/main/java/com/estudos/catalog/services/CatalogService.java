package com.estudos.catalog.services;

import java.util.List;
import java.util.Optional;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO.ProductDTO;

@Service
public interface CatalogService {

    List<CatalogDTO> getCatalog(String ownerId);

    List<ProductDTO> getNoAllowedItems(String ownerId);

    Optional<ProductResponseDTO> postProduct(ProductRequestDTO dto);

    Optional<CategoryResponseDTO> postCategory(CategoryRequestDTO dto);

    Optional<ProductResponseDTO> insertCategoryProduct(String productId, String categoryId);

    Optional<ProductResponseDTO> updateProduct(String productId, ProductRequestDTO dto);

    Void deleteProduct(String productId);

    Void deleteCategory(String categoryId);

    InputStream buildCatalogJson(String ownerId);
}
