package com.inker.mblockly.MBotServer;

import android.app.IntentService;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ParcelableCompat;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

public class MBotService extends IntentService {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice connectDevice = null;
    private BluetoothSocket socket;
    private String workspaceXml;


    public MBotService() {
        super("MBotService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private String ExceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void BroadcastResult(String action, String extra_field_name, Parcelable object) {
        Intent intent = new Intent(action);
        intent.putExtra(extra_field_name, object);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void BroadcastResult(String action, String extra_field_name, String extra_field) {
        Intent intent = new Intent(action);
        intent.putExtra(extra_field_name, extra_field);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void BroadcastError(String action, String errorMsg) {
        BroadcastResult(action, Constants.MBOTSERVICE_ERROR_MESSAGE, errorMsg);
    }

    private void ConnectTo(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            connectDevice = device;
            this.socket = socket;
            BroadcastResult(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, device);
        } catch (IOException e) {
            BroadcastError(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION, ExceptionToString(e));
        }
    }

    private void Disconnect() {
        if(connectDevice == null)
            BroadcastError(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION, Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT);
        else {
            try {
                socket.close();
                BluetoothDevice device = connectDevice;
                connectDevice = null;
                socket = null;
                BroadcastResult(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, device);
            } catch (IOException e) {
                BroadcastError(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION, ExceptionToString(e));
            }
        }
    }

    private void QueryConnectState() {
        if(connectDevice == null)
            BroadcastError(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION, Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT);
        else
            BroadcastResult(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, connectDevice);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if(action == Constants.MBOTSERVICE_CONNECT_ACTION) {
            BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
            assert device != null;
            if(connectDevice != null)
                Disconnect();
            ConnectTo(device);
        } else if (action == Constants.MBOTSERVICE_DISCONNECT_ACTION)
            Disconnect();
        else if (action == Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION)
            QueryConnectState();
        else
            assert false;
    }
}
