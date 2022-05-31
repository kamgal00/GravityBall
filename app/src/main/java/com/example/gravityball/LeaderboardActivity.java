package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gravityball.ranking.Network;
import com.example.gravityball.ranking.RankingClient;
import com.example.gravityball.state.StateManager;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {


    ListView listView;
    List<Network.ScoreMessage> entries;
    ArrayAdapter<Network.ScoreMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboardListView);

    }

    @Override
    protected void onResume() {
        super.onResume();


        new Thread(() -> {
            Network.Leaderboard leaderboard = null;
            try{
                leaderboard = RankingClient.getLeaderboardForLevel(StateManager.getInstance().getLevelName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(leaderboard== null) {
                return;
            }

            entries = leaderboard.list;
            adapter = new ArrayAdapter<>(
                    LeaderboardActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    entries
            );
            runOnUiThread(() -> listView.setAdapter(adapter));
        }).start();
    }
}