package com.lamonjush.libsynctask.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lamonjush.libsynctask.db.dao.TaskDao;
import com.lamonjush.libsynctask.db.entity.TaskEntity;

@Database(entities = {TaskEntity.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
}
