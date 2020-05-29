package com.lamonjush.libsynctask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.WorkerThread;
import androidx.room.Room;

import com.lamonjush.libsynctask.db.TaskDatabase;
import com.lamonjush.libsynctask.db.dao.TaskDao;

import java.net.InetAddress;

public class SyncTaskLib {

    @SuppressLint("StaticFieldLeak")
    private static SyncTaskLib syncTaskLib;

    private SyncTaskLib() {
    }

    public static SyncTaskLib getInstance() {
        if (syncTaskLib == null) {
            synchronized (SyncTaskLib.class) {
                if (syncTaskLib == null) {
                    syncTaskLib = new SyncTaskLib();
                }
            }
        }
        return syncTaskLib;
    }

    static TaskDao taskDao;

    private Context context;

    public void initiate(Context applicationContext, String packageName) {
        context = applicationContext.getApplicationContext();
        TaskDatabase database = Room.databaseBuilder(applicationContext.getApplicationContext(),
                TaskDatabase.class,
                packageName + SyncTaskLib.class.getName())
                .build();
        taskDao = database.taskDao();
    }

    boolean isNetworkConnected() {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            }
        }
        return false;
    }

    @WorkerThread
    boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}