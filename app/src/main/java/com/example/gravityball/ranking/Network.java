package com.example.gravityball.ranking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.example.gravityball.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Network {
    public static final String HOST = "localhost";
    public static final int port = 56000;
    public static final int portUDP = 56100;
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(ScoreMessage.class);
        kryo.register(ArrayList.class);
        kryo.register(AskForLeaderboard.class);
        kryo.register(Leaderboard.class);
    }

    public static class ScoreMessage implements Serializable {
        public String levelName, playerName;
        public long time;

        @Override
        public String toString() {
            return levelName+" || "+playerName+" || "+ StringUtils.millisToString(time);
        }
    }

    public static class Leaderboard implements Serializable{
        public ArrayList<ScoreMessage> list;
    }
    public static class AskForLeaderboard {
        public String levelName;
    }
}
