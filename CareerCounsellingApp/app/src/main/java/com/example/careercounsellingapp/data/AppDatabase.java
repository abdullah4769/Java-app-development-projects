package com.example.careercounsellingapp.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.careercounsellingapp.data.dao.CareerBenchmarkDao;
import com.example.careercounsellingapp.data.dao.QuestionDao;
import com.example.careercounsellingapp.data.dao.UserResultDao;
import com.example.careercounsellingapp.data.entities.CareerBenchmark;
import com.example.careercounsellingapp.data.entities.Option;
import com.example.careercounsellingapp.data.entities.Question;
import com.example.careercounsellingapp.data.entities.UserResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Question.class, Option.class, CareerBenchmark.class, UserResult.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuestionDao questionDao();
    public abstract UserResultDao userResultDao();
    public abstract CareerBenchmarkDao careerBenchmarkDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pathfinder_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Populate the database in the background
            databaseWriteExecutor.execute(() -> {
                AppDatabase database = INSTANCE;
                DatabaseInitializer.populateAsync(database);
            });
        }
    };
}