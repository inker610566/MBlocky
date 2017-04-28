package com.inker.mblockly.MBotServer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

/**
 * Created by kuoin on 2017/4/26.
 */

public class MBotServiceUtil {
    private Activity activity;

    private BroadcastReceiver mReceiver = null;

    private ConnectEventCallback ccb;
    private DisconnectEventCallback dcb;
    private QueryConnectEventCallback qccb;
    private RxPackageCallback rxcb;

    public MBotServiceUtil(
            Activity activity,
            ConnectEventCallback ccb,
            DisconnectEventCallback dcb,
            QueryConnectEventCallback qccb,
            RxPackageCallback rxcb
        ) {
        this.activity = activity;
        this.ccb = ccb;
        this.dcb = dcb;
        this.qccb = qccb;
        this.rxcb = rxcb;
    }

    /**
     * Should be called in activity onCreate
     */
    public void onCreate() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction(), errmsg;
                if(action.equals(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION)) {
                    errmsg = intent.getStringExtra(Constants.MBOTSERVICE_ERROR_MESSAGE);
                    if(errmsg != null)
                        ccb.callError(errmsg);
                    else {
                        BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
                        assert device != null;
                        ccb.call(device);
                    }
                }
                else if (action.equals(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION)) {
                    errmsg = intent.getStringExtra(Constants.MBOTSERVICE_ERROR_MESSAGE);
                    if(errmsg != null) {
                        if (errmsg.equals(Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT))
                            dcb.call(null);
                        else
                            dcb.callError(errmsg);
                    }
                    else {
                        BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
                        assert device != null;
                        dcb.call(device);
                    }
                } else if (action.equals(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION)) {
                    errmsg = intent.getStringExtra(Constants.MBOTSERVICE_ERROR_MESSAGE);
                    if(errmsg != null) {
                        assert errmsg.equals(Constants.MBOTSERVICE_ERROR_NO_DEVICE_CONNECT);
                        qccb.call(null);
                    }
                    else {
                        BluetoothDevice device = intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE);
                        assert device != null;
                        qccb.call(device);
                    }
                } else if (action.equals(Constants.MBOTSERVICE_RXPACKAGE_RESULT_ACTION)) {
                    RxPackage pkg = intent.getParcelableExtra(Constants.MBOTSERVICE_PACKGE);
                    assert pkg != null;
                    rxcb.call(pkg);
                } else
                    assert false;
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MBOTSERVICE_CONNECT_RESULT_ACTION);
        filter.addAction(Constants.MBOTSERVICE_DISCONNECT_RESULT_ACTION);
        filter.addAction(Constants.MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this.activity).registerReceiver(mReceiver, filter);

    }

    /**
     * Should be called in activity onDestroy
     */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.activity).unregisterReceiver(mReceiver);
    }

    public void RequestConnectDevice(BluetoothDevice device) {
        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_CONNECT_ACTION);
        intent.putExtra(Constants.BLUETOOTH_DEVICE, device);
        activity.startService(intent);
    }

    public void RequestQueryConnectState() {
        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION);
        activity.startService(intent);
    }

    public void RequestDisconnect() {
        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_DISCONNECT_ACTION);
        activity.startService(intent);
    }

    public void RequestSendPackage(byte[] bytes) {
        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_SEND_PACKAGE_ACTION);
        intent.putExtra(Constants.MBOTSERVICE_PACKGE, bytes);
        activity.startService(intent);
    }
}
