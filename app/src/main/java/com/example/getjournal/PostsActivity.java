//package com.example.getjournal;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.getjournal.Adapter.AdapterPosts;
//import com.example.getjournal.Model.Posts;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class PostsActivity extends AppCompatActivity {
//
//    public  static RecyclerView recyclerView;
//    AdapterPosts adapter1;
//    private String tokenLogin;
//    public static ArrayList<Posts> Listposts = new ArrayList<>();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_posts);
//        recyclerView = findViewById(R.id.recycler1);
//        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
//        tokenLogin = userPref.getString("token", null);
//
//        getPosts();
//    }
//
//    private void getPosts(){
//        Listposts.clear();
//        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS, response -> {
//            try {
//                JSONObject object1 = new JSONObject(response);
//                if (object1.getBoolean("success")) {
//                    JSONArray user = new JSONArray(object1.getString("posts"));
//                    for (int i=0; i<user.length(); i++){
//                        JSONObject daftar = user.getJSONObject(i);
//
//                        Posts posts = new Posts();
//                        posts.setId(daftar.getString("id"));
//                        posts.setId_user(daftar.getString("id_user"));
//                        posts.setAbstrak(daftar.getString("abstrak"));
//                        posts.setDoi(daftar.getString("doi"));
//                        posts.setFile(daftar.getString("file"));
//                        posts.setJudul(daftar.getString("judul"));
//
//
//                        Listposts.add(posts);
//                    }
//                    adapter1 = new AdapterPosts(Listposts, getApplicationContext());
//                    recyclerView.setAdapter(adapter1);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Get Failed Failed", Toast.LENGTH_SHORT).show();
//            }
//        },error -> {
//            error.printStackTrace();
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                // Basic Authentication
//                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);
//
//                headers.put("Authorization", "Bearer " + tokenLogin);
//                return headers;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        queue.add(request);
//    }
//}
