package com.example.getjournal.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.getjournal.Dao.PostDao;
import com.example.getjournal.Dao.UserDao;
import com.example.getjournal.Model.Posts;
import com.example.getjournal.Model.User;

@Database(entities = {Posts.class, User.class},version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static com.example.getjournal.Database.RoomDB database;
    private static String DATABASE_NAME = "database";

    public synchronized static com.example.getjournal.Database.RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), com.example.getjournal.Database.RoomDB.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return database;
    }
    public abstract PostDao postDao();
    public abstract UserDao userDao();
}
