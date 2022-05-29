package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.esotericsoftware.minlog.Log;
import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;

public class MainActivity extends AppCompatActivity {
    Button single, multi, lobby;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Navigator.initialize(getApplicationContext());

        single = findViewById(R.id.button);
        multi = findViewById(R.id.JoinMultiplayer);
        lobby = findViewById(R.id.CreateLobby);

        single.setOnClickListener(view -> startSingle());
        multi.setOnClickListener(view -> joinMulti());
        lobby.setOnClickListener(view -> createLobby());

        Log.set(Log.LEVEL_DEBUG);

    }

    private void startSingle() {
        StateManager.getInstance().setCreatingLobby(false);
        startActivity(new Intent(MainActivity.this, SelectLevelActivity.class));
    }
    private void joinMulti(){
        StateManager.getInstance().changeState(GameState.CHOOSE_LOBBY);
    }
    private void createLobby(){
        StateManager.getInstance().setCreatingLobby(true);
        startActivity(new Intent(MainActivity.this, SelectLevelActivity.class));
    }
}