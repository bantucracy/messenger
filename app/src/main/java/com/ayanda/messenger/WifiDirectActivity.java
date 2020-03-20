package com.ayanda.messenger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import sintulabs.p2p.Ayanda;
import sintulabs.p2p.IWifiDirect;
import sintulabs.p2p.Server;

public class WifiDirectActivity extends AppCompatActivity {
    private Button btnWdDiscover;
    private Button btnWdAnnounce;
    private ListView lvDevices;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private ArrayAdapter<String> peersAdapter = null;
    private final int FINE_LOCATION_CODE = 1000;
    private Ayanda a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        setListeners();
        a = Ayanda.createInstance(this, null, null, new IWifiDirect() {
            @Override
            public void wifiP2pStateChangedAction(Intent intent) {

            }

            @Override
            public void wifiP2pPeersChangedAction() {
                peers.clear();
                // TODO fix error when WiFi off
                peers.addAll(a.wdGetDevicesDiscovered() );
                peerNames.clear();
                for (int i = 0; i < peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                    peersAdapter.add(device.deviceName);
                }
            }

            @Override
            public void wifiP2pConnectionChangedAction(Intent intent) {

            }

            @Override
            public void wifiP2pThisDeviceChangedAction(Intent intent) {

            }

            @Override
            public void onConnectedAsServer(Server server) {

            }

            @Override
            public void onConnectedAsClient(final InetAddress groupOwnerAddress) {
                // This is essentially polling,
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });
        setDeviceInfo();
    }

    private boolean checkPermissions(String permission, int requestCode) {
        // IF permission not granted
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {permission}, requestCode);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    discover();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case FINE_LOCATION_CODE + 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void setDeviceInfo() {
        Random rand = new Random();
        a.setDeviceName("Aya" + rand.nextInt(100000));
        HashMap<String, String> txtRecords = new HashMap<>();
        txtRecords.put("deviceName", a.getDeviceName());
        a.wdSetTxtRecords(txtRecords);
    }

    private void createView() {
        setContentView(R.layout.wd_content);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        lvDevices.setAdapter(peersAdapter);
        btnWdDiscover = (Button) findViewById(R.id.btnWdDiscover);
        btnWdAnnounce = (Button) findViewById(R.id.btnWdAnnounce);
    }

    private void setListeners() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnWdDiscover:
                        if (a.isWDEnabled()) {
                            if (checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE)) {
                                discover();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.wd_unavailable, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btnWdAnnounce:
                        if (checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE + 1)) {
                            announce();
                        }
                        break;
                }
            }
        };
        AdapterView.OnItemClickListener deviceClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                a.wdConnect(device);
            }
        };
        btnWdDiscover.setOnClickListener(clickListener);
        btnWdAnnounce.setOnClickListener(clickListener);
        lvDevices.setOnItemClickListener(deviceClick);
    }

    private void announce() {
        Toast.makeText(getApplicationContext(), "Announcing", Toast.LENGTH_SHORT).show();
        a.wdAnnounce();
    }

    private void discover() {
        Toast.makeText(getApplicationContext(), R.string.wd_available, Toast.LENGTH_SHORT).show();
        a.wdDiscover();    }
  /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        a.wdRegisterReceivers();
    }

    /* unregister the broadcast receiver */

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        a.wdUnregisterReceivers();
    }
}
