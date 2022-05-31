package com.example.gravityball.state;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gravityball.SelectLevelActivity;
import com.example.gravityball.networking.CharacterConnection;
import com.example.gravityball.networking.GravityBallClient;
import com.example.gravityball.networking.GravityBallServer;
import com.example.gravityball.networking.Network;

public class StateManager extends Listener {

    private GameState currentState = GameState.MAIN;

    private static final StateManager instance = new StateManager();

    public static StateManager getInstance(){
        return instance;
    }

    private StateManager(){}

    @Override
    public synchronized void connected(Connection c) {
        currentState.connected(c);
    }

    @Override
    public void received(Connection c, Object object) {
        if(object instanceof Network.ServerUpdate) {
            GravityBallClient.messageQueue.add((Network.ServerUpdate) object);
        }
        else if(object instanceof Network.ClientUpdate) {
            if(!(c instanceof CharacterConnection)) {
                return;
            }
            ((Network.ClientUpdate) object).id = ((CharacterConnection) c).id;
            GravityBallServer.messageQueue.add((Network.ClientUpdate) object);
        }
        else {
            synchronized (this) {
                currentState.received(c, object);
            }
        }
    }

    @Override
    public synchronized void disconnected(Connection c) {
        currentState.disconnected(c);
    }

    public synchronized void changeState(GameState newState) {
        if(newState == GameState.MAIN) {
            currentState = GameState.MAIN;
            currentState.prepare();
            return;
        }
        if(newState == currentState) return;
        if(!currentState.getPossibleNext().contains(newState)) {
            changeState(GameState.MAIN);
            return;
        }
        currentState = newState;
        currentState.prepare();
    }

    private String levelName, playerName;
    private int players, playerId;
    private SelectLevelActivity.SelectLevelGoal selectLevelGoal = SelectLevelActivity.SelectLevelGoal.RUNNING_SINGLE_PLAYER;

    public GameState getCurrentState() {
        return currentState;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public SelectLevelActivity.SelectLevelGoal getSelectLevelGoal() {
        return selectLevelGoal;
    }

    public void setSelectLevelGoal(SelectLevelActivity.SelectLevelGoal selectLevelGoal) {
        this.selectLevelGoal = selectLevelGoal;
    }
}
