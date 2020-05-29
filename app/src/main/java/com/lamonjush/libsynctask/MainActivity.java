package com.lamonjush.libsynctask;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lamonjush.libsynctask.callback.TaskCompleteCallBack;
import com.lamonjush.libsynctask.callback.TaskEntryListener;
import com.lamonjush.libsynctask.model.InvocationMethod;
import com.lamonjush.libsynctask.model.Task;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*new AddTask(new Task().setUrl("uRl").setInvocationMethod(InvocationMethod.POST),
                new TaskEntryListener() {
                    @Override
                    public void onTaskDone(String response, TaskCompleteCallBack callBack) {
                        Log.d("AddTask", "onTaskDone");
                        callBack.isTaskCompleted(true);
                    }

                    @Override
                    public void onTaskAddedToSyncQueue() {
                        Log.d("AddTask", "task added to sync queue");
                        AddTask.readTask();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("AddTask", e.getMessage());
                    }
                });*/
    }
}
