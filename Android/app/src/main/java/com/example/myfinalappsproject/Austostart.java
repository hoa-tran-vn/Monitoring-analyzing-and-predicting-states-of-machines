package com.example.myfinalappsproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Austostart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                System.out.println("test2");
                WorkerService.enqueueWork(context, new Intent());
                System.out.println("test3");
            }
        }catch (Exception exception){

        }
    }
}
