package com.lamonjush.libsynctask;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.model.RequestHeader;
import com.lamonjush.libsynctask.model.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddTask {

    public AddTask(@NonNull Task task, TaskEntryListener listener) {
        //check lib init and task validation
        if (checkInitState(listener)
                && isTaskValid(task, listener)) {

            ExecutorService service = Executors.newSingleThreadExecutor();

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
                    if (!SyncTaskLib.getInstance().isInternetAvailable()) {
                        addTaskToLocalDB(task, listener);
                    }
                    //else try to call the server, if server call failed save the task in the db & update the listener
                    //if server call success, let user check response, user will notify via `TaskCompleteCallBack` whatever
                    //task is successful or not, if not successful save the task in the db & update the listener.
                    else {
                        String response = makeServerRequest(task);
                        //server call failed
                        if (response == null){
                            addTaskToLocalDB(task, listener);
                        }
                        //server call successful
                        else {
                            //ask user for task successful verification
                            if (listener != null) {
                                listener.onTaskDone(response, taskCompleted -> {
                                    //task is not successful, save it to local db & update listener
                                    if (!taskCompleted) {
                                        listener.onTaskAddedToSyncQueue();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    //return null if request failed
    private String makeServerRequest(@NonNull Task task) {
        OkHttpClient client = new OkHttpClient();
        Request request = createRequest(task);
        try (Response response = client.newCall(request).execute()) {
            if (response != null && response.body() != null) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @NotNull
    private Request createRequest(@NonNull Task task) {
        Request.Builder requestBuilder = new Request.Builder();
        //add url
        requestBuilder = requestBuilder.url(task.getUrl());
        //method setup with request body
        switch (task.getInvocationMethod()) {
            case GET:
                requestBuilder = requestBuilder.get();
                break;
            case DELETE:
                requestBuilder = requestBuilder.delete();
                break;
            case POST:
                RequestBody body = getRequestBody("");
                if (task.getRequestBody() != null) {
                    body = getRequestBody(task.getRequestBody());
                }
                requestBuilder = requestBuilder.post(body);
                break;
            case PUT:
                RequestBody body1 = getRequestBody("");
                if (task.getRequestBody() != null) {
                    body1 = getRequestBody(task.getRequestBody());
                }
                requestBuilder = requestBuilder.put(body1);
                break;
        }
        //add header
        if (task.getHeaders() != null) {
            for (RequestHeader header : task.getHeaders()) {
                requestBuilder = requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        return requestBuilder.build();
    }

    @NotNull
    private RequestBody getRequestBody(String body) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        return RequestBody.create(body, JSON);
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