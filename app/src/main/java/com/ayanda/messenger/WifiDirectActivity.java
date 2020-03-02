package com.ayanda.messenger;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import sintulabs.p2p.Ayanda;
import sintulabs.p2p.IWifiDirect;
import sintulabs.p2p.Server;

public class WifiDirectActivity extends AppCompatActivity {
    private Button btnWdDiscover;
    private ListView lvDevices;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private ArrayAdapter<String> peersAdapter = null;
    private ArrayList<WifiP2pDevice> wdDevices;
    private Ayanda a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        setListeners();
        a = new Ayanda(this, null, null, new IWifiDirect() {
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
    }

    private void createView() {
        setContentView(R.layout.wd_content);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        lvDevices.setAdapter(peersAdapter);
        btnWdDiscover = (Button) findViewById(R.id.btnWdDiscover);
    }

    private void setListeners() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnWdDiscover:
                        a.wdDiscover();
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
        lvDevices.setOnItemClickListener(deviceClick);
    }

    private void setupWifiDirect() {
        IWifiDirect iWifiDirect = new IWifiDirect() {
            @Override
            public void wifiP2pStateChangedAction(Intent intent) {
                // Wifi has been disabled or disabled
            }

            @Override
            public void wifiP2pPeersChangedAction() {
                // Number of Peers has changed
                peers.clear();
                // TODO fix error when WiFi off
                peers.addAll(a.wdGetDevicesDiscovered());
                peerNames.clear();
                for (int i = 0; i < peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                    peersAdapter.add(device.deviceName);
                }
                // Connect to a particular device

            }

            @Override
            public void wifiP2pConnectionChangedAction(Intent intent) {
                // Either connected to or Disconnected from device
            }

            @Override
            public void wifiP2pThisDeviceChangedAction(Intent intent) {

            }

            @Override
            public void onConnectedAsServer(Server server) {

            }

            @Override
            public void onConnectedAsClient(InetAddress groupOwnerAddress) {

            }
        };
        a = new Ayanda(this, null, null, iWifiDirect);
    }

}
