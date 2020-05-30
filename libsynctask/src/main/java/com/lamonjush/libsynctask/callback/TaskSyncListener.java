package com.lamonjush.libsynctask.callback;

import com.lamonjush.libsynctask.model.Task;

public interface TaskSyncListener {

    /**
     * call when server call successful, ask user whatever the response is valid,
     * if response valid, remove from the db and call <b>onTaskRemovedFromSyncQueue</b>
     * if response invalid, call <b>onTaskFailed</b>
     * <p>
     * if user set <b>SyncTask.stopOnSingleTaskFailure = true;</b> and response invalid, call <b>onComplete</b>
     **/
    void onTaskDone(Task task, String response, TaskCompleteCallBack callBack);

    /**
     * remove task from db when it is successfully done
     *
     * @param task to be remove
     */
    void onTaskRemovedFromSyncQueue(Task task);

    /**
     * task failed to complete,
     * task can be failed if the server call failed, or response is invalid
     *
     * @param task which is failed
     */
    void onTaskFailed(Task task);

    /**
     * sync is done
     *
     * @param totalTaskCount     total task count what was put for sync
     * @param completedTaskCount completed task count
     */
    void onComplete(int totalTaskCount, int completedTaskCount);

    /**
     * sync cannot be done, the reason might be->
     * not internet access
     * <b>SyncTaskLib</b> not initialize
     *
     * @param e the reason
     */
    void onError(Exception e);
}