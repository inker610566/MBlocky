package com.inker.mblockly;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by kuoin on 2017/4/24.
 * The discovery process is strongly bind with UI component
 */
public class BTDiscoveryUtil {
    private Activity activity;
    private BTRequestEnableCallback callback;
    private final String[] REQUIRE_PERMISSION = new String[]{
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                callback.deviceFound((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                callback.finishDiscovery();
            }
        }
    };

    /**
     * context should implement onRequestPermissionsResult
     */
    public BTDiscoveryUtil(Activity activity, BTRequestEnableCallback callback){
        this.activity = activity;
        this.callback = callback;
    }

    /**
     * Should be called in activity.onCreate
     */
    public void onCreate()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.activity.registerReceiver(mReceiver, filter);
    }

    private void startDiscovery() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        assert adapter.isEnabled();
        adapter.startDiscovery();
    }

    private void checkPermission(){
        ActivityCompat.requestPermissions(this.activity, REQUIRE_PERMISSION, Constants.REQUEST_PERMISSION_BT);
    }

    private void checkEnable(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        assert(adapter != null);
        if(!adapter.isEnabled())
            activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Constants.REQUEST_ENABLE_BT);
        else
            startDiscovery();
    }

    public void initiateDiscovery() {
        checkPermission();
    }

    public boolean handlePermissionRequest(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != Constants.REQUEST_PERMISSION_BT ||
            grantResults.length != REQUIRE_PERMISSION.length)
            return false;
        for(int r : grantResults)
            if(r != PackageManager.PERMISSION_GRANTED)
                return false;
        checkEnable();
        return true;
    }

    /**
     * Should be called in onActivityResult
     * @param requestCode
     * @param resultCode
     * @return isHandled
     */
    public boolean handleEnableRequest(int requestCode, int resultCode) {
        if(requestCode != Constants.REQUEST_ENABLE_BT)
            return false;
        if(resultCode == Activity.RESULT_OK)
            startDiscovery();
        else if(resultCode == Activity.RESULT_CANCELED)
            callback.resultDisableBT();
        else
            assert false;

        return true;
    }

    /**
     * Should be called in activity onDestroy
     */
    public void onDestroy()
    {
        this.activity.unregisterReceiver(mReceiver);
    }

}
