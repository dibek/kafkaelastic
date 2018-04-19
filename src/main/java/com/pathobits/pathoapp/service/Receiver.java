package com.pathobits.pathoapp.service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.pathobits.pathoapp.domain.Message;
import com.pathobits.pathoapp.repository.search.MessageSearchRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public Receiver(MessageSearchRepository messageSearchRepository) {
        this.messageSearchRepository = messageSearchRepository;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    private final MessageSearchRepository messageSearchRepository;

    @KafkaListener(topics = "${kafka.topic.boot}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) throws IOException {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(consumerRecord.value().toString(), Message.class);
        messageSearchRepository.save(message);
        latch.countDown();
    }
}
