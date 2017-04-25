package com.inker.mblockly;


/**
 * Created by kuoin on 2017/4/25.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.HashMap;

public class NavMenuUtil implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity activity;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private  NavigationView navigationView;
    public NavMenuUtil(AppCompatActivity activtiy) {
        this.activity = activtiy;
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
        put(new Integer(R.id.workspace_nav_button), WorkspaceActivity.class);
    }};

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class<?> aclass = nButtonId2Actvitiy.get(new Integer(id));
        if(aclass != null && aclass != activity.getClass())
        {
            Intent intent = new Intent(activity, aclass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            activity.startActivity(intent);
            activity.finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
