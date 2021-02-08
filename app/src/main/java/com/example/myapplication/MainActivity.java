package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.MacAddress;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import static android.net.wifi.p2p.WifiP2pManager.BUSY;
import static android.net.wifi.p2p.WifiP2pManager.ERROR;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_WIFI_STATE},
                    1);
            return;
        } else
        {
            dotest();
            localhosttest();
        }
    }

    @SuppressLint("MissingPermission")
    public void dotest(){
        WifiP2pManager mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {

            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {


                    String ssid = group.getNetworkName();
                    String password = group.getPassphrase();
                    String address = group.getOwner().deviceAddress;
                    Log.d("TEST","SSID = "+ssid+", password: "+password+", group owner address="+address);
                } else {

                    mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                           dotest();
                        }

                        @Override
                        public void onFailure(int reason) {
                            if (reason==BUSY)
                            {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(300);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                            @Override
                                            public void onSuccess() {
                                                dotest();
                                            }

                                            @Override
                                            public void onFailure(int reason) {
                                                dotest();
                                            }
                                        });
                                    }
                                }).start();

                            }

                        }
                    });
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void localhosttest(){
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifi.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback(){
                                        @Override
                                        public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                                            super.onStarted(reservation);
                                            String ssid = reservation.getSoftApConfiguration().getSsid();
                                            String password = reservation.getSoftApConfiguration().getPassphrase();
                                            MacAddress address = reservation.getSoftApConfiguration().getBssid();
                                            Log.d("TEST","LocalHotspot - SSID = "+ssid+", password: "+password+", group owner address="+address);
                                        }
                                    }
                ,new Handler(getMainLooper()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    dotest();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}
