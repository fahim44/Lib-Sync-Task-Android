package com.lamonjush.libsynctask;

import androidx.annotation.NonNull;

import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTask {

    private static String TAG = "AddTask";

    private ExecutorService service = Executors.newSingleThreadExecutor();

    public AddTask(@NonNull Task task, TaskEntryListener listener) {
        if (checkInitState(listener)
                && isTaskValid(task, listener)) {
            service.submit(() -> {
                if (listener != null) {
                    listener.onTaskDone("", taskCompleted -> {
                        if (taskCompleted) {
                            /*saveTaskInDB(task);
                            listener.onTaskAddedToSyncQueue();*/
                        }
                    });
                }
            });
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
