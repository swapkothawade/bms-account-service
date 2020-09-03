package com.mybank.bmsaccountservice.dao;

import com.mongodb.client.MongoClient;
import com.mybank.api.config.MongoDBConfiguration;
import com.mybank.api.dao.AccountDao;
import com.mybank.api.domain.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest(classes = {MongoDBConfiguration.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountDaoTest {
    @Autowired
    MongoClient mongoClient;

    @Value("${spring.mongodb.database}")
    String databaseName;

    private AccountDao accountDao;

    @Before
    public void setup() {
        this.accountDao = new AccountDao(mongoClient, databaseName);
    }

    @Test
    public void addAccountTest(){
        long result = this.accountDao.addAccountDetails(getAccountDetail());
        assertNotNull(result);
    }


    @Test
    public void addTransactionDetails(){
        boolean result =this.accountDao.addTransaction(getTransaction());
        assertTrue(result);
    }

    @Test
    public void testgetTransactionDetails(){
        List<AccountTransaction> transactions = this.accountDao.getAccountStatment("1015568897");
        assertNotNull(transactions);
        assertEquals(1,transactions.size());
    }

    private AccountTransaction getTransaction() {
        AccountTransaction transaction = new AccountTransaction();
        transaction.setAccountnumber("1015568897");
        transaction.setTransactionAmount(5000);
        transaction.setBalance(10000);
        transaction.setNewBalance(transaction.getBalance() - transaction.getTransactionAmount());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionDateTime(LocalDateTime.now());
        return transaction;
    }


    private AccountDetails getAccountDetail(){
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountnumber("1015568897");
        accountDetails.setEmail("swapnil.kothawade@gmail.com");
        accountDetails.setPan("1234354567");
        accountDetails.setCommunicationAddress(getAddress());
        accountDetails.setAccountType(AccountType.SAVING);
        accountDetails.setContactnumber("+1-20173688326");
        return accountDetails;
    }

    private Address getAddress() {
        Address communicationAddress = new Address();
        communicationAddress.setStreet("169 Manhattan Avenue");
        communicationAddress.setCity("Jersey City");
        communicationAddress.setState("New Jerssey");
        communicationAddress.setZip("07307");
        return communicationAddress;
    }


}
