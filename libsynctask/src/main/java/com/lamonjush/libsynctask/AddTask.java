package com.lamonjush.libsynctask;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTask {

    private static String TAG = "AddTask";

    private ExecutorService service = Executors.newSingleThreadExecutor();


    public AddTask(@NonNull Task task, TaskEntryListener listener) {
        //check lib init and task validation
        if (checkInitState(listener)
                && isTaskValid(task, listener)) {

            //if network not connected, save the task in the db & update the listener
            if (!SyncTaskLib.getInstance().isNetworkConnected()) {
                service.submit(() -> {
                    addTaskToLocalDB(task, listener);
                });
            }
            //else check internet availability
            else {
                service.submit(() -> {
                    //if internet not connected,save the task in the db & update the listener
                    if(!SyncTaskLib.getInstance().isInternetAvailable()){
                        addTaskToLocalDB(task, listener);
                    }
                    //else try to call the server
                });
            }


            /*service.submit(() -> {
                Log.d(TAG, "isInternetAvailable : " + SyncTaskLib.getInstance().isInternetAvailable());
                if (listener != null) {
                    listener.onTaskDone("", taskCompleted -> {
                        if (taskCompleted) {
                            *//*
                            listener.onTaskAddedToSyncQueue();*//*
                        }
                    });
                }
            });*/
        }
    }

    private void addTaskToLocalDB(@NonNull Task task, TaskEntryListener listener) {
        saveTaskInDB(task);
        if (listener != null) {
            new Handler(Looper.getMainLooper())
                    .post(listener::onTaskAddedToSyncQueue);
        }
    }

    private boolean checkInitState(TaskEntryListener listener) {
        if (SyncTaskLib.taskDao == null) {
            if (listener != null) {
                listener.onError(new Exception("Sync Task not initialized"));
            }
            return false;
        }
        return true;
    }

    private boolean isTaskValid(@NonNull Task task, TaskEntryListener listener) {
        if (task.getUrl() == null || task.getUrl().equals("")) {
            if (listener != null) {
                listener.onError(new Exception("Task url cannot be null"));
            }
            return false;
        } else if (task.getInvocationMethod() == null) {
            if (listener != null) {
                listener.onError(new Exception("invocation method cannot be null"));
            }
            return false;
        }
        return true;
    }

    private void saveTaskInDB(@NonNull Task task) {
        SyncTaskLib.taskDao.insert(task.getTaskEntity());
    }
}
