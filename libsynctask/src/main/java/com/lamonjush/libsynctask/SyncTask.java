package com.lamonjush.libsynctask;

import android.os.Handler;
import android.os.Looper;

import com.lamonjush.libsynctask.callback.TaskSyncListener;
import com.lamonjush.libsynctask.model.Task;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncTask {

    /**
     * if it is true, sync will stop if any task completion failed.
     * if it is false, sync will continue whatever any task completion failed
     */
    private AtomicBoolean stopOnAnyTaskFailure = new AtomicBoolean(false);
    private AtomicInteger totalTaskCount = new AtomicInteger(0);
    private AtomicInteger completedTaskCount = new AtomicInteger(0);

    private ExecutorService service = Executors.newSingleThreadExecutor();

    public SyncTask(boolean stopOnAnyTaskFailure, TaskSyncListener listener) {
        this.stopOnAnyTaskFailure = new AtomicBoolean(stopOnAnyTaskFailure);
        //check lib init
        if (Helper.checkInitState(listener)) {
            //if network not connected, call listener.onError
            if (!SyncTaskLib.getInstance().isNetworkConnected()) {
                if (listener != null) {
                    listener.onError(new Exception("No network is connected"));
                }
            } else {
                service.submit(() -> {
                    //if internet not available call listener.onError
                    if (!SyncTaskLib.getInstance().isInternetAvailable()) {
                        listener.onError(new Exception("No internet is available"));
                    } else {
                        List<Task> tasks = Helper.getTasksFromDB();
                        totalTaskCount.set(tasks.size());
                        Iterator<Task> iterator = tasks.iterator();
                        iterateThroughTasks(iterator, listener);
                    }
                });
            }
        }
    }

    private void iterateThroughTasks(Iterator<Task> iterator, TaskSyncListener listener) {
        if (iterator.hasNext()) {
            Task task = iterator.next();
            String response = Helper.makeServerRequest(task);
            //check response is inValid call 'onTaskCompletionFailed'
            if (response == null) {
                onTaskCompletionFailed(iterator, listener, task);
            }
            //else response is valid, ask user for verification
            else {
                //if listener is not null, ask for user verification
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            listener.onTaskDone(task, response, taskCompleted ->
                                    service.submit(() -> {
                                        //taskCompletion is success, remove task from db and call listener.onTaskRemovedFromSyncQueue
                                        if (taskCompleted) {
                                            Helper.deleteTaskFromDB(task);
                                            completedTaskCount.incrementAndGet();
                                            new Handler(Looper.getMainLooper())
                                                    .post(() -> listener.onTaskRemovedFromSyncQueue(task));
                                            iterateThroughTasks(iterator, listener);
                                        }
                                        //task completion failed
                                        else {
                                            onTaskCompletionFailed(iterator, listener, task);
                                        }
                                    })));
                }
                //listener is null, assume task completed successfully
                else {
                    //delete task from db
                    Helper.deleteTaskFromDB(task);
                    completedTaskCount.incrementAndGet();
                    iterateThroughTasks(iterator, null);
                }
            }
        } else {
            //no item left, call sync complete
            callOnComplete(listener);
        }
    }

    private void onTaskCompletionFailed(Iterator<Task> iterator, TaskSyncListener listener, Task task) {
        //task completion failed, call 'onTaskFailed'
        if (listener != null) {
            new Handler(Looper.getMainLooper())
                    .post(() -> listener.onTaskFailed(task));
        }
        //if stopOnAnyTaskFailure = true, call onComplete
        if (stopOnAnyTaskFailure.get()) {
            callOnComplete(listener);
        }
        //else move to next item
        else {
            iterateThroughTasks(iterator, listener);
        }
    }

    private void callOnComplete(TaskSyncListener listener) {
        if (listener != null) {
            new Handler(Looper.getMainLooper())
                    .post(() -> listener.onComplete(totalTaskCount.get(), completedTaskCount.get()));
        }
    }
}