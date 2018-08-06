package com.laurensius.simlakalantas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.appcontroller.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private TextView tvMasuk, tvNotifikasi;
    private EditText etNamaPengguna, etKataSandi, etNamaLengkap, etAlamat, etTelepon, etEmail;
    private Button btnDaftar;

    private String url_api;
    private String endpoint_user_register;

    private String TAG;
    private String TAG_REQ_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        url_api = getResources().getString(R.string.url_api);
        endpoint_user_register = getResources().getString(R.string.endpoint_user_register);
        TAG = AppController.class.getSimpleName();
        TAG_REQ_LOGIN = getResources().getString(R.string.tag_request_login);
        etNamaPengguna = (EditText)findViewById(R.id.et_namapengguna);
        etKataSandi = (EditText)findViewById(R.id.et_katasandi);
        etNamaLengkap = (EditText)findViewById(R.id.et_namalengkap);
        etAlamat = (EditText)findViewById(R.id.et_alamat);
        etTelepon = (EditText)findViewById(R.id.et_telepon);
        etEmail = (EditText)findViewById(R.id.et_email);
        btnDaftar = (Button)findViewById(R.id.btn_daftar);
        tvMasuk = (TextView)findViewById(R.id.tv_masuk);
        tvMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        tvNotifikasi = (TextView) findViewById(R.id.tv_notifikasi);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etNamaPengguna.getText().toString();
                String password = etKataSandi.getText().toString();
                String full_name = etNamaLengkap.getText().toString();
                String address = etAlamat.getText().toString();
                String phone = etTelepon.getText().toString();
                String email = etEmail.getText().toString();
                validateRegister(username,password,full_name,address,phone,email);
            }
        });
    }

    void validateRegister(String username, String password, String full_name, String address, String phone, String email){
        if(username.equals(getResources().getString(R.string.param_no_text)) ||
        password.equals(getResources().getString(R.string.param_no_text)) ||
        full_name.equals(getResources().getString(R.string.param_no_text)) ||
        address.equals(getResources().getString(R.string.param_no_text)) ||
        phone.equals(getResources().getString(R.string.param_no_text)) ||
        email.equals(getResources().getString(R.string.param_no_text))){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.notif_login_form_not_blank),
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_login_form_not_blank));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
        }else{
            registerProcess(username,password,full_name,address,phone,email);
        }
    }

    void registerProcess(String username, String password, String full_name, String address, String phone, String email){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_username), username);
        params.put(getResources().getString(R.string.param_password), password);
        params.put(getResources().getString(R.string.param_full_name), full_name);
        params.put(getResources().getString(R.string.param_address), address);
        params.put(getResources().getString(R.string.param_phone), phone);
        params.put(getResources().getString(R.string.param_email), email);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_api.concat(endpoint_user_register), parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        pDialog.dismiss();
                        validateRegisterResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        tvNotifikasi.setVisibility(View.VISIBLE);
                        tvNotifikasi.setText(getResources().getString(R.string.notif_error_connection));
                        tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityDanger));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG_REQ_LOGIN);
    }

    void validateRegisterResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
            String message = jsonObject.getString(getResources().getString(R.string.json_tag_message));
            JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
            if(severity.equals(getResources().getString(R.string.severity_success))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severitySuccess));
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
            }else
            if(severity.equals(getResources().getString(R.string.severity_warning))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
            }else
            if(severity.equals(getResources().getString(R.string.severity_danger))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityDanger));
            }
        }catch (JSONException e){
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_error_json_response));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityDanger));
        }
    }

}
