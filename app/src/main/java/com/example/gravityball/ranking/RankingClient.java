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
        Log.e("GET LEADERBOARD: ", "after create");
        if(client == null) {
            Log.e("GET LEADERBOARD: ", "cant create client");
            return null;
        }

        BlockingQueue<Network.Leaderboard> queue = new LinkedBlockingQueue<>();
        client.addListener(new Listener.ThreadedListener(new Listener(){
            @Override
            public void received (Connection c, Object object) {
                if(object instanceof Network.Leaderboard) {
                    Log.e("GOT OBJECT", ((Network.Leaderboard) object).list.toString());
                    queue.add((Network.Leaderboard) object);
                    Log.e("ADDED OBJECT", ((Network.Leaderboard) object).list.toString());
                    c.close();
                }
            }
        }));

        Network.AskForLeaderboard ask = new Network.AskForLeaderboard();
        ask.levelName = levelName;
        client.sendTCP(ask);

        Network.Leaderboard received = null;
        try {
            Log.e("POLL", "before poll");
            received = queue.poll(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            Log.e("GET LEADERBOARD: ", "receive timeout");
            e.printStackTrace();
        }
        Log.e("POLL", "before poll");

        client.close();
        Log.i("GET LEADERBOARD: ", "received "+received.list);
        return received;
    }

    public static void sendScore(Network.ScoreMessage score) {
        Client client = createClient();
        if(client == null) {
            System.out.println("CLIENT IS NULL");
            return;
        }

        client.sendTCP(score);
        client.close();
    }

    public static void main(String[] args) {
        Network.ScoreMessage message = new Network.ScoreMessage();
        message.time = 1;
        message.levelName="A";
        message.playerName="B";
        sendScore(message);
    }

}
