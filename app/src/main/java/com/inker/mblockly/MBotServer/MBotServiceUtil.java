package com.inker.mblockly.MBotServer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.inker.mblockly.Constants;
import com.inker.mblockly.FailedCallback;
import com.inker.mblockly.SuccessCallback;

/**
 * Created by kuoin on 2017/4/26.
 */

public class MBotServiceUtil {
    private Activity activity;

    private BroadcastReceiver mReceiver = null;
    private SuccessCallback<String> scb;
    private FailedCallback<String> fcb;

    public MBotServiceUtil(Activity activity) {
        this.activity = activity;
    }

    private void CancelReceiver() {
        if(mReceiver != null) {
            this.activity.unregisterReceiver(mReceiver);
            mReceiver = null;
            scb = null;
            fcb = null; // if notify cancel?
        }
    }

    public void ConnectDevice(BluetoothDevice device, SuccessCallback<String> scb, FailedCallback<String> fcb) {
        CancelReceiver();
        this.scb = scb;
        this.fcb = fcb;
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String errMsg = intent.getStringExtra(Constants.MBOTSERVICE_ERROR_MESSAGE);
                if(errMsg != null)
                    MBotServiceUtil.this.fcb.Callback(errMsg);
                else
                    MBotServiceUtil.this.scb.Callback(null);
            }
        };
        this.activity.registerReceiver(mReceiver, new IntentFilter(Constants.MBOTSERVICE_CONNECT_ACTION_RESULT));

        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_CONNECT_ACTION);
        intent.putExtra(Constants.BLUETOOTH_DEVICE, device);
        activity.startService(intent);
    }

    class ConnectStateBroadcastReceiver extends BroadcastReceiver {
        SuccessCallback<BluetoothDevice> callback;
        ConnectStateBroadcastReceiver(SuccessCallback<BluetoothDevice> callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            callback.Callback((BluetoothDevice) intent.getParcelableExtra(Constants.BLUETOOTH_DEVICE));
        }
    }

    public void GetConnectState(SuccessCallback<BluetoothDevice> scb) {
        this.activity.registerReceiver(
            new ConnectStateBroadcastReceiver(scb),
            new IntentFilter(Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION_RESULT)
        );

        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_QUERY_CONNECT_STATE_ACTION);
        activity.startService(intent);
    }

    public void DisconnectDevice(SuccessCallback<String> scb) {
        CancelReceiver();
        this.scb = scb;
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MBotServiceUtil.this.scb.Callback(null);
            }
        };
        this.activity.registerReceiver(mReceiver, new IntentFilter(Constants.MBOTSERVICE_DISCONNECT_ACTION_RESULT));

        Intent intent = new Intent(activity, MBotService.class);
        intent.setAction(Constants.MBOTSERVICE_DISCONNECT_ACTION);
        activity.startService(intent);
    }

    /**
     * Should be called in activity onDestroy
     */
    public void onDestroy() {
        CancelReceiver();
    }
}
