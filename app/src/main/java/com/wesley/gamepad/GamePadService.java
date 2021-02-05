package com.wesley.gamepad;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class GamePadService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        BTDeviceManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
