package org.kafka.beginers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProducerDemo {
    private static  final Logger log= LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("Lets Go..........");

        //create producer properties
        Properties properties=new Properties();

        properties.setProperty("bootstrap.servers","localhost:9092");

//        properties.setProperty("security.protocol","SASL_SSL");
//        properties.setProperty("sasl.jaas.config","localhost:9092");
//        properties.setProperty("sasl.mechanism","PLAIN");

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer",StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String,String> producer=new KafkaProducer<>(properties);

        //create producer record
        ProducerRecord<String,String> producerRecord= new ProducerRecord<>("demoProducer-3","Hello Kafka!!!!!!");

        //send data
        producer.send(producerRecord);

        //flush and close the producer
        //tells the producer to send all data and block until done---synchronous
        producer.flush();

        producer.close();

        CompletableFuture<String> stringCompletableFuture=CompletableFuture.supplyAsync(
                ()->{
                    return "this is completableFuture from spplyAsync";
                }
        );

        System.out.println(stringCompletableFuture.get());
    }
}
