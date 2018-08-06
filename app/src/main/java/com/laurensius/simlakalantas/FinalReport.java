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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FinalReport extends AppCompatActivity {

    private EditText etKronologi, etKorban, etKerusakan, etTindakan;
    private Button btnKirim;

    private  TextView tvNotifikasi;

    private String url_api;
    private String report_insert;

    private String TAG;
    private String TAG_REQ_INS;

    private String incidentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_report);
        url_api = getResources().getString(R.string.url_api);
        report_insert = getResources().getString(R.string.endpoint_report_insert);
        TAG = AppController.class.getSimpleName();
        TAG_REQ_INS = getResources().getString(R.string.tag_request_f_report_insert);

        Intent i = getIntent();
        incidentId = i.getStringExtra(getResources().getString(R.string.intent_str_id));
        tvNotifikasi = (TextView)findViewById(R.id.tv_notifikasi);
        etKronologi = (EditText)findViewById(R.id.et_kronologi);
        etKorban = (EditText)findViewById(R.id.et_korban);
        etKerusakan  = (EditText)findViewById(R.id.et_kerusakan);
        etTindakan = (EditText)findViewById(R.id.et_tindakan);
        btnKirim= (Button)findViewById(R.id.btn_kirim);
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kronologi = etKronologi.getText().toString();
                String korban = etKorban.getText().toString();
                String kerusakan = etKerusakan.getText().toString();
                String tindakan = etTindakan.getText().toString();
                validateLogin(kronologi,korban,kerusakan,tindakan);
            }
        });
    }

    void validateLogin(String kronologi,String korban,String kerusakan,String tindakan){
        if(kronologi.equals(getResources().getString(R.string.param_no_text)) ||
        korban.equals(getResources().getString(R.string.param_no_text)) ||
        kerusakan.equals(getResources().getString(R.string.param_no_text)) ||
        tindakan.equals(getResources().getString(R.string.param_no_text)) ){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.notif_login_form_not_blank),
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_login_form_not_blank));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
        }else{
            kirimProcess(kronologi,korban,kerusakan,tindakan);
        }
    }

    void kirimProcess(String kronologi,String korban,String kerusakan,String tindakan){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_incident), incidentId);
        params.put(getResources().getString(R.string.param_chronology), kronologi);
        params.put(getResources().getString(R.string.param_accident_victim), korban);
        params.put(getResources().getString(R.string.param_damage), kerusakan);
        params.put(getResources().getString(R.string.param_action), tindakan);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_api.concat(report_insert), parameter,
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG_REQ_INS);
    }

    void validateLoginResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
            String message = jsonObject.getString(getResources().getString(R.string.json_tag_message));
            if(severity.equals(getResources().getString(R.string.severity_success))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severitySuccess));
                //finish();
                etKronologi.setText(getResources().getString(R.string.param_no_text));
                etKorban.setText(getResources().getString(R.string.param_no_text));
                etKerusakan.setText(getResources().getString(R.string.param_no_text));
                etTindakan.setText(getResources().getString(R.string.param_no_text));
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
