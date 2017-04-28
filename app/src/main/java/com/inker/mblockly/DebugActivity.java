package com.inker.mblockly;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inker.mblockly.MBotServer.ConnectEventCallback;
import com.inker.mblockly.MBotServer.DisconnectEventCallback;
import com.inker.mblockly.MBotServer.MBotServiceUtil;
import com.inker.mblockly.MBotServer.QueryConnectEventCallback;
import com.inker.mblockly.MBotServer.RxPackageCallback;
import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;

/**
 * Created by kuoin on 2017/4/27.
 */

public class DebugActivity  extends AppCompatActivity {
    private NavMenuUtil navUtil = new NavMenuUtil(this, null);
    private TextView mBotOutput;
    private EditText mBotInput;
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
                    Toast.makeText(DebugActivity.this, "Device Disconnected", Toast.LENGTH_LONG).show();
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
                    byte[] bytes = pkg.getBytes();
                    StringBuilder sb = new StringBuilder(bytes.length * 2);
                    for(byte b: bytes)
                        sb.append(String.format("%02x", b));
                    mBotOutput.append(sb.toString());
                }
            });

    private String[] SplitStringArray(String text) {
        text = text.replaceAll(new String("[^0-9a-fA-F]"), "");
        String[] ss = new String[(text.length()+1)/2];
        int i = 0, j = 0;
        for(; j+2 <= text.length() ; j += 2, i ++)
            ss[i] = text.substring(j, j+2);
        if(j+1 == text.length())
            ss[i] = new String(new char[]{'0', text.charAt(j)});
        return ss;
    }

    private String FormatString(String text) {
        text = TextUtils.join(" ", SplitStringArray(text));
        return text;
    }

    private byte[] ToByteArray(String text) {
        String[] bs = SplitStringArray(text);
        byte[] bytes = new byte[bs.length];
        for(int i = 0 ; i < bs.length ; i ++)
            bytes[i] = (byte)Integer.parseInt(bs[i], 16);
        return bytes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_layout);
        navUtil.onCreate();

        mBotOutput = (TextView) findViewById(R.id.debugMbotOutput);
        mBotInput = (EditText) findViewById(R.id.debugMbotInput);

        ((Button)findViewById(R.id.debugFormatButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = FormatString(mBotInput.getText().toString());
                mBotInput.setText(text);
            }
        });

        ((Button)findViewById(R.id.debugSendButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bs = ToByteArray(mBotInput.getText().toString());
                mBotOutput.setText(new String(bs, StandardCharsets.UTF_8));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(!navUtil.onBackPressed())
            super.onBackPressed();
    }
}
