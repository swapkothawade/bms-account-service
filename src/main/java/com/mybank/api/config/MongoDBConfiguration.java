package com.mybank.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@Service
@RefreshScope
public class MongoDBConfiguration {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public MongoClient mongoClient(@Value("${spring.mongodb.uri}") String connectionString) {
		log.info("Mongo DB Connection URL " + connectionString);
		MongoClient mongoClient = MongoClients.create(connectionString);
		return mongoClient;
	}
}
