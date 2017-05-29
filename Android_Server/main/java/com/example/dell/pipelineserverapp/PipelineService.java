package com.example.dell.pipelineserverapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PipelineService extends Service {
    private Thread myTask;
    PipelineServer pipelineTask;

    public PipelineService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pipelineTask=new PipelineServer();
        myTask=new Thread(pipelineTask);
        myTask.start();
        Log.i("Service", "service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        pipelineTask.isDone=true;
        Log.i("PipelineService:", "onDestroy");
        super.onDestroy();
    }
}
