package com.example.getjournal.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private int idNya;

    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "lastname")
    private String lastname;

    //
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    //
    public int getIdNya() {
        return idNya;
    }

    public void setIdNya(int idNya) {
        this.idNya = idNya;
    }

}

