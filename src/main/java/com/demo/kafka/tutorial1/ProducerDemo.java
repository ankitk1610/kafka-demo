package com.demo.kafka.tutorial1;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * 
 * @author Ankit Kumar
 * @since April 28th, 2019
 *
 */
public class ProducerDemo {

private static final String FIRST_TOPIC = "first_topic";
//	private static final String VALUE_SERIALIZER = "value.serializer";
//	private static final String KEY_SERIALIZER = "key.serializer";
//	private static final String BOOTSTRAP_SERVERS = "bootstrap-servers";

	private static final String STRING_SERIALIZER = StringSerializer.class.getName();
	private static final String LOCALHOST = "127.0.0.1:9092";
	
	public static void main(String[] args) {
//		System.out.println("Hello World!");

//		 STEP 1 : Create Producer Properties
		
		Properties properties = new Properties();
		
//		OLD WAY
//		properties.setProperty(BOOTSTRAP_SERVERS, LOCALHOST);
//		properties.setProperty(KEY_SERIALIZER, StringSerializer.class.getName());
//		properties.setProperty(VALUE_SERIALIZER, StringSerializer.class.getName()); //Helps to know what type of value you send to Kafka and how will this be serialized to bytes
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, STRING_SERIALIZER);
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, STRING_SERIALIZER);
		
		

//		STEP 2: Create the Producer
		
		KafkaProducer<String, String> producer = new KafkaProducer(properties);
		
//		STEP 3: Create a Producer Record
		
		ProducerRecord<String, String> producerRecord = new ProducerRecord(FIRST_TOPIC, "Hello World!");
		
		

//		STEP 4 : Send Data
		
		producer.send(producerRecord);
		producer.flush();
		producer.close();

	}
}
