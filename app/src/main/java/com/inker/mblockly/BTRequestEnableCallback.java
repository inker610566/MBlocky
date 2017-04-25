package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kuoin on 2017/4/24.
 */

public interface BTRequestEnableCallback {
    void deviceFound(BluetoothDevice device);
    void resultDeniedBT();
    void resultDisableBT();
    void finishDiscovery();
}
