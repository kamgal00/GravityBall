package com.example.gravityball;


import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceView;

import com.example.gravityball.drawing.GameDrawer;
import com.example.gravityball.drawing.ScaleCalculator;
import com.example.gravityball.world.GameWorld;

import org.jbox2d.common.Vec2;

public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    private final ScaleCalculator scaleCalculator;
    private final GameWorld gameWorld;
    private final GameDrawer gameDrawer;
    private Canvas canvas;


    private final int worldWidth = 20, worldHeight = 10;

    private Thread thread;
    private boolean isPlaying;

    private final long mInterval = 10;
    private float timeStep = ((float) mInterval)/1000;

    private volatile Vec2 gravity =new Vec2();

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        scaleCalculator =
                new ScaleCalculator(worldWidth, worldHeight, screenX, screenY, 0.95f);

        gameWorld =
                new GameWorld(0.63f, new Vec2(1,9), worldWidth, worldHeight);

        gameDrawer = new GameDrawer(gameWorld, scaleCalculator, getResources());

        createWorld();

        gameDrawer.prepareStaticObjects();

    }

    private void createWorld(){
        gameWorld.addWall(new Vec2(0,8), new Vec2(6,7));
        gameWorld.addWall(new Vec2(8,10), new Vec2(9,5));
        gameWorld.addWall(new Vec2(2,5), new Vec2(6,4));
        gameWorld.addWall(new Vec2(0,2), new Vec2(3,0));
        gameWorld.addWall(new Vec2(5,4), new Vec2(6,2));
        gameWorld.addWall(new Vec2(6,3), new Vec2(11,2));
        gameWorld.addWall(new Vec2(11,8), new Vec2(12,2));
        gameWorld.addWall(new Vec2(12,8), new Vec2(15,7));
        gameWorld.addWall(new Vec2(14,10), new Vec2(15,8));
        gameWorld.addWall(new Vec2(14,5), new Vec2(15,0));
        gameWorld.addWall(new Vec2(15,5), new Vec2(18,4));
        gameWorld.addWall(new Vec2(17,8), new Vec2(18,5));
        gameWorld.addWall(new Vec2(17,2), new Vec2(20,0));

        gameWorld.addObstacle(new Vec2(2f, 8.25f), new Vec2(6, 8), new Vec2(1,9));
        gameWorld.addObstacle(new Vec2(7.75f, 10f), new Vec2(8, 5), new Vec2(1,9));
//        gameWorld.addObstacle(new Vec2(2f, 7f), new Vec2(6, 6.75f), new Vec2(1,9));
//        gameWorld.addObstacle(new Vec2(2f, 5.25f), new Vec2(6, 5f), new Vec2(1,9));

        gameWorld.addObstacle(new Vec2(3f, 0.25f), new Vec2(6, 0), new Vec2(1,3));
        gameWorld.addObstacle(new Vec2(7f, 2f), new Vec2(10, 1.75f), new Vec2(1,3));
        gameWorld.addObstacle(new Vec2(11f, 0.25f), new Vec2(14, 0), new Vec2(1,3));
        gameWorld.addObstacle(new Vec2(12f, 7f), new Vec2(14, 6.75f), new Vec2(1,3));
        gameWorld.addObstacle(new Vec2(15f, 5.25f), new Vec2(17, 5), new Vec2(1,3));

        gameWorld.addObstacle(new Vec2(9f, 8f), new Vec2(9.10f, 5), new Vec2(13,9));
        gameWorld.addObstacle(new Vec2(10.90f, 8f), new Vec2(11f, 5), new Vec2(13,9));
        gameWorld.addObstacle(new Vec2(18f, 8f), new Vec2(18.10f, 5), new Vec2(13,9));
        gameWorld.addObstacle(new Vec2(19.90f, 8f), new Vec2(20f, 5), new Vec2(13,9));

        gameWorld.addTreasure(new Vec2(15, 1.5f), new Vec2(17, 0));
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
