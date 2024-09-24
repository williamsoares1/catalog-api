package com.estudos.catalog.infra.aws.sqs.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.estudos.catalog.infra.aws.s3.service.S3Service;
import com.estudos.catalog.infra.aws.sqs.dto.MessageDTO;
import com.estudos.catalog.util.CatalogMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class Consumer {

    @Value("${aws.queueName}")
    private String queueName;

    @Autowired
    private S3Service s3Service;

    private final AmazonSQS amazonSQSClient;

    public Consumer(AmazonSQS amazonSQSClient) {
        this.amazonSQSClient = amazonSQSClient;
    }

    @Scheduled(fixedDelay = 3000)
    public void consumeMessages() {
        try {
            String queueUrl = amazonSQSClient.getQueueUrl(queueName).getQueueUrl();

            ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

            if (!receiveMessageResult.getMessages().isEmpty()) {
                Message sqsMessage = receiveMessageResult.getMessages().get(0);

                MessageDTO message = CatalogMapper.INSTANCE.tDto(sqsMessage.getBody());
                log.info(":::::::::::::::::::::::" + sqsMessage.getBody());
                log.info(":::::::::::::::::::::::" + message);
                s3Service.putObject(message);

                amazonSQSClient.deleteMessage(queueUrl, sqsMessage.getReceiptHandle());
            }

        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
}
