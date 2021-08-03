package com.example.myfinalappsproject;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class WorkerService extends JobIntentService {
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WorkerService.class, 104501, work);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Intent intents = new Intent(getBaseContext(),MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
    }
}
