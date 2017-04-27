package com.inker.mblockly;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kuoin on 2017/4/27.
 */

public class DebugActivity  extends AppCompatActivity {
    private NavMenuUtil navUtil = new NavMenuUtil(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_layout);
        navUtil.onCreate();
    }

    @Override
    public void onBackPressed() {
        if(!navUtil.onBackPressed())
            super.onBackPressed();
    }
}
