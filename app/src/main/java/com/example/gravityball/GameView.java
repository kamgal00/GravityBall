package com.example.gravityball;


import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.gravityball.database.AppDatabase;
import com.example.gravityball.database.BestScoreEnt;
import com.example.gravityball.drawing.GameDrawer;
import com.example.gravityball.drawing.ScaleCalculator;
import com.example.gravityball.networking.GravityBallClient;
import com.example.gravityball.networking.GravityBallServer;
import com.example.gravityball.networking.Network;
import com.example.gravityball.ranking.RankingClient;
import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;
import com.example.gravityball.world.GameBuilder;
import com.example.gravityball.world.GameWorld;

import org.jbox2d.common.Vec2;

public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    private final ScaleCalculator scaleCalculator;
    private final GameWorld gameWorld;
    private final GameDrawer gameDrawer;
    private Canvas canvas;
    private final GameState gameState;
    private final int players, playerId;
    private boolean isSaved = false;
    private final String levelName;

    private Thread thread;
    private boolean isPlaying;

    private final long UPS = 90;
    private final long MS_PER_UPDATE=1000/UPS;

    private volatile Vec2 gravity =new Vec2();

    public GameView(GameActivity activity, int screenX, int screenY, String levelName) {
        super(activity);

        this.levelName = levelName;

        gameState = StateManager.getInstance().getCurrentState();
        players = StateManager.getInstance().getPlayers();
        playerId = StateManager.getInstance().getPlayerId();

        gameWorld = loadGameWorld(levelName);

        scaleCalculator =
                new ScaleCalculator(
                        gameWorld.worldWidth,
                        gameWorld.worldHeight,
                        screenX,
                        screenY,
                        0.95f);

        gameDrawer = new GameDrawer(gameWorld, scaleCalculator, getResources(), players, playerId);

    }

    @NonNull
    private GameWorld loadGameWorld(String levelName) {
        final GameWorld gameWorld;
        try {
            boolean triggers = (gameState == GameState.MAIN || gameState == GameState.GAME_OWNER);
            Log.i("LOAD GAME", "triggers: "+triggers);
            gameWorld = GameBuilder.buildFromJSON(getResources(), levelName, players, triggers);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return gameWorld;
    }

    @Override
    public void run() {
        long previous = System.currentTimeMillis();
        long lagUpdate = 0;
        gameWorld.startTimers();
        while (isPlaying) {
            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;
            lagUpdate += elapsed;

            while(lagUpdate>=MS_PER_UPDATE) {
                updateControlsAndRemoteChanges();
                update();
                if(gameState == GameState.GAME_OWNER) {
                    GravityBallServer.sendUpdate(gameWorld.createServerUpdate());
                }
                lagUpdate-=MS_PER_UPDATE;
            }

            if(isGameFinished() && !isSaved){
                isSaved=true;
                saveScore();
            }

            draw();
        }
    }

    private void saveScore() {
        AppDatabase db = AppDatabase.createAppDatabase(getContext());
        long currentTime = gameWorld.times.get(playerId)-gameWorld.serverStartTime;

        com.example.gravityball.ranking.Network.ScoreMessage m = new com.example.gravityball.ranking.Network.ScoreMessage();
        m.playerName=StateManager.getInstance().getPlayerName();
        m.levelName=StateManager.getInstance().getLevelName();
        m.time = currentTime;
        new Thread(() -> {
            RankingClient.sendScore(m);
        }).start();

        BestScoreEnt lastBest = db.userDao().getById(levelName);
        if(lastBest ==null) {
            lastBest = new BestScoreEnt();
            lastBest.levelName = levelName;
            lastBest.time = currentTime;
            db.userDao().insertAll(lastBest);
        }
        else {
            if (lastBest.time > currentTime) {
                lastBest.time = currentTime;
                db.userDao().update(lastBest);
            }
        }
    }

    private void update () {
        gameWorld.step(((float)MS_PER_UPDATE)/1000);
        gameDrawer.update();
    }

    private void draw () {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();
            gameDrawer.draw(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }
    private void updateControlsAndRemoteChanges(){
        if(gameState == GameState.GAME_OWNER) {
            while(!GravityBallServer.messageQueue.isEmpty()){
                Network.ClientUpdate u = GravityBallServer.messageQueue.poll();
                if(u == null) continue;
                gameWorld.forces.set(u.id, u.force);
            }
        }
        if(gameState == GameState.GAME_CLIENT) {
            while(!GravityBallClient.messageQueue.isEmpty()) {
                Network.ServerUpdate u = GravityBallClient.messageQueue.poll();
                if(u == null) continue;
                gameWorld.forces = u.forces;
                gameWorld.adjust(u);
            }
        }
        gameWorld.forces.set(playerId, gravity);
    }
    public void resume () {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause () {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isGameFinished(){
        return gameWorld.times.get(playerId) != -1;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        gravity.x =  sensorEvent.values[1];
        gravity.y = -sensorEvent.values[0];
        gravity = gravity.mul(15f);
        if(gameState == GameState.GAME_CLIENT) {
            Network.ClientUpdate u = new Network.ClientUpdate();
            u.force=gravity;
            GravityBallClient.sendUpdate(u);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
