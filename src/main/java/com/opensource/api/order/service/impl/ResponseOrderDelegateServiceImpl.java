package com.opensource.api.order.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.api.order.model.OrderRequest;
import com.opensource.api.order.rest.OrderIntakeRest;
import com.opensource.api.order.service.api.OrderResponseService;
import com.opensource.api.order.service.util.ZookeeperUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.api.order.model.Order;
import com.opensource.api.order.model.OrderRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.inject.Named;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Created by amg871 on 5/20/17.
 */

@Named

public class ResponseOrderDelegateServiceImpl implements OrderResponseService{



    @Value("${order.response.consumer.group}")
    private String orderResponseConsumerGroup;

//    @Value("${order.response.topic.brokers}")
    private String orderResponseTopicBrokers;

    @Value("${order.response.topic.name}")
    private String orderResponseTopicName;

    @Value("${zookeeper.address}")
    private String zooKeeperAddress;

    @Inject
    private ZookeeperUtil zookeeperUtil;

    @PostConstruct
    public void postConstruct() throws Exception{
        orderResponseTopicBrokers=zookeeperUtil.getBrokers(zooKeeperAddress);
    }

    private Logger logger = LoggerFactory.getLogger(ResponseOrderDelegateServiceImpl.class);

//    @EventListener
//    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception{
//
//        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
//            recieveOrderResponseFromTopic();
//        });
//    }

    @PostConstruct
    public void handleContextRefresh() throws Exception{

        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            recieveOrderResponseFromTopic();
        });
    }


    @Override
    public Order recieveOrderResponseFromTopic() {
        Properties props = new Properties();
        props.put("bootstrap.servers", orderResponseTopicBrokers);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", orderResponseConsumerGroup);

        KafkaConsumer consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(orderResponseTopicName));

        try {
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                List<Order> orderResponseList = new ArrayList<Order>();
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(
                            (String.format("Topic %s, Partition %d, Offset %d, Key %s, Value %s",
                                    record.topic(), record.partition(), record.offset(), record.key(), record.value())));
                    ObjectMapper mapper = new ObjectMapper();
                    Order response = mapper.readValue(record.value(), Order.class);
                    orderResponseList.add(response);
                }
                orderResponseList.forEach(order -> {
                    logger.info("Received response back fro id: {} ",order.getId());
                    if(OrderIntakeRest.requestResponseMap.containsKey(order.getId()))
                        OrderIntakeRest.requestResponseMap.get(order.getId()).setResult(ResponseEntity.accepted().build());
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            consumer.close();
        }
        return null;
    }
}
