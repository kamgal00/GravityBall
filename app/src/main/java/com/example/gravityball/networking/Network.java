package com.example.gravityball.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(EnterLobby.class);
        kryo.register(EnterGame.class);
        kryo.register(ServerUpdate.class);
        kryo.register(ClientUpdate.class);
        kryo.register(Vec2.class);
        kryo.register(ArrayList.class);
        kryo.register(Float.class);
        kryo.register(Long.class);
    }
    public static class EnterLobby{}
    public static class EnterGame{
        public String levelName;
        public int players, playerId;
    }
    public static class ServerUpdate{
        public ArrayList<Vec2> positions, velocities, forces;
        public ArrayList<Float> angles, angularVelocities;
        public ArrayList<Long> times;
        public long startTime;
    }
    public static class ClientUpdate{
        public Vec2 force;
        public int id;
    }
}
