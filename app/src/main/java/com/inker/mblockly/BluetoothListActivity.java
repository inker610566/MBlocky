package com.inker.mblockly;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inker.mblockly.MBotServer.ConnectEventCallback;
import com.inker.mblockly.MBotServer.DisconnectEventCallback;
import com.inker.mblockly.MBotServer.MBotServiceUtil;
import com.inker.mblockly.MBotServer.QueryConnectEventCallback;
import com.inker.mblockly.MBotServer.RxPackageCallback;
import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

import java.util.ArrayList;
import java.util.List;

public class BluetoothListActivity extends AppCompatActivity
    implements BTRequestEnableCallback {

    private ArrayList<RxPackage> debugRxPkgs = new ArrayList<>();
    private BTDiscoveryUtil btDiscovery = new BTDiscoveryUtil(this, this);
    private MBotServiceUtil btMbot = new MBotServiceUtil(this, new ConnectEventCallback() {
        @Override
        public void call(BluetoothDevice device) {
            setUIConnectTo(device);
            setUIConnectIdle();
        }

        @Override
        public void callError(String message) {
            setUIConnectIdle();
            Toast.makeText(BluetoothListActivity.this, "[ERROR] " + message, Toast.LENGTH_LONG).show();
        }
    }, new DisconnectEventCallback() {
        @Override
        public void call(BluetoothDevice device) {
            // if device == null already disconnect
            setUIDisconnectFrom(device);
        }

        @Override
        public void callError(String message) {
            Toast.makeText(BluetoothListActivity.this, "[ERROR] " + message, Toast.LENGTH_LONG).show();
        }
    }, new QueryConnectEventCallback() {
        @Override
        public void call(BluetoothDevice device) {
            if (device != null)
                setUIConnectTo(device);
        }
    }, new RxPackageCallback() {
        @Override
        public void call(RxPackage pkg) {
            debugRxPkgs.add(pkg);
        }
    });
    private BluetoothList blist;
    private Button scanButton;
    private ListView btListview;
    private boolean isUIScanning = false, isUIConnecting = false, isUIDisconnecting = false;
    private NavMenuUtil navUtil = new NavMenuUtil(this, debugRxPkgs);
    private ProgressDialog connectingDialog, disconnectingDialog;

    private void setUIConnectTo(BluetoothDevice device) {
        blist.setConnectDevice(device);
    }

    private  void setUIDisconnectFrom(BluetoothDevice device) {
        blist.setDisconnectFrom(device);
    }


    private void setUIScanning() {
        assert !isUIScanning;
        isUIScanning = true;
        blist.Clear();
        scanButton.setText(getResources().getText(R.string.scanning));
        scanButton.setEnabled(false);
    }

    private void setUIScanIdle() {
        assert isUIScanning;
        isUIScanning = false;
        scanButton.setText(getResources().getText(R.string.scan));
        scanButton.setEnabled(true);
    }

    private void setUIConnecting(BluetoothDevice device) {
        assert !isUIConnecting;
        isUIConnecting = true;
        connectingDialog = ProgressDialog.show(this, "Connect to "+device.getName()+" "+device.getAddress(), "Connecting");
    }

    private void setUIConnectIdle() {
        assert isUIConnecting;
        isUIConnecting = false;
        connectingDialog.dismiss();
    }

    private void setUIDisconnecting() {
        assert !isUIDisconnecting;
        isUIDisconnecting = true;
        disconnectingDialog = ProgressDialog.show(this, "Disconnect to device", "Disconnecting");
    }

    private  void setUIDisconnectIdle() {
        assert isUIDisconnecting;
        isUIDisconnecting = false;
        debugRxPkgs.clear();
        disconnectingDialog.dismiss();
    }

    private void setBTItemStar(View view) {
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.star_big_on);
    }

    private void setBTItemUnStar(View view) {
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.star_big_off);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        navUtil.onCreate();
        btMbot.onCreate();

        // UI to symbol
        scanButton = ((Button)findViewById(R.id.scan_button));
        btListview = (ListView)findViewById(R.id.bluetooth_listview);

        // set handler
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            setUIScanning();
            btDiscovery.initiateDiscovery();
            }
        });
        blist = new BluetoothList(new ArrayAdapterFactory() {
            @Override
            public BaseAdapter produce(ArrayList<Pair<Boolean, BluetoothDevice>> data) {
                return new ArrayAdapter<Pair<Boolean, BluetoothDevice>>(BluetoothListActivity.this, R.layout.btdevice_item, data){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View item = getLayoutInflater().inflate(R.layout.btdevice_item, parent, false);
                        BluetoothDevice device = blist.data.get(position).second;
                        String name = device.getName(), addr = device.getAddress();
                        ((TextView)item.findViewById(R.id.textView)).setText(name);
                        ((TextView)item.findViewById(R.id.textView2)).setText(addr);
                        if(blist.data.get(position).first)
                            setBTItemStar(item);
                        return item;
                    }
                };
            }
        });
        btListview.setAdapter(blist.getAdapter());
        btListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = blist.data.get(i).second;
                if(BluetoothList.IsEqual(blist.getConnectDevice(), device)) {
                    setUIDisconnectFrom(device);
                    btMbot.RequestDisconnect();
                }
                else {
                    setUIConnecting(device);
                    btMbot.RequestConnectDevice(device);
                }
            }
        });

        btMbot.RequestQueryConnectState();
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
        if(!btDiscovery.handleEnableRequest(requestCode, resultCode))
        {
            // check rest code
        }
    }

    @Override
    public void deviceFound(BluetoothDevice device) {
        blist.addDevice(device);
    }

    @Override
    public void resultDeniedBT() {
        setUIScanIdle();
        Toast.makeText(this, getResources().getText(R.string.request_bt_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resultDisableBT() {
        setUIScanIdle();
        Toast.makeText(this, getResources().getText(R.string.enable_bt_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishDiscovery() {
        setUIScanIdle();
    }

    @Override
    public void onBackPressed() {
        if(!navUtil.onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btMbot.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!btDiscovery.handlePermissionRequest(requestCode, permissions, grantResults))
        {
            // check rest code
        }
    }
}
