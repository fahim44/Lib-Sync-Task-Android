package com.lamonjush.libsynctask;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTask {

    public AddTask(@NonNull Task task, TaskEntryListener listener) {
        //check lib init and task validation
        if (Helper.checkInitState(listener)
                && isTaskValid(task, listener)) {

            ExecutorService service = Executors.newSingleThreadExecutor();

            //if network not connected, save the task in the db & update the listener
            if (!SyncTaskLib.getInstance().isNetworkConnected()) {
                service.submit(() -> addTaskToLocalDB(task, listener));
            }
            //else check internet availability
            else {
                service.submit(() -> {
                    //if internet not connected,save the task in the db & update the listener
                    if (!SyncTaskLib.getInstance().isInternetAvailable()) {
                        addTaskToLocalDB(task, listener);
                    }
                    //else try to call the server, if server call failed save the task in the db & update the listener
                    //if server call success, let user check response, user will notify via `TaskCompleteCallBack` whatever
                    //task is successful or not, if not successful save the task in the db & update the listener.
                    else {
                        String response = Helper.makeServerRequest(task);
                        //server call failed
                        if (response == null) {
                            addTaskToLocalDB(task, listener);
                        }
                        //server call successful
                        else {
                            //ask user for task successful verification
                            if (listener != null) {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        listener.onTaskDone(response, taskCompleted ->
                                                service.submit(() -> {
                                                    //task is not successful, save it to local db & update listener
                                                    if (!taskCompleted) {
                                                        addTaskToLocalDB(task, listener);
                                                    }
                                                    //task is successful, update listener
                                                    else {
                                                        new Handler(Looper.getMainLooper())
                                                                .post(listener::onTaskComplete);
                                                    }
                                                })));
                            }
                        }
                    }
                });
            }
        }
    }

    private void addTaskToLocalDB(@NonNull Task task, TaskEntryListener listener) {
        Helper.saveTaskInDB(task);
        if (listener != null) {
            new Handler(Looper.getMainLooper())
                    .post(listener::onTaskAddedToSyncQueue);
        }
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
}