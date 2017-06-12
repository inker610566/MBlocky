package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;
import android.support.v4.util.Pair;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kuoin on 2017/6/12.
 */

/**
 * Controller for UI
 */
public class BluetoothList {
    private HashMap<String, Integer> scanAddress = new HashMap<>();
    // (IsConnected, scanDevices) for bind to adapter
    public ArrayList<Pair<Boolean, BluetoothDevice>> data = new ArrayList<>();
    private BluetoothDevice connectDevice = null;
    private BaseAdapter adapter;

    public BluetoothList(ArrayAdapterFactory factory) {
        this.adapter = factory.produce(data);
    }

    public BaseAdapter getAdapter() {
        return this.adapter;
    }

    /**
     *
     * @param device filtered to avoid repeated item
     */
    public void addDevice(BluetoothDevice device) {
        String addr = device.getAddress();
        if(!scanAddress.containsKey(addr)) {
            data.add(new Pair<Boolean, BluetoothDevice>(IsEqual(device, connectDevice), device));
            scanAddress.put(addr, data.size()-1);
            adapter.notifyDataSetChanged();
        }
    }

    public void setConnectDevice(BluetoothDevice device) {
        // Unstar old device
        if(connectDevice != null) {
            Integer old_idx = scanAddress.get(connectDevice.getAddress());
            if(old_idx != null) {
                data.set(old_idx, new Pair<Boolean, BluetoothDevice>(false, connectDevice));
            }
        }
        connectDevice = device;
        Integer new_idx = scanAddress.get(connectDevice.getAddress());
        if(new_idx != null) {
            data.set(new_idx, new Pair<Boolean, BluetoothDevice>(true, connectDevice));
        }
        else {
            data.add(new Pair<Boolean, BluetoothDevice>(IsEqual(device, connectDevice), device));
            scanAddress.put(connectDevice.getAddress(), data.size()-1);
        }
        adapter.notifyDataSetChanged();
    }

    public void setDisconnectFrom(BluetoothDevice device) {
        if(connectDevice != null) {
            assert IsEqual(connectDevice, device);
            Integer idx = scanAddress.get(connectDevice.getAddress());
            data.set(idx, new Pair<Boolean, BluetoothDevice>(false, connectDevice));
            connectDevice = null;
            adapter.notifyDataSetChanged();
        }
    }

    public BluetoothDevice getConnectDevice() {
        return connectDevice;
    }

    public void Clear() {
        scanAddress.clear();
        data.clear();
        if(connectDevice != null)
            addDevice(connectDevice);
        adapter.notifyDataSetChanged();
    }

    public static boolean IsEqual(BluetoothDevice d1, BluetoothDevice d2) {
        if(d1 == null || d2 == null)
            return d1 == d2;
        return d1.getAddress().equals(d2.getAddress()) &&
               d1.getName().equals(d2.getName());
    }
}
