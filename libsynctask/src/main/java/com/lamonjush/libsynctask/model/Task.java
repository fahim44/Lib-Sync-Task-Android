package com.lamonjush.libsynctask.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamonjush.libsynctask.db.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class Task {

    private long id;

    private String url;

    private InvocationMethod invocationMethod;

    private String requestBody;

    private List<RequestHeader> headers;

    public void setRequestBody(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            requestBody = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Task addHeader(String key, String value) {
        if (headers == null)
            headers = new ArrayList<>();
        headers.add(new RequestHeader(key, value));
        return this;
    }

    public long getId() {
        return id;
    }

    public Task setId(long id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Task setUrl(String url) {
        this.url = url;
        return this;
    }

    public InvocationMethod getInvocationMethod() {
        return invocationMethod;
    }

    public Task setInvocationMethod(InvocationMethod invocationMethod) {
        this.invocationMethod = invocationMethod;
        return this;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Task setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public List<RequestHeader> getHeaders() {
        return headers;
    }

    public Task setHeaders(List<RequestHeader> headers) {
        this.headers = headers;
        return this;
    }

    @JsonIgnore
    public TaskEntity getTaskEntity() {
        ObjectMapper mapper = new ObjectMapper();

        TaskEntity entity = new TaskEntity();
        entity.url = url;
        entity.invocationMethod = invocationMethod.name();
        entity.requestBody = requestBody;

        if (headers != null) {
            try {
                entity.requestHeader = mapper.writeValueAsString(headers);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
}