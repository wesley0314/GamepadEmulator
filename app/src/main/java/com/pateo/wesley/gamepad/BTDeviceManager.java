package com.pateo.wesley.gamepad;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.Executors;

public class BTDeviceManager {

    private static final String TAG = BTDeviceManager.class.getSimpleName();
    private Context mContext;
    private static BTDeviceManager mInstance;
    private static BluetoothProfile bluetoothProfile;
    private static BluetoothHidDevice HidDevice;
    private int mConnectState = BluetoothProfile.STATE_DISCONNECTED;
    private ConnectionStateListener mListener;

    public BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            Log.d(TAG, "mProfileServiceListener onServiceDisconnected: profile = " + profile);
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.d(TAG, "mProfileServiceListener onServiceConnected: profile = " + profile);
            bluetoothProfile = proxy;
            if (profile == BluetoothProfile.HID_DEVICE) {
                HidDevice = (BluetoothHidDevice) proxy;
                HidConsts.HidDevice = HidDevice;
                if (bluetoothProfile.getConnectedDevices().size() > 0) {
                    mConnectState = HidDevice.getConnectionState(bluetoothProfile.getConnectedDevices().get(0));
                } else {
                    mConnectState = BluetoothProfile.STATE_DISCONNECTED;
                }
                BluetoothHidDeviceAppSdpSettings sdp = new BluetoothHidDeviceAppSdpSettings(HidConsts.NAME,
                        HidConsts.DESCRIPTION, HidConsts.PROVIDER, BluetoothHidDevice.SUBCLASS2_JOYSTICK, HidConsts.DESCRIPTOR);
                HidDevice.registerApp(sdp, null, null, Executors.newCachedThreadPool(), mCallback);
            }
        }
    };

    public final BluetoothHidDevice.Callback mCallback = new BluetoothHidDevice.Callback() {
        @Override
        public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) { }
        @Override
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            Log.d(TAG, "onConnectionStateChanged: device = " + device.getName() + ", state = " + state);
            mConnectState = state;
            if (mListener != null) {
                mListener.onStateChanged(state);
            }
        }
    };

    public interface ConnectionStateListener {
        void onStateChanged(int state);
    }

    private BTDeviceManager(Context context) {
        this.mContext = context;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(mContext, mProfileServiceListener, BluetoothProfile.HID_DEVICE);
    }

    public static BTDeviceManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BTDeviceManager.class) {
                if (mInstance == null) {
                    mInstance = new BTDeviceManager(context);
                }
            }
        }
        return mInstance;
    }

    public boolean isConnected() {
        return mConnectState == BluetoothAdapter.STATE_CONNECTED;
    }

    public void setConnectStateListener(ConnectionStateListener listener) {
        this.mListener = listener;
    }

    public void sendReport(int id, byte[] data) {
        for (BluetoothDevice btDev : bluetoothProfile.getConnectedDevices()) {
            HidDevice.sendReport(btDev, id, data);
        }
    }

}
