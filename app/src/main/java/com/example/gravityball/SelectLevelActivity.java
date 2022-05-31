package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;
import com.example.gravityball.utils.ResourcesUtils;

import java.util.ArrayList;

public class SelectLevelActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> levels;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
        initialize();
    }

    private void initialize() {
        lv = findViewById(R.id.level_list_view);
        levels = ResourcesUtils.getLevels();
        lv.setOnItemClickListener(
                (adapterView, view, i, l) -> startGame(levels.get(i))
        );

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                levels
        );
        lv.setAdapter(arrayAdapter);
    }

    private void startGame(String name) {
        StateManager.getInstance().setLevelName(name);

        StateManager.getInstance().getSelectLevelGoal().onStart(this);

    }

    public enum SelectLevelGoal {
        CREATING_LOBBY {
            @Override
            public void onStart(SelectLevelActivity a) {
                StateManager.getInstance().changeState(GameState.LOBBY_OWNER);
            }
        },
        SELECTING_LEADERBOARD {
            @Override
            public void onStart(SelectLevelActivity a) {
                Intent intent = new Intent(a.getApplicationContext(), LeaderboardActivity.class);
                a.startActivity(intent);
            }
        },
        RUNNING_SINGLE_PLAYER{
            @Override
            public void onStart(SelectLevelActivity a) {
                Intent intent = new Intent(a.getApplicationContext(), GameActivity.class);
                StateManager.getInstance().setPlayerId(0);
                StateManager.getInstance().setPlayers(1);
                a.startActivity(intent);
            }
        };

        public abstract void onStart(SelectLevelActivity a);
    }
}