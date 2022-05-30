package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gravityball.database.AppDatabase;
import com.example.gravityball.database.BestScoreDao;
import com.example.gravityball.database.BestScoreEnt;

import java.util.List;

public class BestTimesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_times);

        ListView ls = findViewById(R.id.BestScoresListView);

        AppDatabase db = AppDatabase.createAppDatabase(getApplicationContext());

        List<BestScoreEnt> scores = db.userDao().getAll();
        ArrayAdapter<BestScoreEnt> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                scores
        );
        ls.setAdapter(adapter);

    }
}