package com.example.careercounsellingapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.careercounsellingapp.data.entities.CareerBenchmark;

import java.util.List;

@Dao
public interface CareerBenchmarkDao {
    @Insert
    void insertBenchmarks(List<CareerBenchmark> benchmarks);

    @Query("SELECT * FROM career_benchmarks")
    List<CareerBenchmark> getAllBenchmarks();
}