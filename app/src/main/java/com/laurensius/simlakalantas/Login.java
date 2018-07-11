package com.laurensius.simlakalantas;

import com.laurensius.simlakalantas.appcontroller.AppController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private TextView tvDaftar, tvNotifikasi;
    private EditText etNamaPengguna, etKataSandi;
    private Button btnMasuk;

    private String url_api;
    private String endpoint_verfikasi;

    private String TAG;
    private String TAG_REQ_LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        url_api = getResources().getString(R.string.url_api);
        endpoint_verfikasi = getResources().getString(R.string.endpoint_verifikasi);
        TAG = AppController.class.getSimpleName();
        TAG_REQ_LOGIN = getResources().getString(R.string.tag_request_login);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        String sharedpref_data_user = sharedPreferences.getString(getResources().getString(R.string.sharedpref_data_user),null);
        if(sharedpref_data_user != null){
            try{
                JSONArray jsonArray = new JSONArray(sharedpref_data_user);
                String is_officer = jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_is_officer));
                if(Boolean.parseBoolean(is_officer)){
//                Intent i = new Intent(Login.this,AppOfficer.class);
//                startActivity(i);
//                finish();
                }else{
                    Intent i = new Intent(Login.this,AppPelapor.class);
                    startActivity(i);
                    finish();
                }
            }catch (JSONException e){}
        }
        etNamaPengguna = (EditText)findViewById(R.id.et_namapengguna);
        etKataSandi = (EditText)findViewById(R.id.et_katasandi);
        btnMasuk = (Button)findViewById(R.id.btn_masuk);
        tvDaftar = (TextView)findViewById(R.id.tv_daftar);
        tvNotifikasi = (TextView) findViewById(R.id.tv_notifikasi);
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etNamaPengguna.getText().toString();
                String password = etKataSandi.getText().toString();
                validateLogin(username,password);
            }
        });
    }

    void validateLogin(String username, String password){
        if(username.equals(getResources().getString(R.string.param_no_text)) ||
        password.equals(getResources().getString(R.string.param_no_text))){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.notif_login_form_not_blank),
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_login_form_not_blank));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
        }else{
            loginProcess(username,password);
        }
    }

    void loginProcess(String username,String password){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_username), username);
        params.put(getResources().getString(R.string.param_password), password);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_api.concat(endpoint_verfikasi), parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        pDialog.dismiss();
                        validateLoginResponse(response);
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

    void validateLoginResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
            String message = jsonObject.getString(getResources().getString(R.string.json_tag_message));
            JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
            if(severity.equals(getResources().getString(R.string.severity_success))){
                editorPreferences.putString(getResources().getString(R.string.sharedpref_data_user),data.toString());
                editorPreferences.commit();
                Intent i = new Intent(Login.this, AppPelapor.class);
                i.putExtra(getResources().getString(R.string.tag_extra_nav_beranda),getResources().getString(R.string.extra_nav_beranda));
                startActivity(i);
                finish();
            }else
            if(severity.equals(getResources().getString(R.string.severity_warning))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityDanger));
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
