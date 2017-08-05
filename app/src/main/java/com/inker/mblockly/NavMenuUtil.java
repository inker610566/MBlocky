package com.inker.mblockly;


/**
 * Created by kuoin on 2017/4/25.
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.inker.mblockly.MBotServer.*;
import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class NavMenuUtil implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity activity;
    private DrawerLayout drawer = null;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ArrayList<RxPackage> debugPkgList;
    public NavMenuUtil(AppCompatActivity activtiy, @Nullable ArrayList<RxPackage> debugPkgList) {
        this.activity = activtiy;
        this.debugPkgList = debugPkgList;
    }

    public void onCreate(){
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);

        activity.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    final HashMap<Integer, Class<?>> nButtonId2Actvitiy = new HashMap<Integer, Class<?>>(){{
        put(new Integer(R.id.bluetooth_nav_button), BluetoothListActivity.class);
        put(new Integer(R.id.demo_nav_button), DemoActivity.class);
        put(new Integer(R.id.debug_nav_button), DebugActivity.class);
    }};

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class<?> aclass = nButtonId2Actvitiy.get(new Integer(id));
        if(aclass != null && aclass != activity.getClass())
        {
            Intent intent = new Intent(activity, aclass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            if(aclass == DebugActivity.class) {
                intent.putParcelableArrayListExtra(Constants.DEBUG_RXPACKAGE_LIST, debugPkgList);
            }
            activity.startActivity(intent);
            activity.finish();
        }

        if(drawer != null)
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
