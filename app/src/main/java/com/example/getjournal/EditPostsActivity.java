package com.example.getjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.getjournal.Model.Posts;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostsActivity extends AppCompatActivity {

    private Button buttonCencel, btnEdit;
    EditText judul, doi, abstrak;
    TextInputLayout layoutJudul, layoutDoi, layoutAbstrak;
    ProgressDialog dialog;
    int position;
    String id, tokenLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_posts);
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);
        init();
    }


    public void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void init(){
        layoutJudul = (TextInputLayout)findViewById(R.id.txtJudulLayout);
        layoutDoi = (TextInputLayout)findViewById(R.id.txtDoiLayout);
        layoutAbstrak = (TextInputLayout)findViewById(R.id.txtAbstrakLayout);

        judul = (EditText)findViewById(R.id.txtJudul);
        doi = (EditText)findViewById(R.id.txtDoi);
        abstrak = (EditText)findViewById(R.id.txtAbstrak);

        getIncomingExtra();
        setData();

        dialog = new ProgressDialog(EditPostsActivity.this);
        dialog.setCancelable(false);

        buttonCencel = (Button) findViewById(R.id.CencelEdit);
        btnEdit = (Button)findViewById(R.id.btnEditJurnal);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    edit();
                }
            }
        });
        buttonCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();
            }
        });

        judul.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!judul.getText().toString().isEmpty()){
                    layoutJudul.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        doi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!doi.getText().toString().isEmpty()){
                    layoutDoi.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        abstrak.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!abstrak.getText().toString().isEmpty()){
                    layoutAbstrak.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean validate(){
        if(judul.getText().toString().isEmpty()){
            layoutJudul.setErrorEnabled(true);
            layoutJudul.setError("Judul is Required");
            return false;
        }
        if(doi.getText().toString().isEmpty()){
            layoutDoi.setErrorEnabled(true);
            layoutDoi.setError("Doi is Required");
            return false;
        }
        if(abstrak.getText().toString().isEmpty()){
            layoutAbstrak.setErrorEnabled(true);
            layoutAbstrak.setError("Abstrak is Required");
            return false;
        }
        return true;

    }

    private void getIncomingExtra() {

        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    private void edit(){
        dialog.setMessage("Updating Data");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS_UPDATE, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getBoolean("success")){
                    Posts posts = HomeActivity.Listposts.get(position);
                    posts.setJudul(judul.getText().toString());
                    posts.setDoi(doi.getText().toString());
                    posts.setAbstrak(abstrak.getText().toString());
                    HomeActivity.Listposts.set(position, posts);
                    HomeActivity.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeActivity.recyclerView.getAdapter().notifyDataSetChanged();

                    Intent intent = new Intent(this, DetailRiwayatPost.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + tokenLogin);
                return headers;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id",id);
                map.put("judul",judul.getText().toString());
                map.put("doi",doi.getText().toString());
                map.put("abstrak",abstrak.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void setData(){
        Posts posts = HomeActivity.Listposts.get(position);
        judul.setText(posts.getJudul());
        doi.setText(posts.getDoi());
        abstrak.setText(posts.getAbstrak());
        id = posts.getId();
    }
}