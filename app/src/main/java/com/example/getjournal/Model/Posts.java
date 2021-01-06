package com.example.getjournal.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "posts")
public class Posts {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @ColumnInfo(name = "id_user")
    private String id_user;

    @ColumnInfo(name = "abstrak")
    private String abstrak;

    @ColumnInfo(name = "file")
    private String file;

    @ColumnInfo(name = "judul")
    private String judul;

    @ColumnInfo(name = "doi")
    private String doi;

    @ColumnInfo(name = "created_at")
    private String date;


    //id
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    //id_user
    public String getId_user(){
        return id_user;
    }

    public void setId_user(String id_user){
        this.id_user= id_user;
    }

    //abstrak
    public String getAbstrak(){
        return abstrak;
    }

    public void setAbstrak(String abstrak){
        this.abstrak= abstrak;
    }

    //doi
    public String getDoi(){
        return doi;
    }

    public void setDoi(String doi){
        this.doi = doi;
    }

    //judul
    public String getJudul(){
        return judul;
    }

    public void setJudul(String judul){
        this.judul = judul;
    }

    //file
    public String getFile(){
        return file;
    }

    public void setFile(String file){
        this.file = file;
    }

    //date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

