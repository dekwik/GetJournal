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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TambahPostActivity extends AppCompatActivity {

    private Button button, btnRegis;
    EditText judul, doi, abstrak;
    TextInputLayout layoutjudul, layoutdoi, layoutabstrak;
    private String tokenLogin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahposts);
        init();
    }


    public void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void init(){
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);

        layoutjudul = (TextInputLayout)findViewById(R.id.txtJudulBaruLayout);
        layoutdoi = (TextInputLayout)findViewById(R.id.txtDoiJurnalLayout);
        layoutabstrak = (TextInputLayout)findViewById(R.id.txtAbstrakBaruLayout);

        judul = (EditText)findViewById(R.id.txtJudulBaru);
        doi = (EditText)findViewById(R.id.txtDoiBaru);
        abstrak = findViewById(R.id.txtAbstrakBaru);

        dialog = new ProgressDialog(TambahPostActivity.this);
        dialog.setCancelable(false);

        button = (Button) findViewById(R.id.Regcancel);
        btnRegis = (Button)findViewById(R.id.btnRegRegis);


        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    registrasi();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();
            }
        });

        judul.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!judul.getText().toString().isEmpty()){
                    layoutjudul.setErrorEnabled(false);
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
                    layoutdoi.setErrorEnabled(false);
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
                    layoutabstrak.setErrorEnabled(false);
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
            layoutjudul.setErrorEnabled(true);
            layoutjudul.setError("Judul is Required");
            return false;
        }
        if(doi.getText().toString().isEmpty()){
            layoutdoi.setErrorEnabled(true);
            layoutdoi.setError("Doi is Required");
            return false;
        }
        if(abstrak.getText().toString().isEmpty()){
            layoutabstrak.setErrorEnabled(true);
            layoutabstrak.setError("Abstrak is Required");
            return false;
        }
        return true;

    }

    private void registrasi(){
        dialog.setMessage("Menambahkan Jurnal");
        dialog.show();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("allDevices");
        StringRequest request = new StringRequest(Request.Method.POST, Constant.POSTS_CREATE, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                if (object1.getInt("message_id")!=0){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Tambah Jurnal Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Tambah Jurnal Failed", Toast.LENGTH_SHORT).show();
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
                map.put("judul",judul.getText().toString());
                map.put("doi",doi.getText().toString());
                map.put("abstrak",abstrak.getText().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}
