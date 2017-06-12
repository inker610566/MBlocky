package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;
import android.support.v4.util.Pair;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by kuoin on 2017/6/12.
 */

public interface ArrayAdapterFactory {
    BaseAdapter produce(ArrayList<Pair<Boolean, BluetoothDevice>> data);
}
