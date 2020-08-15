package com.mybank.api.dao;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mybank.api.domain.AccountDetails;
import com.mybank.api.domain.AccountTransaction;
import com.mybank.api.exception.CustomException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.Document;
@Component
public class AccountDao extends AbstractBMSDao{


    private final Logger log;
    private final MongoCollection<AccountDetails> accountDetailColllection;
    private final MongoCollection<AccountTransaction> transactionCollection;

    @Autowired
    public AccountDao(
            MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
        super(mongoClient, databaseName);
        CodecRegistry pojoCodecRegistry =
                fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        accountDetailColllection = db.getCollection("account_detail", AccountDetails.class).withCodecRegistry(pojoCodecRegistry);
        transactionCollection = db.getCollection("account_transaction", AccountTransaction.class).withCodecRegistry(pojoCodecRegistry);
        log = LoggerFactory.getLogger(this.getClass());

    }

    public AccountDetails getAccountDetail(String accountnumber){
        Bson accountFilter = eq("accountnumber",accountnumber);
       try {
           return accountDetailColllection.find(accountFilter).first();
       }catch(MongoException exception){
           throw new CustomException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    public boolean addAccountDetails(AccountDetails accountDetail) {
       try {
           accountDetailColllection.insertOne(accountDetail);
           return true;
       }catch(MongoException exception){
           throw new CustomException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }

    public boolean addTransaction(AccountTransaction transaction) {
        try{
            transactionCollection.insertOne(transaction);
            return true;
        }catch(MongoException exception){
            throw new CustomException(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // use of Method reference
    public List<AccountTransaction> getAccountStatment(String accountnumber) {
//        Bson transactionfilter = new Document("accountnumber",accountnumber).append("sort",new Document("transactionDateTime",-1L));
        Bson transactionfilter = new Document("accountnumber",accountnumber);
        Iterator<AccountTransaction> transactionItr = transactionCollection.find(transactionfilter,AccountTransaction.class) .iterator();
        List<AccountTransaction> transactions = new ArrayList<>();
        transactionItr.forEachRemaining(transactions::add);
        return transactions;
    }
}
