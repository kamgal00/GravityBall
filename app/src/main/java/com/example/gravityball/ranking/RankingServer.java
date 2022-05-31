package com.example.gravityball.ranking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RankingServer {
    private static final String FILEPATH = "leaderboard.json";
    public static synchronized Network.Leaderboard getStoredLeaderboard(){
        Network.Leaderboard out = null;
        try {
            Gson gson = new Gson();

            Reader reader = Files.newBufferedReader(Paths.get(FILEPATH));

            Network.Leaderboard leaderboard = gson.fromJson(reader, Network.Leaderboard.class);

            reader.close();

            out = leaderboard;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(out == null || out.list == null) {
            out = new Network.Leaderboard();
            out.list = new ArrayList<>();
        }

        return out;
    }

    public static synchronized void saveLeaderboard(Network.Leaderboard leaderboard){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILEPATH));
            writer.write(new Gson().toJson(leaderboard));

            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        Network.register(server);

        server.addListener(new Listener(){
            @Override
            public void received (Connection c, Object object){
                if(object instanceof Network.ScoreMessage) {
                    Network.Leaderboard leaderboard = getStoredLeaderboard();
                    leaderboard.list.add((Network.ScoreMessage) object);
                    saveLeaderboard(leaderboard);
                }
                else if(object instanceof Network.AskForLeaderboard) {
                    String levelName = ((Network.AskForLeaderboard) object).levelName;
                    List<Network.ScoreMessage> lsm =
                            getStoredLeaderboard().list.stream()
                                    .filter(x-> Objects.equals(x.levelName, levelName))
                                    .sorted(Comparator.comparingLong(x -> x.time))
                                    .limit(100)
                                    .collect(Collectors.toList());
                    Network.Leaderboard out = new Network.Leaderboard();
                    out.list = new ArrayList<>(lsm);
                    c.sendTCP(out);
                }
                c.close();
            }
        });
        try {
            server.bind(Network.port, Network.portUDP);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        server.start();
        Log.set(Log.LEVEL_DEBUG);
    }
}
