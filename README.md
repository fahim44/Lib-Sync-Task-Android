# Lib-Sync-Task-Android

<a href="https://www.buymeacoffee.com/fahim44" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>

[![](https://jitpack.io/v/fahim44/Lib-Sync-Task-Android.svg)](https://jitpack.io/#fahim44/Lib-Sync-Task-Android)

It's a simple helper sync library for Android. 

If your application make server requests and if any disaster happens (ex: no internet/ response failed),
you want to save the request in queue so that you can retry the requests (sync with the server) later time.... This library can help you out.

This library has used `Room` as db-wrapper, `Jackson` for json perser, `okhttp` for network calling

## setup:

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
 repositories {
  maven { url 'https://jitpack.io' }
 }
}
```
Add the dependency in your module's build.gradle
```gradle
dependencies {
 implementation 'com.github.fahim44:Lib-Sync-Task-Android:VERSION'
}
```

## howTo

### Initialization

Initiate the lib in your `Application` class

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SyncTaskLib.getInstance().initiate(getApplicationContext());
    }
}
```
### Add new server request (Task)
First add a task with your data
```java
Task task = new Task()
                .setUrl("https://jsonplaceholder.typicode.com/posts/1")
                .addHeader("Content-Type","application/json")
                .setRequestBody(new Object())
                .setInvocationMethod(InvocationMethod.GET);
 ```

then create an AddTask obj, which will try to execute the request, if it fails, it will save the request in the queue
```java
new AddTask(task, new TaskEntryListener() {
            @Override
            public void onTaskDone(String response, TaskCompleteCallBack callBack) {
                //it will be called when server request successful, response is the server provided response
                //here response must be check for the validation,
                //if the response is the desired output, call 'callBack.isTaskCompleted(true);' to complete the task
                //if the response is not what is desired and it should try again, please call 'callBack.isTaskCompleted(false);'
                //so that the task can be saved in the syncQueue and tried later
                callBack.isTaskCompleted(true);
            }

            @Override
            public void onTaskAddedToSyncQueue() {
                //It is called when task has been failed & saved in the syncQueue to try again later
            }

            @Override
            public void onTaskComplete() {
                //It is called when task is successfully completed
            }

            @Override
            public void onError(Exception e) {
                //It is called when some error occured
            }
        });
```

### Sync previously failed server requests
make a SyncTask object to complete the sync
```java
//If you want to stop the syncing when any task failed, create 'SyncTask' with firstParam= 'true'
//If you want to continue syncing when any task failed, create 'SyncTask' with firstParam= 'false'
new SyncTask(false, new TaskSyncListener() {
            @Override
            public void onTaskDone(Task task, String response, TaskCompleteCallBack callBack) {
                //it will be called when server request successful, response is the server provided response
                //here response must be check for the validation,
                //if the response is the desired output, call 'callBack.isTaskCompleted(true);' to complete the task
                //if the response is not what is desired and it should try again, please call 'callBack.isTaskCompleted(false);'
                //If the 'callBack.isTaskCompleted' is not called the sync will not proceed to the next task.
                callBack.isTaskCompleted(true);
            }

            @Override
            public void onTaskRemovedFromSyncQueue(Task task) {
                //Task is successfully completed and removed from the queue.
            }

            @Override
            public void onTaskFailed(Task task) {
                //Task is failed to complete, so it will stay in the queue for later try
            }

            @Override
            public void onComplete(int totalTaskCount, int completedTaskCount) {
                //he sync proceess is complete.
            }

            @Override
            public void onError(Exception e) {
                //It is called when any exception occured
            }
        });
```
