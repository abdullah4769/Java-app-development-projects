package com.example.careercounsellingapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.careercounsellingapp.data.entities.UserResult;

import java.util.List;

@Dao
public interface UserResultDao {
    @Insert
    void insertResult(UserResult result);

    @Query("SELECT * FROM user_results ORDER BY timestamp DESC")
    LiveData<List<UserResult>> getAllResults();
}