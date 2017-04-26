package com.inker.mblockly;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

/**
 * Created by kuoin on 2017/4/26.
 */

public class MBotServiceUtil {
    private Activity activity;
    public MBotServiceUtil(Activity activity) {
        this.activity = activity;
    }

    public void ConnectDevice(BluetoothDevice device) {
        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_CONNECT_ACTION);
        intent.putExtra(Constants.BLUETOOTH_DEVICE, device);
        activity.startService(intent);
    }

}
