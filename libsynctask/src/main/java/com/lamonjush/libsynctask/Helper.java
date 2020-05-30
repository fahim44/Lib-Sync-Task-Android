package com.lamonjush.libsynctask;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.lamonjush.libsynctask.callback.TaskListener;
import com.lamonjush.libsynctask.db.entity.TaskEntity;
import com.lamonjush.libsynctask.model.RequestHeader;
import com.lamonjush.libsynctask.model.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Helper {
    //return null if request failed
    static String makeServerRequest(@NonNull Task task) {
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
    private static Request createRequest(@NonNull Task task) {
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
    private static RequestBody getRequestBody(String body) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        return RequestBody.create(body, JSON);
    }

    @WorkerThread
    static void saveTaskInDB(@NonNull Task task) {
        SyncTaskLib.taskDao.insert(task.getTaskEntity());
    }

    @WorkerThread
    static List<Task> getTasksFromDB() {
        List<TaskEntity> entities = SyncTaskLib.taskDao.getAll();
        if (entities == null) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        for (TaskEntity entity : entities) {
            tasks.add(Task.fromTaskEntity(entity));
        }
        return tasks;
    }

    @WorkerThread
    static void deleteTaskFromDB(@NonNull Task task) {
        SyncTaskLib.taskDao.delete(task.getTaskEntity());
    }

    /**
     * check whatever {@link SyncTaskLib}'s init is called and db is setup or not
     * if not setup done, call listener.onError
     */
    static boolean checkInitState(TaskListener listener) {
        if (SyncTaskLib.taskDao == null) {
            if (listener != null) {
                listener.onError(new Exception("Sync Task not initialized"));
            }
            return false;
        }
        return true;
    }
}