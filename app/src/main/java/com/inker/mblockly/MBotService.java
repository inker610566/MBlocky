package com.inker.mblockly;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.BluetoothDevice;

public class MBotService extends Service {
    private BluetoothDevice connectDevice;
    private String workspaceXml;

    public MBotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
