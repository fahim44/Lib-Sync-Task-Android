package com.lamonjush.libsynctask.model;

public class RequestHeader {
    private String key, value;

    public RequestHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public RequestHeader setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public RequestHeader setValue(String value) {
        this.value = value;
        return this;
    }
}