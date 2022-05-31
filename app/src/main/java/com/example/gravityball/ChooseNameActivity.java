package com.example.gravityball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.gravityball.state.StateManager;

public class ChooseNameActivity extends AppCompatActivity {

    EditText name;
    Button enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);

        name = findViewById(R.id.name);
        enter = findViewById(R.id.enterMenu);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("name", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String sName = sp.getString("name", "anonymous");
        name.setText(sName);

        enter.setOnClickListener(view->{
            String currName = name.getText().toString();
            editor.putString("name", currName);
            editor.apply();
            StateManager.getInstance().setPlayerName(currName);
            startActivity(new Intent(ChooseNameActivity.this, MainActivity.class));
        });

    }
}