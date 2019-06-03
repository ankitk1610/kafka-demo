package com.demo.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoAssignAndSeek {
	private static final String FIRST_TOPIC = "first_topic";
	private static final String MY_SEVENTH_APP = "my-seventh-app";
	private static final String DESERIALIZER = StringDeserializer.class.getName();
	private static final String LOCALHOST = "127.0.0.1:9092";
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoAssignAndSeek.class);

	public static void main(String[] args) {

//		create config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//		Create consumer

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

//		Subscribe Consumer to Topics
		
		
//		assign and seek are used to replay data or fetch a specific data

//		Assign :
		TopicPartition partitionTpReadFrom = new TopicPartition(FIRST_TOPIC	, 0);
		Long offsetToReadFrom = 5L; 
		consumer.assign(Arrays.asList(partitionTpReadFrom));
		 
		 consumer.seek(partitionTpReadFrom,  offsetToReadFrom);
		
		
		 int numnerOfMessagesToRead = 5;
		 boolean keepOnReading = true;
		 int numberOfMessagesReadSoFar = 0;
		 
		 
//		Poll for new data
		while (keepOnReading) {  
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				numberOfMessagesReadSoFar += 1;
				LOGGER.info("Key : " + record .key() + " Value : " + record.value());
				LOGGER.info("Partition : " + record.partition() + " , Offset : " + record.offset());
				if(numberOfMessagesReadSoFar  >= numnerOfMessagesToRead) {
					keepOnReading = false;
					break;
				}

			}
			LOGGER.info("Exiting...");

		}

	}

}
