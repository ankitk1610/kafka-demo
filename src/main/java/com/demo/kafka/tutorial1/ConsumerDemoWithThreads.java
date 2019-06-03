package com.demo.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoWithThreads {
	private static final String FIRST_TOPIC = "first_topic";
	private static final String MY_FOURTH_APP = "my-fourth-app";
	private static final String DESERIALIZER = StringDeserializer.class.getName();
	private static final String LOCALHOST = "127.0.0.1:9092";
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoWithThreads.class);

	public static void main(String[] args) {

		new ConsumerDemoWithThreads().run();

	}

	private ConsumerDemoWithThreads() {
	}

	private void run() {
//		create config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, MY_FOURTH_APP);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		CountDownLatch latch = new CountDownLatch(1);

//		Create consumer
		LOGGER.info("Creating Consumer Thread");
		Runnable myConsumerRunnable = new ConsumerRunnable(LOCALHOST, MY_FOURTH_APP, FIRST_TOPIC, latch);

		Thread myThread = new Thread(myConsumerRunnable);
		myThread.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOGGER.info("Caught shutdown hook.");
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((ConsumerRunnable) myConsumerRunnable).shutDown();
		})
		);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Application is interrupted.");
		} finally {
			LOGGER.info("Application is closing.");
		}
//		Subscribe Consumer to Topics

	}
}

class ConsumerRunnable implements Runnable {

	private CountDownLatch latch; 
	KafkaConsumer<String, String> consumer;
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRunnable.class);

	public ConsumerRunnable(String bootsrapServers, String groupId, String topic, CountDownLatch latch) {
		this.latch = latch;
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootsrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumer = new KafkaConsumer<String, String>(properties);
		consumer.subscribe(Arrays.asList(topic));

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
//			Poll for new data
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> record : records) {
					LOGGER.info("Key : " + record.key() + " Value : " + record.value());
					LOGGER.info("Partition : " + record.partition() + " , Offset : " + record.offset());

				}

			}
		} catch (WakeupException e) {
			LOGGER.info("Recieved Shutdown signal");
		} finally {
			consumer.close();
//			 tells main code that we are done with the consumer
			latch.countDown();
		}

	}

	public void shutDown() {
//		wakeup is a method to interrupt .poll()
//		throws wakeUpException
		consumer.wakeup();
	}

}
