package com.example.gravityball.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.example.gravityball.state.StateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GravityBallServer {
    final Server server;

    private static GravityBallServer instance;

    private GravityBallServer(){
        server = new Server() {
            protected Connection newConnection () {
                return new CharacterConnection();
            }
        };
        Network.register(server);
        server.addListener(StateManager.getInstance());
    }
    private void start() throws IOException {
        server.start();
        server.bind(Network.TCP_PORT, Network.UDP_PORT);
    }
    private void close(){
        server.close();
    }

    public static synchronized boolean create() {
        if(instance != null) return false;
        instance = new GravityBallServer();
        try{
            instance.start();
            messageQueue.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized void destroy(){
        if(instance != null) {
//            throw new RuntimeException();
            instance.close();
            instance=null;
        }
    }

    public static void sendUpdate(Network.ServerUpdate update) {
        try{
            instance.server.sendToAllUDP(update);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean isRunning() {
        return instance != null;
    }

    public static synchronized int sendGameStart(String levelName){

        if(instance == null) return -1;

        ArrayList<Connection> connections = new ArrayList<>(Arrays.asList(instance.server.getConnections()));

        Network.EnterGame m = new Network.EnterGame();
        m.levelName = levelName;
        m.players = connections.size()+1;

        for(int i=0;i<connections.size();i++) {
            m.playerId=i+1;
            ((CharacterConnection) connections.get(i)).id=i+1;
            connections.get(i).sendTCP(m);
        }
        return m.players;
    }

    public static final ConcurrentLinkedQueue<Network.ClientUpdate> messageQueue = new ConcurrentLinkedQueue<>();

}
