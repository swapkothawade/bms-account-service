package com.mybank.api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@Component
@RefreshScope
public abstract class AbstractBMSDao {

    protected final String MYBANK_DATABASE;
    protected MongoDatabase db;
    protected MongoClient mongoClient;
    @Value("${spring.mongodb.uri}")
    private String connectionString;

    protected AbstractBMSDao(MongoClient mongoClient, String databaseName) {
    	System.out.println("****** from abstract dao " + connectionString);
        this.mongoClient = mongoClient;
        MYBANK_DATABASE = databaseName;
        this.db = this.mongoClient.getDatabase(MYBANK_DATABASE);
    }

    public ObjectId generateObjectId() {
        return new ObjectId();
    }

    public Map<String, Object> getConfiguration() {
        ConnectionString connString = new ConnectionString(connectionString);
        Bson command = new Document("connectionStatus", 1);
        Document connectionStatus = this.mongoClient.getDatabase(MYBANK_DATABASE).runCommand(command);

        List authUserRoles =
                ((Document) connectionStatus.get("authInfo")).get("authenticatedUserRoles", List.class);

        Map<String, Object> configuration = new HashMap<>();

        if (!authUserRoles.isEmpty()) {
            configuration.put("role", ((Document) authUserRoles.get(0)).getString(
                    "role"));
            configuration.put("pool_size", connString.getMaxConnectionPoolSize());
            configuration.put(
                    "wtimeout",
                    this.mongoClient
                            .getDatabase(MYBANK_DATABASE)
                            .getWriteConcern()
                            .getWTimeout(TimeUnit.MILLISECONDS));
        }
        return configuration;
    }
}
