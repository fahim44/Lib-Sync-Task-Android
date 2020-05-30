package com.lamonjush.libsynctask.callback;

public interface TaskListener {

    /**
     * task completing or sync task cannot be done, the reason might be->
     * not internet access
     * <b>SyncTaskLib</b> not initialize
     * task url, invocationMethod are invalid (for {@link com.lamonjush.libsynctask.AddTask})
     *
     * @param e the reason
     */
    void onError(Exception e);
}
