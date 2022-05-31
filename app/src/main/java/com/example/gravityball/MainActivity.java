package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.esotericsoftware.minlog.Log;
import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;
import com.example.gravityball.utils.Navigator;

public class MainActivity extends AppCompatActivity {
    Button single, multi, lobby, scores, global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Navigator.initialize(getApplicationContext());

        loadViews();

        setListeners();

        Log.set(Log.LEVEL_DEBUG);

    }

    private void setListeners() {
        single.setOnClickListener(view -> runSelectLevelWithGoal(SelectLevelActivity.SelectLevelGoal.RUNNING_SINGLE_PLAYER));
        multi.setOnClickListener(view -> joinMulti());
        lobby.setOnClickListener(view -> runSelectLevelWithGoal(SelectLevelActivity.SelectLevelGoal.CREATING_LOBBY));
        scores.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, BestTimesActivity.class)));
        global.setOnClickListener(view -> runSelectLevelWithGoal(SelectLevelActivity.SelectLevelGoal.SELECTING_LEADERBOARD));
    }

    private void loadViews() {
        single = findViewById(R.id.button);
        multi = findViewById(R.id.JoinMultiplayer);
        lobby = findViewById(R.id.CreateLobby);
        scores = findViewById(R.id.scoresButton);
        global = findViewById(R.id.global);
    }
    private void joinMulti(){
        StateManager.getInstance().changeState(GameState.CHOOSE_LOBBY);
    }
    private void runSelectLevelWithGoal(SelectLevelActivity.SelectLevelGoal goal) {
        StateManager.getInstance().setSelectLevelGoal(goal);
        startActivity(new Intent(MainActivity.this, SelectLevelActivity.class));
    }
}