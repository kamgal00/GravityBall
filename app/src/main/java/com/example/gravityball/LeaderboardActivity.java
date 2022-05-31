package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gravityball.networking.GravityBallClient;
import com.example.gravityball.ranking.Network;
import com.example.gravityball.ranking.RankingClient;
import com.example.gravityball.state.StateManager;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

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


        new Thread(new Runnable() {
            @Override
            public void run() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

                Log.e("LEADERBOARD ACTIVITY", "entering thread");
                Network.Leaderboard leaderboard = null;
                try{
                    leaderboard = RankingClient.getLeaderboardForLevel(StateManager.getInstance().getLevelName());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("LEADERBOARD ACTIVITY", "after network");
                if(leaderboard== null) {
                    Log.e("LEADERBOARD INFO", "LEADERBOARD IS NULL");
                    return;
                }
                Log.e("LEADERBOARD INFO", leaderboard.toString());
                entries = leaderboard.list;
                Log.i("LEADERBOARD INFO", leaderboard.list.toString());
                adapter = new ArrayAdapter<>(
                        LeaderboardActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        entries
                );
                runOnUiThread(() -> listView.setAdapter(adapter));
//                listView.setAdapter(adapter);
            }
        }).start();
    }
}