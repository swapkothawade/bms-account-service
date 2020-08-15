package com.mybank.api.domain;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal");
    private  String value;
    TransactionType(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

}
