package com.lamonjush.libsynctask.callback;

public interface TaskEntryListener {

    void onTaskDone(String response, TaskCompleteCallBack callBack);

    void onTaskAddedToSyncQueue();

    void onError(Exception e);
}