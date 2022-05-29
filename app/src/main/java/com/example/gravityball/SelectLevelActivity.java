package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gravityball.state.GameState;
import com.example.gravityball.state.StateManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
        levels = getLevels();
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

    private ArrayList<String> getLevels(){
        ArrayList<String> out = new ArrayList<>();
        Pattern levelPattern = Pattern.compile("level[0-9]*");

        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            String file = fields[count].getName();
            if(levelPattern.matcher(file).matches()) {
                out.add(file);
            }
        }

        return out;
    }

    private void startGame(String name) {
        StateManager.getInstance().setLevelName(name);
        if(StateManager.getInstance().isCreatingLobby()) {
            StateManager.getInstance().changeState(GameState.LOBBY_OWNER);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            StateManager.getInstance().setPlayerId(0);
            StateManager.getInstance().setPlayers(1);
            startActivity(intent);
        }

    }
}