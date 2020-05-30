package com.lamonjush.libsynctask.callback;

public interface TaskCompleteCallBack {

    /**
     * When any task's server call success, ask user if the response is the valid response which he was asking.
     * If it is valid call it with <b>true</b>
     * If response is invalid, call it with <b>false</b>
     *
     * If response is valid, task will not insert in db or will be deleted from db
     *
     * If response is invalid, task will be added in db
     * @param taskCompleted
     */
    void isTaskCompleted(boolean taskCompleted);
}