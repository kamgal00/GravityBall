package com.example.gravityball.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.gravityball.drawing.GameDrawer;

import java.io.Serializable;

@Entity
public class BestScoreEnt {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "levelName")
    public String levelName;

    @ColumnInfo(name = "time")
    public long time;

    @Override
    public String toString(){
        return levelName+" : "+ GameDrawer.millisToString(time);
    }
}
