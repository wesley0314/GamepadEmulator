package com.wesley.gamepad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Range;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pateo.wesley.gamepad.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private BTDeviceManager mBTManager;
    private ImageButton[] buttons;
    private TextView status;
    private JoystickView joystick;
    private Vibrator vibrator;
    static int reportIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBTManager = BTDeviceManager.getInstance(getApplicationContext());
        mBTManager.setConnectStateListener(state -> mHandler.post(() -> updateConnectionState(state)));
        vibrator = getSystemService(Vibrator.class);
        initView();
    }

    private void initView() {
        status = findViewById(R.id.status);
        initButton();
        initJoystick();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initButton() {
        buttons = new ImageButton[]{
                findViewById(R.id.button_1),
                findViewById(R.id.button_2),
                findViewById(R.id.button_3),
                findViewById(R.id.button_4),
        };
        for (ImageButton button : buttons) {
            button.setTag(R.id.tag_pressed, false);
            button.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Do something
                        if(!(boolean) v.getTag(R.id.tag_pressed)) {
                            vibrator.vibrate(VibrationEffect.createOneShot(40, 50));
                        }
                        v.setTag(R.id.tag_pressed, true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // No longer down
                        v.setTag(R.id.tag_pressed, false);
                        break;
                    default:
                        return false;
                }
                sendReport();
                return false;
            });
        }
    }

    private void initJoystick() {
        joystick = findViewById(R.id.joystick);
        joystick.setTag(R.id.tag_x_pos, 0.0);
        joystick.setTag(R.id.tag_y_pos, 0.0);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                double rad = Math.toRadians(angle);
                double dist = strength / 100.0;
                joystick.setTag(R.id.tag_x_pos, dist * Math.cos(rad));
                joystick.setTag(R.id.tag_y_pos, dist * Math.sin(rad));
                sendReport();
            }
        });
    }

    private void updateConnectionState(int state) {
        switch (state) {
            case BluetoothProfile.STATE_DISCONNECTED:
                status.setText(R.string.status_disconnected);
                break;
            case BluetoothProfile.STATE_CONNECTED:
                status.setText(R.string.status_connected);
                break;
            case BluetoothProfile.STATE_CONNECTING:
                status.setText(R.string.status_connecting);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                status.setText(R.string.status_disconnecting);
                break;
        }
    }

    private void sendReport() {
        // get button state
        byte state = 0;
        for (int i = 0; i < 4; ++i) {
            if ((boolean) buttons[i].getTag(R.id.tag_pressed)) {
                state |= (1 << i);
            }
        }

        // get joystick state
        Range<Integer> bounds = new Range<>(-127, 127);
        int adjX = bounds.clamp((int) ((double) joystick.getTag(R.id.tag_x_pos) * 127));
        int adjY = bounds.clamp((int) ((double) joystick.getTag(R.id.tag_y_pos) * -127));

        Log.d(TAG, "sendReport: " + state + " " + adjX + " " + adjY);
        TextView reportIndicator = findViewById(R.id.reportCount);
        reportIndicator.setText("#" + reportIndex++);
        mBTManager.sendReport(0, new byte[]{
                state,
                (byte) adjX,
                (byte) adjY,
        });
    }
}