package com.example.getjournal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.getjournal.Model.Posts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailRiwayatPost extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    TextView judul, doi, abstrak;
    Button btnEdit, btnDelete, btndownload;
    int position, status;
    ProgressDialog dialog1;
    String id, idUser, idUser2, tokenLogin, file, downloadUrl = Constant.DOWNLOAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_posts);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        idUser2 = userPref.getString("id",null);
        tokenLogin = userPref.getString("token", null);
        init();
    }

    private void init(){
        try {
            Posts posts = HomeActivity.Listposts.get(position);
            id = posts.getId();
            idUser = posts.getId_user();
            Log.d("aaa", id);
            Log.d("bbb", idUser2);


            judul = (TextView)findViewById(R.id.txtJurnal);
            doi = (TextView)findViewById(R.id.txtDoiJurnal);
            abstrak = (TextView)findViewById(R.id.txtAbstrakJurnal);

        }catch (Exception e ){
            Posts posts = HomeActivity.Listpostsbackup.get(position);
            id = posts.getId();
            idUser = posts.getId_user();
            Log.d("aaa", id);
            Log.d("bbb", idUser2);


            judul = (TextView)findViewById(R.id.txtJurnal);
            doi = (TextView)findViewById(R.id.txtDoiJurnal);
            abstrak = (TextView)findViewById(R.id.txtAbstrakJurnal);
        }


        btnEdit = (Button)findViewById(R.id.editrwtrgs);
        btnDelete = (Button)findViewById(R.id.delrwtrgs);
        btndownload = findViewById(R.id.btndownload);

        dialog1 = new ProgressDialog(DetailRiwayatPost.this);
        dialog1.setCancelable(false);

        getIncomingExtra();
        if(status==0){
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
//        Log.d("3CCC", String.valueOf(status));

        setDetail();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailRiwayatPost.this);
                builder.setTitle("Confirm");
                builder.setMessage("Delete Registrasi?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog1.setMessage("Deleting Data");
                        dialog1.show();
                        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS_DELETE, response -> {
                            try {
                                JSONObject object1 = new JSONObject(response);
                                if (object1.getBoolean("success")){
                                    HomeActivity.recyclerView.getAdapter().notifyItemRemoved(position);
                                    HomeActivity.recyclerView.getAdapter().notifyDataSetChanged();

                                    Intent intent = new Intent(DetailRiwayatPost.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Delete Jurnal Success", Toast.LENGTH_SHORT).show();
                                }else if(!object1.getBoolean("success")){

                                    Toast.makeText(getApplicationContext(), "Anda Tidak Berhak", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Delete Jurnal Failed", Toast.LENGTH_SHORT).show();
                            }
                            dialog1.dismiss();
                        },error -> {
                            error.printStackTrace();
                            dialog1.dismiss();
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                // Basic Authentication
                                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                                headers.put("Authorization", "Bearer " + tokenLogin);
                                return headers;
                            }

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id",id);
                                return map;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(request);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailRiwayatPost.this, EditPostsActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("position", getIntent().getIntExtra("position", 0));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //if denied, grant it
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        //popup
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                    }else{
                        startDownload();
                    }
                }else{

                }
                Toast.makeText(getApplicationContext(), "Downloading", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getIncomingExtra() {

        if(getIntent().hasExtra("position")){
            position = getIntent().getIntExtra("position", 0);
            status = getIntent().getIntExtra("status",0);
        }
    }

    private void setDetail(){
        try {
            Posts posts = HomeActivity.Listposts.get(position);
            judul.setText(posts.getJudul());
            doi.setText(posts.getDoi());
            abstrak.setText(posts.getAbstrak());
            id = posts.getId();
            downloadUrl = downloadUrl+posts.getFile();
        }catch (Exception e){
            Posts posts = HomeActivity.Listpostsbackup.get(position);
            judul.setText(posts.getJudul());
            doi.setText(posts.getDoi());
            abstrak.setText(posts.getAbstrak());
            id = posts.getId();
            downloadUrl = downloadUrl+posts.getFile();
        }

    }

    private void startDownload(){
        //download req from url
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        //ijin koneksi wifi dan data
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//        request.setTitle("Download");
//        request.setDescription("Downloading Journal...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, System.currentTimeMillis()+".pdf"); //get datetime untuk nama file nantinya

        //download service
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    //permission result handler

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownload();
                }else{
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
