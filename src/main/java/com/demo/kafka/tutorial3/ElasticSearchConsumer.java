package com.demo.kafka.tutorial3;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

public class ElasticSearchConsumer {

	private static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConsumer.class);

	public static RestHighLevelClient createClient() {

		
//		Username:password@hostname
		String hostname = "";
		String username = "";
		String password = "";

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostname, 443, "https"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						// TODO Auto-generated method stub
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				});

		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
		return client;

	}

	public static KafkaConsumer<String, String> createConsumer(String topic) {

//		create config
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-demo-elastic-search");
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

//		Create consumer 

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

//		Subscribe Consumer to Topics
		consumer.subscribe(Arrays.asList(topic));

		return consumer;
	}

	public static void main(String[] args) throws IOException {
		RestHighLevelClient client = createClient();
//		String jsonString = " { \"foo\" : \"bar\" }";
//		IndexRequest indexRequest = new IndexRequest("twitter", "_docs").source(jsonString, XContentType.JSON);

		KafkaConsumer<String, String> consumer = createConsumer("twitter_tweets");

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			LOGGER.info("Recieved " + records.count() + " records");

			BulkRequest bulkRequest = new BulkRequest();

			for (ConsumerRecord<String, String> record : records) {

				// 2 strategies to make consumer idempotent

				// 1. : Kafka Generic
//				String id = record.topic() + "_" + record.partition() + "_" + record.offset();

				// 2.twitter feed specific id
				String tweetId = extractIdFromTweet(record.value());

				LOGGER.info("Key : " + record.key() + " Value : " + record.value());
				LOGGER.info("Partition : " + record.partition() + " , Offset : " + record.offset());

				String jsonString = record.value();

				IndexRequest indexRequest = new IndexRequest("twitter", "tweet", tweetId).source(jsonString,
						XContentType.JSON);

				bulkRequest.add(indexRequest);
				BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
				
				
//				IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

				
				
				
				
//				String id = indexResponse.getId();
				LOGGER.info("Tweet ID : " + tweetId);
//				LOGGER.info("Index Response ID : " + indexResponse.getId());
//			try {
//					Thread.sleep(10 * 1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			LOGGER.info("Committing offsets...");
			consumer.commitSync();
			LOGGER.info("Offset committed...");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static JsonParser jsonParser = new JsonParser();

	private static String extractIdFromTweet(String tweet) {

		return jsonParser.parse(tweet).getAsJsonObject().get("id_str").getAsString();

	}

}
