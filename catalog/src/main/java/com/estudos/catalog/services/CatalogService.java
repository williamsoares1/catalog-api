package com.estudos.catalog.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;

@Service
public interface CatalogService {

    Page<ProductResponseDTO> getAllProducts(@PageableDefault(size = 5, page = 0) Pageable pageable);

    Optional<ProductResponseDTO> getProductById(String productId);

    Page<CategoryResponseDTO> getAllCategories(@PageableDefault(size = 10, page = 0) Pageable pageable);

    Optional<CategoryResponseDTO> getCategoryById(String categoryId);

    Optional<ProductResponseDTO> postProduct(ProductRequestDTO dto);

    Optional<CategoryResponseDTO> postCategory(CategoryRequestDTO dto);

    Optional<ProductResponseDTO> insertCategoryProduct(String productId, String categoryId);

    Optional<ProductResponseDTO> updateProduct(String productId, ProductRequestDTO dto);

    Void deleteProduct(String productId);

    Void deleteCategory(String categoryId);
}
