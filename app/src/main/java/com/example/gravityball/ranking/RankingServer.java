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
        try {
            Gson gson = new Gson();

            Reader reader = Files.newBufferedReader(Paths.get(FILEPATH));

            Network.Leaderboard leaderboard = gson.fromJson(reader, Network.Leaderboard.class);

            reader.close();

            return leaderboard;

        } catch (Exception ex) {
            ex.printStackTrace();
            Network.Leaderboard out = new Network.Leaderboard();
            out.list = new ArrayList<>();
            return out;
        }
    }

    public static synchronized void saveLeaderboard(Network.Leaderboard leaderboard){
        try{
            System.out.println("SAVELEADERBOARD: "+leaderboard.list);

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
            public void connected(Connection c){
                System.out.println("CONNECTED");
            }
            @Override
            public void received (Connection c, Object object){
                if(object instanceof Network.ScoreMessage) {
                    Network.ScoreMessage s = (Network.ScoreMessage) object;
                    System.out.println("RECEIVED SCORE MESSAGE "+ s.playerName+" "+s.levelName+" "+s.time);
                    Network.Leaderboard l = getStoredLeaderboard();
                    if(l == null || l.list == null) {
                        l = new Network.Leaderboard();
                        l.list = new ArrayList<>();
                    }
                    l.list.add((Network.ScoreMessage) object);
                    saveLeaderboard(l);
                }
                else if(object instanceof Network.AskForLeaderboard) {
                    Network.AskForLeaderboard a = (Network.AskForLeaderboard) object;
                    System.out.println("RECEIVED ASK MESSAGE "+ a.levelName);
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
                System.out.println("END CONNECTION");
                c.close();
            }
        });
        try {
            server.bind(Network.port, Network.portUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        System.out.println("TEST");
        Log.set(Log.LEVEL_DEBUG);
    }
}
