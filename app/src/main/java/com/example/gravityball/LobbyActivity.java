package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.esotericsoftware.minlog.Log;
import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;

import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    Button start;
    TextView waitView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Log.set(Log.LEVEL_DEBUG);

        allowNetworking();

        waitView = findViewById(R.id.waitingText);
        start = findViewById(R.id.startButton);

        start.setOnClickListener(view -> StateManager.getInstance().changeState(GameState.GAME_OWNER));

        if(StateManager.getInstance().getCurrentState() == GameState.LOBBY_OWNER) {
            waitView.setVisibility(View.INVISIBLE);
        }
        else {
            start.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    public void onBackPressed(){
        StateManager.getInstance().changeState(GameState.MAIN);
    }


    private void allowNetworking() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}