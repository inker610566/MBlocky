package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothListActivity extends AppCompatActivity implements BTRequestEnableCallback{

    private BTDiscoveryUtil bt = new BTDiscoveryUtil(this, this);
    private Button scanButton;
    private ListView btListview;
    private ArrayList<BluetoothDevice> scanDevices = new ArrayList<>();
    private boolean isUIScanning = false;

    private void setUIScanning() {
        assert !isUIScanning;
        isUIScanning = true;
        scanDevices.clear();
        ((ArrayAdapter)btListview.getAdapter()).notifyDataSetChanged();
        scanButton.setText(getResources().getText(R.string.scanning));
        scanButton.setEnabled(false);
    }

    private void setUIIdle() {
        assert isUIScanning;
        isUIScanning = false;
        scanButton.setText(getResources().getText(R.string.scan));
        scanButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt.onCreate();
        setContentView(R.layout.activity_bluetooth_list);
        // UI to symbol
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scanButton = ((Button)findViewById(R.id.scan_button));
        btListview = (ListView)findViewById(R.id.bluetooth_listview);

        // set handler
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUIScanning();
                bt.initiateDiscovery();
            }
        });
        ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(this, R.layout.btdevice_item, scanDevices){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View item = inflater.inflate(R.layout.btdevice_item, parent, false);
                BluetoothDevice device = scanDevices.get(position);
                String name = device.getName(), addr = device.getAddress();
                ((TextView)item.findViewById(R.id.textView)).setText(name);
                ((TextView)item.findViewById(R.id.textView2)).setText(addr);
                return item;
            }
        };
        btListview.setAdapter(arrayAdapter);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!bt.handleEnableRequest(requestCode, resultCode))
        {
            // check rest code
        }
    }

    @Override
    public void deviceFound(BluetoothDevice device) {
        scanDevices.add(device);
        ((ArrayAdapter)btListview.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void resultDeniedBT() {
        setUIIdle();
        Toast.makeText(this, getResources().getText(R.string.request_bt_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resultDisableBT() {
        setUIIdle();
        Toast.makeText(this, getResources().getText(R.string.enable_bt_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishDiscovery() {
        setUIIdle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!bt.handlePermissionRequest(requestCode, permissions, grantResults))
        {
            // check rest code
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    /*public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    */
}
