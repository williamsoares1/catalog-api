package com.estudos.catalog.infra.aws.sqs.publisher;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.estudos.catalog.infra.aws.sqs.dto.MessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class Publisher {

    @Value("${aws.queueName}")
    private String queueName;

    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;

    public Publisher(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    public void publishMessage(String ownerId) {
        try {
            String correlationId = UUID.randomUUID().toString();
            GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);
            var message = MessageDTO.builder()
                    .correlationId(correlationId)
                    .ownerId(ownerId)
                    .createdAt(new Date()).build();
            @SuppressWarnings("unused")
            var result = amazonSQSClient.sendMessage(queueUrl.getQueueUrl(), objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }

    }

}
