package com.mybank.api.domain;

public enum AccountType {
    SAVING("Saving"),
    CHECKING("Checking");
    private String value;
    AccountType(String value){
        this.value = value;
    }
    private String getValue(){
        return this.value;
    }
}
