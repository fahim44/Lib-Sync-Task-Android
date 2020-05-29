package com.lamonjush.libsynctask;

import android.content.Context;

import androidx.room.Room;

import com.lamonjush.libsynctask.db.TaskDatabase;
import com.lamonjush.libsynctask.db.dao.TaskDao;

public class SyncTaskLib {

    static TaskDao taskDao;

    public static void initiate(Context applicationContext, String packageName) {
        TaskDatabase database = Room.databaseBuilder(applicationContext.getApplicationContext(),
                TaskDatabase.class,
                packageName + SyncTaskLib.class.getName())
                .build();
        taskDao = database.taskDao();
    }


}
