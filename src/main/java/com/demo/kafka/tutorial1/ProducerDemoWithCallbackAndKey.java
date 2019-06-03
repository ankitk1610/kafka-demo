package com.demo.kafka.tutorial1;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.metrics.stats.Meter;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ankit Kumar
 * @since April 28th, 2019
 *
 */
public class ProducerDemoWithCallbackAndKey {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithCallbackAndKey.class);

	private static final String FIRST_TOPIC = "first_topic";
//	private static final String VALUE_SERIALIZER = "value.serializer";
//	private static final String KEY_SERIALIZER = "key.serializer";
//	private static final String BOOTSTRAP_SERVERS = "bootstrap-servers";

	private static final String STRING_SERIALIZER = StringSerializer.class.getName();
	private static final String LOCALHOST = "127.0.0.1:9092";

	public static void main(String[] args) throws InterruptedException, ExecutionException {
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

		for(int i = 0; i < 10; i++) {
		
			String key = "ID_" + Integer.toString(i);
			
		ProducerRecord<String, String> producerRecord = new ProducerRecord(FIRST_TOPIC,key ,"Hello World!" + Integer.toString(i));

		LOGGER.info("Key : " + key);
		
		// Partition 0 : 3,4,0
		//Partition 1 :1,6,8 
		//Partition 2 : 2,5,7,9
//		STEP 4 : Send Data Async

		producer.send(producerRecord, new Callback() {

			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				// executes everytime data is successfully sent of exception is thrown.
				if (null == exception) {
					LOGGER.info("Recieved new data. \n" + "Topic : " + metadata.topic() + "\n" + "Partition : "
							+ metadata.partition() + "\n" + "Offsets : " + metadata.offset() + "\n" + "Timestamp : "
							+ metadata.timestamp());

				}
				else  {
					LOGGER.error("Error while producing : " , exception);
				}

			}
		}).get(); //Block send to make it synchronous
		}
		producer.flush();
		producer.close();

	}
}
