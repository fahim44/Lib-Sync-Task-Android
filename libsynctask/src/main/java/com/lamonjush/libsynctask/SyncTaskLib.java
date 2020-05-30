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

    /**
     * init db, it should be called before any <b>AddTask</b> or <b>SyncTask</b> operation
     * @param applicationContext Android context to init db and check network access
     * @param packageName to create unique db name
     */
    public void initiate(Context applicationContext) {
        context = applicationContext.getApplicationContext();
        TaskDatabase database = Room.databaseBuilder(applicationContext.getApplicationContext(),
                TaskDatabase.class,
                applicationContext.getApplicationContext().getPackageName() + SyncTaskLib.class.getName())
                .build();
        taskDao = database.taskDao();
    }

    /**
     * check if device is connected to any network
     * @return
     */
    boolean isNetworkConnected() {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            }
        }
        return false;
    }

    /**
     * check if the connected network has internet access,
     * should not be run on UI/Main thread, it will cause the exception to be called
     * @return
     */
    @WorkerThread
    boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals(""); //FIXME: replace "" with something meaningful

        } catch (Exception e) {
            return false;
        }
    }
}