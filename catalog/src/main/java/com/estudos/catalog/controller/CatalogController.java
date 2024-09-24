package com.estudos.catalog.controller;

import org.springframework.web.bind.annotation.RestController;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO;
import com.estudos.catalog.services.CatalogService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> postProduct(@RequestBody ProductRequestDTO dto) {
        return ResponseEntity.of(catalogService.postProduct(dto));
    }

    @PostMapping("/category")
    public ResponseEntity<CategoryResponseDTO> postCategory(@RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.of(catalogService.postCategory(dto));
    }

    @PostMapping("/product/{productId}/category/{categoryId}")
    public ResponseEntity<ProductResponseDTO> insertCategoryProduct(@PathVariable String productId,
            @PathVariable String categoryId) {
        return ResponseEntity.of(catalogService.insertCategoryProduct(productId, categoryId));
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String id,
            @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.of(catalogService.updateProduct(id, dto));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        return ResponseEntity.ok(catalogService.deleteProduct(id));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        return ResponseEntity.ok(catalogService.deleteCategory(id));
    }

    @GetMapping("/catalog/{ownerId}")
    public List<CatalogDTO> getMethodName(@PathVariable String ownerId) {
        return catalogService.getCatalog(ownerId);
    }
    
}
