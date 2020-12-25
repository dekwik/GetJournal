package com.example.getjournal.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.getjournal.Model.Posts;

import java.util.List;

@Dao
public interface PostDao {
    @Transaction
    @Query("SELECT * FROM posts ORDER BY ID")
    List<Posts> loadAllPosts();

    @Insert
    void insertPendaftaran(Posts posts);

    @Query("DELETE FROM posts")
    void deleteAll();
}
