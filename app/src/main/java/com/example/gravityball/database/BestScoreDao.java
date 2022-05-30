package com.example.gravityball.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BestScoreDao {
    @Query("SELECT * FROM bestscoreent")
    List<BestScoreEnt> getAll();

    @Query("SELECT * FROM bestscoreent WHERE levelName = :level_name LIMIT 1")
    BestScoreEnt getById(String level_name);

    @Insert
    void insertAll(BestScoreEnt ... users);

    @Update
    void update(BestScoreEnt user);

}