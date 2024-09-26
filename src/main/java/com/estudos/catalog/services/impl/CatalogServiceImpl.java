package com.estudos.catalog.services.impl;

import java.util.List;
import java.util.Optional;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.estudos.catalog.dto.request.CategoryRequestDTO;
import com.estudos.catalog.dto.request.ProductRequestDTO;
import com.estudos.catalog.dto.response.CategoryResponseDTO;
import com.estudos.catalog.dto.response.ProductResponseDTO;
import com.estudos.catalog.entity.Category;
import com.estudos.catalog.entity.Product;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO.ProductDTO;
import com.estudos.catalog.infra.aws.s3.json.CatalogJson;
import com.estudos.catalog.infra.aws.sqs.publisher.Publisher;
import com.estudos.catalog.repository.mongodb.CategoryRepository;
import com.estudos.catalog.repository.mongodb.ProductRepository;
import com.estudos.catalog.services.CatalogService;
import com.estudos.catalog.util.CatalogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service

public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Publisher publisher;

    @Autowired
    private MongoTemplate mongoTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

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
        publisher.publishMessage(dto.ownerId());

        return Optional.of(response);
    }

    @Override
    public Optional<CategoryResponseDTO> postCategory(CategoryRequestDTO dto) {
        Category entity = CatalogMapper.INSTANCE.toEntity(dto);

        Category categoryPersistence = categoryRepository.save(entity);

        CategoryResponseDTO response = CatalogMapper.INSTANCE.toDto(categoryPersistence);
        publisher.publishMessage(dto.ownerId());

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
        publisher.publishMessage(response.ownerId());

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
        publisher.publishMessage(dto.ownerId());

        return Optional.of(response);
    }

    @Override
    public Void deleteProduct(String productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // ou busca o ownerId de outra maneira
            publisher.publishMessage(product.getOwnerId());

            productRepository.deleteById(productId);
        }

        return null;
    }

    @Override
    public Void deleteCategory(String categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty()) {
            return null;
        }

        Category category = categoryOpt.get();

        List<Product> products = productRepository.findByParts(category);

        products.forEach((p) -> {
            p.setParts(null);
            productRepository.save(p);
        });

        publisher.publishMessage(category.getOwnerId());
        categoryRepository.deleteById(categoryId);

        return null;
    }

    @Override
    public InputStream buildCatalogJson(String ownerId) {
        CatalogJson catalogJson = CatalogJson.builder().catalog(getCatalog(ownerId))
                .noAllowedItems(getNoAllowedItems(ownerId)).owner(ownerId).build();

        String jsonContent = "";

        try {
            jsonContent = objectMapper.writeValueAsString(catalogJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));

        return inputStream;
    }

    @Override
    public List<CatalogDTO> getCatalog(String ownerId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("ownerId").is(ownerId));

        LookupOperation lookupOperation = Aggregation.lookup(
                "product",
                "_id",
                "parts.$id",
                "items");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("categoryTitle")
                .and("description").as("categoryDescription")
                .and("items").as("items")
                .and("products.title").as("title")
                .and("products.description").as("description")
                .and("products.price").as("price");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                projectOperation);

        AggregationResults<CatalogDTO> results = mongoTemplate.aggregate(aggregation, "category", CatalogDTO.class);
        return results.getMappedResults();
    }

    @Override
    public List<ProductDTO> getNoAllowedItems(String ownerId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("ownerId").is(ownerId)
                .andOperator(
                        Criteria.where("parts").exists(false)));

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("title")
                .and("description").as("description")
                .and("price").as("price");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectOperation);

        AggregationResults<ProductDTO> results = mongoTemplate.aggregate(aggregation, "product", ProductDTO.class);
        return results.getMappedResults();
    }
}
