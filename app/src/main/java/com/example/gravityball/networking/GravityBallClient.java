package com.example.gravityball.networking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.example.gravityball.state.StateManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GravityBallClient {
    Client client;

    private static GravityBallClient instance;

    private GravityBallClient(){
        client = new Client();
        Network.register(client);
        client.addListener(StateManager.getInstance());
    }
    private void start() throws IOException {
        client.start();
    }
    private void close(){
        client.close();
    }

    public static synchronized boolean create() {
        if(instance != null) return false;
        instance = new GravityBallClient();
        try{
            instance.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized void destroy(){
        if(instance != null) {
            instance.close();
            instance=null;
        }
    }

    public static synchronized boolean isConnected(){
        if(instance == null) return false;
        return instance.client.isConnected();
    }

    public static void sendUpdate(Network.ClientUpdate update) {
        try{
            instance.client.sendUDP(update);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized List<InetAddress> getHosts() {
        if(instance == null) return new ArrayList<>();
        return instance.client.discoverHosts(Network.UDP_PORT, 1000);
    }

    public static synchronized void connect(String address) {
        if(instance == null) return;
        try{
            instance.client.connect(5000, address, Network.TCP_PORT, Network.UDP_PORT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentLinkedQueue<Network.ServerUpdate> messageQueue = new ConcurrentLinkedQueue<>();

}
