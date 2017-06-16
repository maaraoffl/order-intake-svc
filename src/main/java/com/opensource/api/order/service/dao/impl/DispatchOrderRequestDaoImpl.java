package com.opensource.api.order.service.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.api.order.model.OrderRequest;
import com.opensource.api.order.service.dao.DispatchOrderRequestDao;
import com.opensource.api.order.service.util.ZookeeperUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.connect.json.JsonConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Properties;

/**
 * Created by amg871 on 5/6/17.
 */
@Component
public class DispatchOrderRequestDaoImpl implements DispatchOrderRequestDao {

//    @Value("${order.process.topic.brokerlist}")
    private String orderProcessTopicBrokers;

    @Value("${order.process.topic.name}")
    private String orderProcessTopicName;

    @Value("${zookeeper.address}")
    private String zooKeeperAddress;

    @Inject
    private ZookeeperUtil zookeeperUtil;

    @PostConstruct
    public void postConstruct() throws Exception{
        orderProcessTopicBrokers=zookeeperUtil.getBrokers(zooKeeperAddress);
    }


    @Override
    public void sendRequestToProcessQueue(OrderRequest orderRequest) {
        Properties props = new Properties();
//        props.put("bootstrap.servers", zookeeperUtil.getBrokers(zooKeeperAddress));
        props.put("bootstrap.servers", orderProcessTopicBrokers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");



        KafkaProducer<String, String> myProducer = new KafkaProducer<String, String>(props);
        try{

            ObjectMapper mapper = new ObjectMapper();
            String orderAsJsonString = mapper.writeValueAsString(orderRequest);
            String key = String.valueOf(Instant.now().toEpochMilli());

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(orderProcessTopicName, key, orderAsJsonString);
            myProducer.send(record, (metadata, exception) -> {
                if(exception !=null)
                    System.out.println(exception.getMessage());
                else
                    System.out.println(metadata.topic() + ":" + metadata.offset());
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            myProducer.close();
        }

    }
}
