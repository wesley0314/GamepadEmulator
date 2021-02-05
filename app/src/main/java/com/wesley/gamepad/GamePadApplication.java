package com.wesley.gamepad;

import android.app.Application;
import android.content.Intent;

public class GamePadApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startGamePadService();
    }

    private void startGamePadService() {
        Intent intent = new Intent();
        intent.setClass(this, GamePadService.class);
        startService(intent);
    }
}
