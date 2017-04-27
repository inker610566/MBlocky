package com.inker.mblockly.MBotServer;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kuoin on 2017/4/27.
 */

public interface ConnectEventCallback {
    void call(BluetoothDevice device);
    void callError(String message);
}
