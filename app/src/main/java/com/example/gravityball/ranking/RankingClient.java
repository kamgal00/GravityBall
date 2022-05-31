package com.example.gravityball.ranking;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RankingClient {

    public static Client createClient(){
        Client client = new Client();
        client.start();
        Network.register(client);
        try{
            client.connect(
                    5000,
                    client.discoverHost(Network.portUDP, 5000).getHostAddress(), //CHANGE FOR GLOBAL SERVER
                    Network.port,
                    Network.portUDP);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return client;
    }

    public static Network.Leaderboard getLeaderboardForLevel(String levelName) {

        Client client = createClient();
        if(client == null) {
            return null;
        }

        BlockingQueue<Network.Leaderboard> queue = new LinkedBlockingQueue<>();

        client.addListener(new Listener.ThreadedListener(new Listener(){
            @Override
            public void received (Connection c, Object object) {
                if(object instanceof Network.Leaderboard) {
                    queue.add((Network.Leaderboard) object);
                    c.close();
                }
            }
        }));

        Network.AskForLeaderboard ask = new Network.AskForLeaderboard();
        ask.levelName = levelName;
        client.sendTCP(ask);

        Network.Leaderboard received = null;
        try {
            received = queue.poll(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.close();
        return received;
    }

    public static void sendScore(Network.ScoreMessage score) {
        Client client = createClient();
        if(client == null) {
            return;
        }

        client.sendTCP(score);
        client.close();
    }

}
