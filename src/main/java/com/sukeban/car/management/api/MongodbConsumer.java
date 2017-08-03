package com.sukeban.car.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class MongodbConsumer extends Thread {

    private String topicName;
    private String groupName;
    private KafkaConsumer<String, String> kafkaConsumer;

    public MongodbConsumer() {
    }

    public MongodbConsumer(String topicName, String groupName) {
        this.topicName = topicName;
        this.groupName = groupName;

    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void run() {

        ObjectMapper mapper = new ObjectMapper();
        DbQuery dbQuery = new DbQuery();

        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "car-management");

        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        //Start processing messages
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {

                    User user = mapper.readValue(record.value(), User.class);
                    if(dbQuery.getUser(user.getLastName(), user.getFirstName()) == null) {
                        dbQuery.getDatastore().save(user);
                    }

                }
            }
        } catch (WakeupException ex) {
            System.out.println("Exception caught " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(MongodbConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }
    }
}
