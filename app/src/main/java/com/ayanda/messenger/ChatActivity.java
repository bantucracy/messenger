package com.ayanda.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import sintulabs.p2p.Ayanda;
import sintulabs.p2p.IWifiDirect;
import sintulabs.p2p.Server;

public class ChatActivity extends AppCompatActivity {
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    final String userId = UUID.randomUUID().toString();
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private ArrayAdapter<String> peersAdapter = null;
    private ArrayList<WifiP2pDevice> wdDevices;
    private Ayanda ayanda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        setupMessagePosting();
        setupWifiDirect();
        ayanda.wdDiscover();

    }

    private void createView() {
        setContentView(R.layout.activity_chat);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
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
                peers.addAll(ayanda.wdGetDevicesDiscovered());
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
        ayanda = Ayanda.createInstance(this, null, null, iWifiDirect);
    }

    void discoverPeersAndConnect() {
        ayanda.wdDiscover();
    }



    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);
        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);

        rvChat.setLayoutManager(linearLayoutManager);
        // When send button is clicked, create message object on Parse

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String data = etMessage.getText().toString();
                message.setBody(data);
                message.setUserId(userId);
                etMessage.setText(null);
                mMessages.add(message);
                refreshMessages();
            }
        });
    }

    void refreshMessages() {
       // mMessages.addAll(mMessages);
        mAdapter.notifyDataSetChanged();
    }
}