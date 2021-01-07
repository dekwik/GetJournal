package com.example.getjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.getjournal.Model.Posts;
import com.example.getjournal.Utils.DataPart;
import com.example.getjournal.Utils.VolleyMultipartRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class EditPostsActivity extends AppCompatActivity {

    private Button buttonCencel, btnEdit,btnUpload;
    EditText judul, doi, abstrak;
    TextInputLayout layoutJudul, layoutDoi, layoutAbstrak;
    ProgressDialog dialog;
    int position;
    String id, tokenLogin;

    private static final int PDF_UPLOAD_FILE = 2;
    private String uristring = null;
    byte[] fileData;
    String displayNamePDF;

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

        btnUpload = findViewById(R.id.buttonEditFile);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "UPLOAD PDF", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("application/pdf");
                startActivityForResult(i, PDF_UPLOAD_FILE);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("nameeeee>>>>  ", displayName);
                        fileData = getPDF(displayName, uri);
                        displayNamePDF = displayName;
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
                Log.d("nameeeee>>>>  ", displayName);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private byte[] getPDF(String displayName, Uri uri) {
        InputStream iStream = null;
        byte[] inputData = null;
        try {
            iStream = getContentResolver().openInputStream(uri);
            inputData = getBytes(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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
    private void edit() {
        dialog.setMessage("Updating Data");
        dialog.show();
        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, Constant.POSTS_UPDATE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject object1 = new JSONObject(new String(response.data));
                            if (object1.getBoolean("success")){
                                Posts posts = HomeActivity.Listposts.get(position);
                                posts.setJudul(judul.getText().toString());
                                posts.setDoi(doi.getText().toString());
                                posts.setAbstrak(abstrak.getText().toString());
                                HomeActivity.Listposts.set(position, posts);
                                HomeActivity.recyclerView.getAdapter().notifyItemChanged(position);
                                HomeActivity.recyclerView.getAdapter().notifyDataSetChanged();


                                Intent i = new Intent(EditPostsActivity.this, HomeActivity.class);
                                i.putExtra("position", position);
                                startActivity(i);
                                finish();
                                Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> map = new HashMap<>();
                long filename = System.currentTimeMillis();
                map.put("file", new DataPart(filename + ".pdf", fileData));
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + tokenLogin);
                return headers;
            }

            //tambahkan parameter
            @Override
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