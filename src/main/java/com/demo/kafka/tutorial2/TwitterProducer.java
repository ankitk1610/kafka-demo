package com.demo.kafka.tutorial2;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {

	String consumerKey = "";
	String consumerSecret = "";
	String token = "";
	String secret = "";
	List<String> terms = Lists.newArrayList("UCL","CWC");

	public TwitterProducer() {
	};

	private void run() {
		// TODO Auto-generated method stub
		// Create a twitter client
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
		Client hosebirdClient = createTwitterClient(msgQueue);
		hosebirdClient.connect();
		// create a Kafka producer

		KafkaProducer<String, String> producer = createKafkaProducer();

		LOGGER.info("Starting... ");

		// loop to send tweets to kafka
		while (!hosebirdClient.isDone()) {
			String msg = null;
			try {
				msg = msgQueue.poll(5, TimeUnit.SECONDS);

			} catch (InterruptedException e) {
				e.printStackTrace();
				hosebirdClient.stop();
			}
			if (msg != null) {
//				LOGGER.info(msg);
				producer.send(new ProducerRecord<String, String>("twitter_tweets", null, msg), new Callback() {

					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (null != exception) {
							LOGGER.error("Something bad happened ", exception);
						}
					}
				});

			}
		}
		LOGGER.info("End of application.");

	}

	private KafkaProducer<String, String> createKafkaProducer() {
		// TODO Auto-generated method stub
		String LOCALHOST = "127.0.0.1:9092";
		String STRING_SERIALIZER = StringSerializer.class.getName();
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, STRING_SERIALIZER);
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, STRING_SERIALIZER);

		// Creating a safe producer
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		return producer;
	}

	public static void main(String[] args) {

		new TwitterProducer().run();
		System.out.println("Heloooo!!!");

	}

	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		/**
		 * Set up your blocking queues: Be sure to size these properly based on expected
		 * TPS of your stream
		 */

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
//		List<Long> followings = Lists.newArrayList(1234L, 566788L);

		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") // optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		Client hosebirdClient = builder.build();
		// Attempts to establish a connection.
		return hosebirdClient;
	}

	private static Logger LOGGER = LoggerFactory.getLogger(TwitterProducer.class);
}
