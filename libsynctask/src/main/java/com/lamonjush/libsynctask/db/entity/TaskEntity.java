package com.lamonjush.libsynctask.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskEntity {

    @PrimaryKey
    public long id;

    public String url;

    public String invocationMethod;

    public String requestHeader;

    public String requestBody;
}
