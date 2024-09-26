package com.estudos.catalog.infra.aws.s3.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.estudos.catalog.infra.aws.sqs.dto.MessageDTO;
import com.estudos.catalog.services.CatalogService;

@Service
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Autowired
    private CatalogService catalogService;

    public S3Service(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public List<Bucket> listBuckets() {
        return amazonS3Client.listBuckets();
    }

    public void putObject(MessageDTO message) {
        InputStream inputStream = catalogService.buildCatalogJson(message.ownerId());
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
}
