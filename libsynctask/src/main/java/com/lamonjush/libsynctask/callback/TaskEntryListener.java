package com.lamonjush.libsynctask.callback;

public interface TaskEntryListener extends TaskListener{

    /**
     * call when server call successful, ask user whatever the response is valid,
     * if response valid, remove from the db and call <b>onTaskRemovedFromSyncQueue</b>
     * if response invalid, call <b>onTaskFailed</b>
     * <p>
     * if user set <b>SyncTask.stopOnSingleTaskFailure = true;</b> and response invalid, call <b>onComplete</b>
     **/
    void onTaskDone(String response, TaskCompleteCallBack callBack);

    /**
     * task completion failed and it is added in the db to try again later via sync operation
     */
    void onTaskAddedToSyncQueue();

    /**
     * server response is valid and task completed successfully.
     */
    void onTaskComplete();
}