package com.estudos.catalog.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;
import com.estudos.catalog.entity.Category;
import com.estudos.catalog.entity.Product;
import com.estudos.catalog.repository.mongodb.CategoryRepository;
import com.estudos.catalog.repository.mongodb.ProductRepository;
import com.estudos.catalog.services.CatalogService;
import com.estudos.catalog.util.CatalogMapper;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProducts'");
    }

    @Override
    public Optional<ProductResponseDTO> getProductById(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductById'");
    }

    @Override
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCategories'");
    }

    @Override
    public Optional<CategoryResponseDTO> getCategoryById(String categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategoryById'");
    }

    @Override
    public Optional<ProductResponseDTO> postProduct(ProductRequestDTO dto) {
        Product entity = CatalogMapper.INSTANCE.toEntity(dto);

        if (dto.categoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(dto.categoryId());

            if (categoryOpt.isPresent())
                entity.setParts(categoryOpt.get());
        }

        Product productPersistence = productRepository.save(entity);

        ProductResponseDTO response = CatalogMapper.INSTANCE.toDto(productPersistence);

        return Optional.of(response);
    }

    @Override
    public Optional<CategoryResponseDTO> postCategory(CategoryRequestDTO dto) {
        Category entity = CatalogMapper.INSTANCE.toEntity(dto);

        Category categoryPersistence = categoryRepository.save(entity);

        CategoryResponseDTO response = CatalogMapper.INSTANCE.toDto(categoryPersistence);

        return Optional.of(response);
    }

    @Override
    public Optional<ProductResponseDTO> insertCategoryProduct(String productId, String categoryId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!productOpt.isPresent() || !categoryOpt.isPresent())
            return Optional.empty();

        Product entity = productOpt.get();
        entity.setParts(categoryOpt.get());

        Product productPersistence = productRepository.save(entity);
        ProductResponseDTO response = CatalogMapper.INSTANCE.toDto(productPersistence);

        return Optional.of(response);
    }

    @Override
    public Optional<ProductResponseDTO> updateProduct(String productId, ProductRequestDTO dto) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (!productOpt.isPresent())
            return Optional.empty();

        Product product = productOpt.get();
        Product entity = CatalogMapper.INSTANCE.toEntity(dto);
        entity.setProductId(productId);
        entity.setParts(product.getParts());

        if (dto.categoryId() != null) {

            if (dto.categoryId().equals("delete")) {
                entity.setParts(null);
            }

            Optional<Category> categoryOpt = categoryRepository.findById(dto.categoryId());

            if (categoryOpt.isPresent())
                entity.setParts(categoryOpt.get());
        }

        Product productPersistence = productRepository.save(entity);

        ProductResponseDTO response = CatalogMapper.INSTANCE.toDto(productPersistence);

        return Optional.of(response);
    }

    @Override
    public Void deleteProduct(String productId) {
        productRepository.deleteById(productId);
        return null;
    }

    @Override
    public Void deleteCategory(String categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if(categoryOpt.isEmpty()){
            return null;
        }

        Category category = categoryOpt.get();

        List<Product> products = productRepository.findByParts(category);

        products.forEach((p) -> {
            p.setParts(null);
            productRepository.save(p);
        });

        categoryRepository.deleteById(categoryId);
        return null;
    }

}
