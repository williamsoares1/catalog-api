package com.estudos.catalog.infra.aws.s3.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO;
import com.estudos.catalog.infra.aws.s3.json.CatalogJson;
import com.estudos.catalog.infra.aws.sqs.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class S3Service {

    private final AmazonS3 amazonS3Client;

    public S3Service(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    public List<Bucket> listBuckets() {
        return amazonS3Client.listBuckets();
    }

    public void putObject(MessageDTO message) {
        InputStream inputStream = buildJson(message.ownerId());
        ObjectMetadata metadata = new ObjectMetadata();
        
        try {
            metadata.setContentLength(inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        amazonS3Client.putObject(
                "catalog-api-1938-br",
                message.ownerId() + "catalog.json",
                inputStream, metadata);
    }

    public InputStream buildJson(String ownerId) {
        CatalogJson catalogJson = CatalogJson.builder().catalog(catalog(ownerId)).owner(ownerId).build();

        String jsonContent = "";

        try {
            jsonContent = objectMapper.writeValueAsString(catalogJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));

        return inputStream;
    }

    public List<CatalogDTO> catalog(String ownerId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("ownerId").is(ownerId));

        LookupOperation lookupOperation = Aggregation.lookup(
                "product",
                "_id",
                "parts.$id",
                "itens");

        ProjectionOperation projectOperation = Aggregation.project()
                .and("title").as("categoryTitle")
                .and("description").as("categoryDescription")
                .and("itens").as("itens")
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
}
