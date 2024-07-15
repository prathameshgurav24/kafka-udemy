package org.kafka.beginers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWithShutDowm {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("Lets Go..........");

        String bootstrapServers = "localhost:9092";
        String groupId = "my-consumer-1";
        String topic = "demoProducer-2";

        //create consumer properties
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", bootstrapServers);

//        properties.setProperty("security.protocol","SASL_SSL");
//        properties.setProperty("sasl.jaas.config","localhost:9092");
//        properties.setProperty("sasl.mechanism","PLAIN");

        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());
        properties.setProperty("group.id", groupId);
    //    properties.setProperty("partition.assignment.strategy", CooperativeStickyAssignor.class.getName());

        //none/earliest/latest
        properties.setProperty("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        //create ref to main thread
        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutdown detected, lets exit by calling consumer.wakeup()");
                kafkaConsumer.wakeup();

                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        try {

            //subscribe topic
            kafkaConsumer.subscribe(Arrays.asList(topic));

            while (true) {
                ConsumerRecords<String, String> records =
                        kafkaConsumer.poll(1000);

                for (ConsumerRecord<String, String> record : records) {
                    log.info("key:" + record.key() + " | value:" + record.value());
                    log.info("partition:" + record.partition() + " | offset:" + record.offset());
                }

            }
        } catch (WakeupException e) {
            log.info("WakeupException triggered");
        }catch (Exception e){
            log.error("Unexpected exception "+ e.getLocalizedMessage());
        }
        finally {
            kafkaConsumer.close();
            log.info("Consumer closed gracefully");
        }


    }
}
