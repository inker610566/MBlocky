package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.inker.mblockly.MBotServer.ConnectEventCallback;
import com.inker.mblockly.MBotServer.DisconnectEventCallback;
import com.inker.mblockly.MBotServer.MBotServiceUtil;
import com.inker.mblockly.MBotServer.QueryConnectEventCallback;
import com.inker.mblockly.MBotServer.RxPackageCallback;
import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity {
    ImageView img;
    int rx = 127, ry=127;

    private NavMenuUtil navUtil = new NavMenuUtil(this, null);

    private MBotServiceUtil btMbot = new MBotServiceUtil(
            this,
            new ConnectEventCallback() {
                @Override
                public void call(BluetoothDevice device) {
                    assert false;
                }

                @Override
                public void callError(String message) {
                    assert false;
                }
            }, new DisconnectEventCallback() {
        @Override
        public void call(BluetoothDevice device) {
            Toast.makeText(DemoActivity.this, "Device Disconnected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void callError(String message) {
            assert false;
        }
    }, new QueryConnectEventCallback() {
        @Override
        public void call(BluetoothDevice device) {
            assert false;
        }
    }, new RxPackageCallback() {
        @Override
        public void call(RxPackage pkg) {
            ProcessDirectionRequest(pkg);
        }
    });

    private void ProcessDirectionRequest(RxPackage pkg) {
        byte[] bytes = pkg.getBytes();
        for(int i = 0 ; i < bytes.length ; i ++)
            switch(bytes[i]) {
                case 48:
                    btMbot.RequestSendPackage(Integer.toString(rx).getBytes(Charset.forName("UTF-8")));
                    btMbot.RequestSendPackage(new byte[]{'.'});
                    break;
                case 49:
                    btMbot.RequestSendPackage(Integer.toString(ry).getBytes(Charset.forName("UTF-8")));
                    btMbot.RequestSendPackage(new byte[]{'.'});
                    break;
                case 50:
                    btMbot.RequestSendPackage(Integer.toString(rx).getBytes(Charset.forName("UTF-8")));
                    btMbot.RequestSendPackage(new byte[]{'.'});
                    break;
                case 51:
                    btMbot.RequestSendPackage(Integer.toString(ry).getBytes(Charset.forName("UTF-8")));
                    btMbot.RequestSendPackage(new byte[]{'.'});
                    break;
                default:
                    Log.e("MBOT", "NO DEFINE "+Byte.toString(bytes[i]));
            }
    }

    private void SetDirection(int x, int y) {
        //Log.e("inkerabcdddd", Integer.toString(x) + Integer.toString(y));
        int x1 = img.getLeft(),
            x2 = img.getRight(),
            y2 = img.getBottom(),
            y1 = img.getTop();
        int rr = (x2-x1);//, cx = (x1+x2)/2, cy = (y1+y2)/2;
        rx = Math.min(Math.max((x - x1)*255/rr, 0), 255);
        ry = Math.min(Math.max((y - y1)*255/rr, 0), 255);
        Log.e("RX RY", Integer.toString(rx) + ' ' + Integer.toString(ry));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        btMbot.onCreate();
        navUtil.onCreate();

        img = (ImageView)findViewById(R.id.imageView2);
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    // TODO: check if getX getY relative to view
                    case MotionEvent.ACTION_DOWN:
                        SetDirection(
                            (int) event.getX(),
                            (int) event.getY()
                        );
                        break;
                    case MotionEvent.ACTION_MOVE:
                        SetDirection(
                                (int) event.getX(),
                                (int) event.getY()
                        );
                        break;
                    case MotionEvent.ACTION_UP:
                        SetDirection(
                                (int) event.getX(),
                                (int) event.getY()
                        );
                        break;
                }
                return true;

            }
        });

        btMbot.RequestSendPackage(new byte[]{'.'});
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
}
