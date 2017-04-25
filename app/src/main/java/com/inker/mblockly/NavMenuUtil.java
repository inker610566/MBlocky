package com.inker.mblockly;


/**
 * Created by kuoin on 2017/4/25.
 */

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
