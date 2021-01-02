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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.getjournal.Utils.DataPart;
import com.example.getjournal.Utils.VolleyMultipartRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TambahPostActivity extends AppCompatActivity {

    private Button button, btnRegis, btnUpload;
    EditText judul, doi, abstrak;
    TextInputLayout layoutjudul, layoutdoi, layoutabstrak;
    private String tokenLogin;
    ProgressDialog dialog;
    private static final int PDF_UPLOAD_FILE = 2;
    private String uristring = null;
    byte[] fileData;
    String displayNamePDF;

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

    public void init() {
        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
        tokenLogin = userPref.getString("token", null);

        layoutjudul = (TextInputLayout) findViewById(R.id.txtJudulBaruLayout);
        layoutdoi = (TextInputLayout) findViewById(R.id.txtDoiJurnalLayout);
        layoutabstrak = (TextInputLayout) findViewById(R.id.txtAbstrakBaruLayout);

        judul = (EditText) findViewById(R.id.txtJudulBaru);
        doi = (EditText) findViewById(R.id.txtDoiBaru);
        abstrak = findViewById(R.id.txtAbstrakBaru);

        dialog = new ProgressDialog(TambahPostActivity.this);
        dialog.setCancelable(false);

        button = (Button) findViewById(R.id.Regcancel);
        btnRegis = (Button) findViewById(R.id.btnRegRegis);

        btnUpload = findViewById(R.id.buttonUploadFile);
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

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
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
                if (!judul.getText().toString().isEmpty()) {
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
                if (!doi.getText().toString().isEmpty()) {
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
                if (!abstrak.getText().toString().isEmpty()) {
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

    private boolean validate() {
        if (judul.getText().toString().isEmpty()) {
            layoutjudul.setErrorEnabled(true);
            layoutjudul.setError("Judul is Required");
            return false;
        }
        if (doi.getText().toString().isEmpty()) {
            layoutdoi.setErrorEnabled(true);
            layoutdoi.setError("Doi is Required");
            return false;
        }
        if (abstrak.getText().toString().isEmpty()) {
            layoutabstrak.setErrorEnabled(true);
            layoutabstrak.setError("Abstrak is Required");
            return false;
        }
        return true;

    }

    private void registrasi() {
        dialog.setMessage("Menambahkan Jurnal");
        dialog.show();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("allDevices");

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constant.POSTS_CREATE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject object1 = new JSONObject(new String(response.data));
                            if (object1.getInt("message_id") != 0) {
                                dialog.dismiss();
                                Intent intent = new Intent(TambahPostActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Tambah Jurnal Success", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Tambah Jurnal Failed", Toast.LENGTH_SHORT).show();
                        }
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
                map.put("judul", judul.getText().toString());
                map.put("doi", doi.getText().toString());
                map.put("abstrak", abstrak.getText().toString());
                return map;
            }
        };

//        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
        RequestQueue queue = Volley.newRequestQueue(TambahPostActivity.this);
        queue.add(volleyMultipartRequest);
    }

}
