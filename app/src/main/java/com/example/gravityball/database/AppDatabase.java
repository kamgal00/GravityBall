package com.example.gravityball.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {BestScoreEnt.class},
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BestScoreDao userDao();

    public static AppDatabase createAppDatabase(Context x) {
        return Room.databaseBuilder(x,
                        AppDatabase.class, "database-name")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}
