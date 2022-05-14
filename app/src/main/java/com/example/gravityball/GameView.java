package com.example.gravityball;


import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.gravityball.drawing.GameDrawer;
import com.example.gravityball.drawing.ScaleCalculator;
import com.example.gravityball.world.GameBuilder;
import com.example.gravityball.world.GameWorld;

import org.jbox2d.common.Vec2;

public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    private final ScaleCalculator scaleCalculator;
    private final GameWorld gameWorld;
    private final GameDrawer gameDrawer;
    private Canvas canvas;

    private Thread thread;
    private boolean isPlaying;

    private final long mInterval = 10;
    private float timeStep = ((float) mInterval)/1000;

    private volatile Vec2 gravity =new Vec2();

    public GameView(GameActivity activity, int screenX, int screenY, String levelName) {
        super(activity);

        gameWorld = loadGameWorld(levelName);

        scaleCalculator =
                new ScaleCalculator(
                        gameWorld.worldWidth,
                        gameWorld.worldHeight,
                        screenX,
                        screenY,
                        0.95f);

        gameDrawer = new GameDrawer(gameWorld, scaleCalculator, getResources());

    }

    @NonNull
    private GameWorld loadGameWorld(String levelName) {
        final GameWorld gameWorld;
        try {
            gameWorld = GameBuilder.buildFromJSON(getResources(), levelName);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return gameWorld;
    }

    @Override
    public void run() {
        while (isPlaying) {
            update ();
            draw ();
            sleep ();
        }
    }

    private void update () {
        gameWorld.step(gravity, timeStep);
    }

    private void draw () {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();
            gameDrawer.draw(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep () {
        try {
            Thread.sleep(mInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        gravity.x =  sensorEvent.values[1];
        gravity.y = -sensorEvent.values[0];
        gravity = gravity.mul(15f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
