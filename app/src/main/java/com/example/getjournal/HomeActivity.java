package com.example.getjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.getjournal.Adapter.AdapterPosts;
import com.example.getjournal.Database.RoomDB;
import com.example.getjournal.Model.Posts;
import com.example.getjournal.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity {
    public  static RecyclerView recyclerView;
    AdapterPosts adapter1;
    AdapterPosts adapter2;
    private String tokenLogin;
    public static ArrayList<Posts> Listposts = new ArrayList<>();
    public static ArrayList<Posts> Listpostsbackup = new ArrayList<>();
    private ArrayList<User> userListBackup;
    private ArrayList<User> listUser;
    private FloatingActionButton fab;
    RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler2);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);

        getPosts();


        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        overridePendingTransition(0,0);
                        break;
                    case R.id.nav_user:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
        fab.setOnClickListener(v-> {
            Intent intent = new Intent(this, TambahPostActivity.class);
            startActivity(intent);
            Toast.makeText(this, "MAU NIH GAN", Toast.LENGTH_LONG).show();
        });

    }

    private void getPosts() {
        Listposts = new ArrayList<>();
        listUser = new ArrayList<>();
        Listpostsbackup = new ArrayList<>();
        userListBackup = new ArrayList<>();
        database = RoomDB.getInstance(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")) {
                    database.postDao().deleteAll();
                    database.userDao().deleteAll();
                    JSONArray array = new JSONArray(object1.getString("post"));
                    for (int i=0; i<array.length(); i++){
                        JSONObject daftar = array.getJSONObject(i);
                        JSONObject userObject = daftar.getJSONObject("user");

                        User user = new User();
                        user.setIdNya(i);
                        user.setId(userObject.getInt("id"));
                        user.setName(userObject.getString("name"));
                        user.setLastname(userObject.getString("lastname"));
                        listUser.add(user);
                        database.userDao().insertUser(user);

                        Posts posts = new Posts();
                        posts.setId(daftar.getString("id"));
                        posts.setId_user(daftar.getString("user_id"));
                        posts.setDate(daftar.getString("created_at"));
                        posts.setAbstrak(daftar.getString("abstrak"));
                        posts.setDoi(daftar.getString("doi"));
                        posts.setFile(daftar.getString("file"));
                        posts.setJudul(daftar.getString("judul"));

                        Listposts.add(posts);
                        database.postDao().insertPendaftaran(posts);
                    }
                    adapter1 = new AdapterPosts(Listposts, listUser,getApplicationContext());
                    recyclerView.setAdapter(adapter1);
                }else {
                    Toast.makeText(getApplicationContext(), "Get", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Get Data Failed", Toast.LENGTH_SHORT).show();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Listpostsbackup = (ArrayList<Posts>) database.postDao().loadAllPosts();
                    userListBackup = (ArrayList<User>) database.userDao().loadAllUsers();
                    adapter2 = new AdapterPosts(Listpostsbackup,userListBackup, getApplicationContext());
                    recyclerView.setAdapter(adapter2);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + tokenLogin);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}