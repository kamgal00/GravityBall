package com.example.gravityball;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;

import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor sensor;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeFullscreen();

        preventSleep();

        Point screenSize = getScreenSize();

        initializeGravitySensor();

        String levelName = loadLevelName();

        gameView = new GameView(this, screenSize.x, screenSize.y, levelName);

        setContentView(gameView);

        allowNetworking();
    }

    private void allowNetworking() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void preventSleep() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private String loadLevelName(){
        return StateManager.getInstance().getLevelName();
    }

    private void initializeGravitySensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @NonNull
    private Point getScreenSize() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    private void makeFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        sensorManager.unregisterListener(gameView, sensor);

        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        sensorManager.registerListener(gameView, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.caram);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void onBackPressed(){
        StateManager.getInstance().changeState(GameState.MAIN);
    }
}