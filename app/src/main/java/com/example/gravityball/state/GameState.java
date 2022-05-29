package com.example.gravityball.state;

import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.example.gravityball.GameActivity;
import com.example.gravityball.LobbyActivity;
import com.example.gravityball.LobbyListActivity;
import com.example.gravityball.MainActivity;
import com.example.gravityball.Navigator;
import com.example.gravityball.networking.GravityBallClient;
import com.example.gravityball.networking.GravityBallServer;
import com.example.gravityball.networking.Network;

import java.util.ArrayList;
import java.util.Arrays;

public enum GameState implements EventHandler{

    MAIN{
        @Override
        public void connected(Connection c) {

        }

        @Override
        public void received(Connection c, Object object) {

        }

        @Override
        public void disconnected(Connection c) {

        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallServer.destroy();
            GravityBallClient.destroy();
            Navigator.returnTo(MainActivity.class);
        }

    },
    CHOOSE_LOBBY() {
        @Override
        public void connected(Connection c) {
            StateManager.getInstance().changeState(LOBBY_CLIENT);
        }

        @Override
        public void received(Connection c, Object object) {

        }

        @Override
        public void disconnected(Connection c) {

        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallServer.destroy();
            GravityBallClient.destroy();
            if(!GravityBallClient.create())
            {
                Log.e("CHOOSE LOBBY", "cant create client");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            Navigator.returnTo(LobbyListActivity.class);
        }
    },
    LOBBY_OWNER{
        @Override
        public void connected(Connection c) {

        }

        @Override
        public void received(Connection c, Object object) {

        }

        @Override
        public void disconnected(Connection c) {

        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallClient.destroy();
            if(!GravityBallServer.create()) {
                Log.e("LOBBY OWNER", "cant create server");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            StateManager.getInstance().setHost(true);
            Navigator.returnTo(LobbyActivity.class);
        }
    },
    LOBBY_CLIENT{
        @Override
        public void connected(Connection c) {

        }

        @Override
        public void received(Connection c, Object object) {
            if(object instanceof Network.EnterGame) {
                Network.EnterGame a = (Network.EnterGame) object;
                StateManager.getInstance().setLevelName(a.levelName);
                StateManager.getInstance().setPlayers(a.players);
                StateManager.getInstance().setPlayerId(a.playerId);
                StateManager.getInstance().changeState(GAME_CLIENT);
            }
        }

        @Override
        public void disconnected(Connection c) {
            StateManager.getInstance().changeState(MAIN);
        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallServer.destroy();
            if(!GravityBallClient.isConnected()) {
                Log.e("LOBBY_CLIENT", "user is not connected");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            StateManager.getInstance().setHost(false);
            Navigator.returnTo(LobbyActivity.class);

        }
    },
    GAME_OWNER{
        @Override
        public void connected(Connection c) {
//            c.close();
        }

        @Override
        public void received(Connection c, Object object) {

        }

        @Override
        public void disconnected(Connection c) {

        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallClient.destroy();
            if(!GravityBallServer.isRunning()) {
                Log.e("GAME_OWNER", "server is not running");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            int players = GravityBallServer.sendGameStart(StateManager.getInstance().getLevelName());
            if(players==-1) {
                Log.e("GAME_OWNER", "can't send start message");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            StateManager.getInstance().setPlayers(players);
            StateManager.getInstance().setPlayerId(0);
            StateManager.getInstance().setHost(true);
            Navigator.returnTo(GameActivity.class);
        }
    },
    GAME_CLIENT{
        @Override
        public void connected(Connection c) {
//            c.close();
        }

        @Override
        public void received(Connection c, Object object) {

        }

        @Override
        public void disconnected(Connection c) {
            StateManager.getInstance().changeState(MAIN);
        }

        @Override
        public void action(Object action) {

        }

        @Override
        public void prepare() {
            GravityBallServer.destroy();
            if(!GravityBallClient.isConnected()) {
                Log.e("GAME_CLIENT", "client is not connected");
                StateManager.getInstance().changeState(MAIN);
                return;
            }
            StateManager.getInstance().setHost(false);
            Navigator.returnTo(GameActivity.class);
        }
    };

    private final ArrayList<GameState> possible_next = new ArrayList<>();

    static{
        MAIN.possible_next.addAll(Arrays.asList(CHOOSE_LOBBY, LOBBY_OWNER));
        CHOOSE_LOBBY.possible_next.addAll(Arrays.asList(LOBBY_CLIENT, MAIN));
        LOBBY_CLIENT.possible_next.addAll(Arrays.asList(GAME_CLIENT, MAIN));
        GAME_CLIENT.possible_next.addAll(Arrays.asList(MAIN));
        LOBBY_OWNER.possible_next.addAll(Arrays.asList(GAME_OWNER, MAIN));
        GAME_OWNER.possible_next.addAll(Arrays.asList(MAIN));
    }

    public ArrayList<GameState> getPossibleNext() {
        return new ArrayList<>(possible_next);
    }
}
