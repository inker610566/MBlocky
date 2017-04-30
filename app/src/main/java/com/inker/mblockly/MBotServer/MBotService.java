package com.inker.mblockly.MBotServer;

import android.app.IntentService;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.inker.mblockly.MBotServer.SerialTransmission.BTSerialPortAdapter;
import com.inker.mblockly.MBotServer.SerialTransmission.ReceivePackageCallback;
import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;
import com.inker.mblockly.MBotServer.SerialTransmission.ShutdownEventCallback;
import com.inker.mblockly.MBotServer.SerialTransmission.TxPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

public class MBotService extends BroadcastReceiveService {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice connectDevice;
    private BluetoothSocket socket;
    private String workspaceXml;
    private BTSerialPortAdapter serialAdapter = new BTSerialPortAdapter(new ReceivePackageCallback() {
        @Override
        public void call(RxPackage pkg) {
            BroadcastResult(
                Constants.MBOTSERVICE_RXPACKAGE_RESULT_ACTION,
                Constants.MBOTSERVICE_PACKGE,
                pkg);
        }
    }, new ShutdownEventCallback() {
        @Override
        public void call() {
            // adapter already shutdown
            Disconnect();
        }
    });


    public MBotService() {
        super("MBotService");
    }

    /**
     * Cleanup state to disconnect
     */
    private void Cleanup() {
        connectDevice = null;
        socket = null;
    }

    private final String[] RECEIVE_ACTIONS = new String[] {
            Constants.MBOTSERVICE_CONNECT_ACTION,
            Constants.MBOTSERVICE_DISCONNECT_ACTION,
            Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION,
            Constants.MBOTSERVICE_SEND_PACKAGE_ACTION
    };

    @Override
    protected String[] getIntentActions() {
        return RECEIVE_ACTIONS;
    }

    @Override
    public void onCreate() {
        Log.i("MBotService", "onCreate");
        super.onCreate();
        serialAdapter.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("MBotService", "onDestroy");
        super.onDestroy();
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

    private void BroadcastError(String action, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        BroadcastResult(action, Constants.MBOTSERVICE_ERROR_MESSAGE, sw.toString());
    }

    /**
     * change state to connect state, have failed if exception
     * @param device
     */
    private void ConnectTo(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            connectDevice = device;
            this.socket = socket;
            serialAdapter.Start(socket);
            BroadcastResult(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, device);
        } catch (IOException e) {
            Cleanup();
            BroadcastError(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION, e);
        }
    }

    /**
     * Will change state to disconnect
     */
    private void Disconnect() {
        if(connectDevice == null)
            BroadcastError(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION, Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT);
        else {
            try {
                socket.close();
            } catch (IOException e) {
            }
            BluetoothDevice device = connectDevice;
            BroadcastResult(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, device);
            Cleanup();
            serialAdapter.Shutdown();
        }
    }

    private void QueryConnectState() {
        if(connectDevice == null)
            BroadcastError(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION, Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT);
        else
            BroadcastResult(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION, Constants.BLUETOOTH_DEVICE, connectDevice);
    }

    private void SendPackage(Intent intent) {
        byte[] bytes = intent.getByteArrayExtra(Constants.MBOTSERVICE_PACKGE);
        assert bytes != null;
        serialAdapter.RequestSendPackage(new TxPackage(bytes));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;
        String action = intent.getAction();
        if(action.equals(Constants.MBOTSERVICE_CONNECT_ACTION)) {
            BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
            assert device != null;
            if(connectDevice != null)
                Disconnect();
            ConnectTo(device);
        } else if (action.equals(Constants.MBOTSERVICE_DISCONNECT_ACTION))
            Disconnect();
        else if (action.equals(Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION))
            QueryConnectState();
        else if (action.equals(Constants.MBOTSERVICE_SEND_PACKAGE_ACTION))
            SendPackage(intent);
        else
            assert false;
    }
}
