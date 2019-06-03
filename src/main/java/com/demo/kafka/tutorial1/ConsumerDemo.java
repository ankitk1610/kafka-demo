package com.demo.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {
	private static final String FIRST_TOPIC = "first_topic";
	private static final String MY_FOURTH_APP = "my-fourth-app";
	private static final String DESERIALIZER = StringDeserializer.class.getName();
	private static final String LOCALHOST = "127.0.0.1:9092";
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemo.class);

	public static void main(String[] args) {

//		create config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, MY_FOURTH_APP);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//		Create consumer

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

//		Subscribe Consumer to Topics
		consumer.subscribe(Arrays.asList(FIRST_TOPIC));

//		Poll for new data
		while (true) {  
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				LOGGER.info("Key : " + record .key() + " Value : " + record.value());
				LOGGER.info("Partition : " + record.partition() + " , Offset : " + record.offset());

			}

		}

	}

}
