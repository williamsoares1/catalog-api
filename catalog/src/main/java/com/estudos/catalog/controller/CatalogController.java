package com.estudos.catalog.controller;

import org.springframework.web.bind.annotation.RestController;

import com.estudos.catalog.dto.request.CategoryIdDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.entity.Category;
import com.estudos.catalog.entity.Product;
import com.estudos.catalog.repository.mongodb.CategoryRepository;
import com.estudos.catalog.repository.mongodb.ProductRepository;
import com.estudos.catalog.util.ProductMapper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class CatalogController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/product")
    public ResponseEntity<Product> postProduct(@RequestBody ProductRequestDTO dto) {
        Product entity = ProductMapper.INSTANCE.toEntity(dto);

        if (dto.categoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(dto.categoryId());

            if (categoryOpt.isPresent())
                entity.setParts(categoryOpt.get());
        }

        return ResponseEntity.ok(productRepository.save(entity));
    }

    @PostMapping("/category")
    public ResponseEntity<Category> postCategory(@RequestBody Category entity) {
        return ResponseEntity.ok(categoryRepository.save(entity));
    }

    @PostMapping("/product/{id}")
    public ResponseEntity<Product> postPartProduct(@PathVariable String id, @RequestBody CategoryIdDTO dto) {
        Optional<Product> productOpt = productRepository.findById(id);
        Optional<Category> categoryOpt = categoryRepository.findById(dto.categoryId());

        if (!productOpt.isPresent() || !categoryOpt.isPresent())
            return ResponseEntity.notFound().build();

        Product product = productOpt.get();
        product.setParts(categoryOpt.get());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody ProductRequestDTO dto) {
        Optional<Product> productOpt = productRepository.findById(id);

        if (!productOpt.isPresent())
            return ResponseEntity.notFound().build();

        Product product = productOpt.get();
        Product entity = ProductMapper.INSTANCE.toEntity(dto);
        entity.setProductId(id);
        entity.setParts(product.getParts());

        if (dto.categoryId() != null) {
            
            if (dto.categoryId().equals("delete")) {
                entity.setParts(null);
                return ResponseEntity.ok(productRepository.save(entity));
            }

            Optional<Category> categoryOpt = categoryRepository.findById(dto.categoryId());

            if (categoryOpt.isPresent())
                entity.setParts(categoryOpt.get());
        }

        return ResponseEntity.ok(productRepository.save(entity));
    }
}
