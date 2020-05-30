package com.lamonjush.libsynctask;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lamonjush.libsynctask.callback.TaskCompleteCallBack;
import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.callback.TaskSyncListener;
import com.lamonjush.libsynctask.model.InvocationMethod;
import com.lamonjush.libsynctask.model.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.logTextView)
    TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        logTextView.setText("");
    }

    private void setLog(String text) {
        logTextView.setText(logTextView.getText().toString()
                + "\n"
                + text);
    }

    @OnClick(R.id.successButton)
    void successButtonClick() {
        Task task = new Task()
                .setUrl("https://jsonplaceholder.typicode.com/posts/1")
                .addHeader("Content-Type","application/json")
                .setRequestBody(new Object())
                .setInvocationMethod(InvocationMethod.GET);
        new AddTask(task, new TaskEntryListener() {
            @Override
            public void onTaskDone(String response, TaskCompleteCallBack callBack) {
                setLog("onTaskDone--> " + response);
                callBack.isTaskCompleted(true);
            }

            @Override
            public void onTaskAddedToSyncQueue() {
                setLog("onTaskAddedToSyncQueue-->");
            }

            @Override
            public void onTaskComplete() {
                setLog("onTaskComplete-->");
            }

            @Override
            public void onError(Exception e) {
                setLog("onError--> " + e.getMessage());
            }
        });
    }

    @OnClick(R.id.failedButton)
    void failedButtonClick() {
        Task task = new Task()
                .setUrl("https://jsonplaceholder.typicode.com/posts/2")
                .setInvocationMethod(InvocationMethod.GET);
        new AddTask(task, new TaskEntryListener() {
            @Override
            public void onTaskDone(String response, TaskCompleteCallBack callBack) {
                setLog("onTaskDone--> " + response);
                callBack.isTaskCompleted(false);
            }

            @Override
            public void onTaskAddedToSyncQueue() {
                setLog("onTaskAddedToSyncQueue-->");
            }

            @Override
            public void onTaskComplete() {
                setLog("onTaskComplete-->");
            }

            @Override
            public void onError(Exception e) {
                setLog("onError--> " + e.getMessage());
            }
        });
    }

    @OnClick(R.id.syncButton)
    void syncButtonClick() {
        new SyncTask(false, new TaskSyncListener() {
            @Override
            public void onTaskDone(Task task, String response, TaskCompleteCallBack callBack) {
                setLog("onTaskDone--> " + response);
                callBack.isTaskCompleted(true);
            }

            @Override
            public void onTaskRemovedFromSyncQueue(Task task) {
                setLog("onTaskRemovedFromSyncQueue--> " + task.getUrl());
            }

            @Override
            public void onTaskFailed(Task task) {
                setLog("onTaskFailed--> " + task.getUrl());
            }

            @Override
            public void onComplete(int totalTaskCount, int completedTaskCount) {
                setLog("onComplete--> totalTaskCount: " + totalTaskCount + " completedTaskCount: " + completedTaskCount);
            }

            @Override
            public void onError(Exception e) {
                setLog("onError--> " + e.getMessage());
            }
        });
    }
}
