package com.example.gravityball;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.minlog.Log;
import com.example.gravityball.networking.GravityBallClient;
import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyListActivity extends AppCompatActivity {

    ListView listView;
    List<String> hosts;
    ArrayAdapter<String> adapter;
    Button refreshButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_list);

        init();
        refreshNoWait();

        com.esotericsoftware.minlog.Log.set(Log.LEVEL_DEBUG);
    }



    void init(){
        listView = findViewById(R.id.HostsListView);
        refreshButton = findViewById(R.id.refreshbutton);

        listView.setOnItemClickListener(
                (adapterView, view, i, l) -> tryToConnectNoWait(hosts.get(i))
        );

        refreshButton.setOnClickListener(view->refreshNoWait());
    }

    private void tryToConnectNoWait(String address) {
        new Thread(() -> GravityBallClient.connect(address)).start();
    }

    private void refresh(){
        List<InetAddress> addresses = GravityBallClient.getHosts();
//        Log.i("FOUND HOSTS: ", addresses.toString());
        hosts = addresses.stream().map(InetAddress::getHostAddress).collect(Collectors.toList());
        System.out.println(hosts);
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                hosts
        );
        runOnUiThread(() -> listView.setAdapter(adapter));
    }

    private void refreshNoWait() {
        new Thread(this::refresh).start();
    }

    @Override
    public void onBackPressed(){
        StateManager.getInstance().changeState(GameState.MAIN);
    }
}