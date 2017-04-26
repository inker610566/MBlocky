package com.inker.mblockly;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

public class MBotService extends IntentService {
    private BluetoothDevice connectDevice;
    private String workspaceXml;

    public MBotService() {
        super("MBotService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if(action == Constants.MBOTSERVICE_CONNECT_ACTION) {
            BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
            device.getName();
        } else
            assert false;
    }
}
