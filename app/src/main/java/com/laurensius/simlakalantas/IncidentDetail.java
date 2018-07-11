package com.laurensius.simlakalantas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.model.Incident;
import com.laurensius.simlakalantas.model.PoliceStation;
import com.laurensius.simlakalantas.model.Stage;
import com.laurensius.simlakalantas.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IncidentDetail extends AppCompatActivity {

    private LinearLayout llWraper, llLoadFailed, llLoadSuccess;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvReceivedAt;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvSender;
    private TextView tvSenderAddress;
    private TextView tvPhone;
    private TextView tvStage;
    private TextView tvStageDatetime;
    private TextView tvStation;
    private TextView tvProcessedBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail);
        llWraper = (LinearLayout)findViewById(R.id.ll_wrapper) ;
        llLoadFailed = (LinearLayout)findViewById(R.id.ll_load_failed) ;
        llLoadSuccess = (LinearLayout)findViewById(R.id.ll_load_success) ;
        ivImage = (ImageView)findViewById(R.id.iv_image);
        tvDescription = (TextView)findViewById(R.id.tv_description);
        tvReceivedAt = (TextView)findViewById(R.id.tv_received_at);
        tvLatitude = (TextView)findViewById(R.id.tv_latitude);
        tvLongitude = (TextView)findViewById(R.id.tv_longitude);
        tvSender = (TextView)findViewById(R.id.tv_sender);
        tvSenderAddress  = (TextView)findViewById(R.id.tv_sender_address);
        tvPhone  = (TextView)findViewById(R.id.tv_phone);
        tvStage = (TextView)findViewById(R.id.tv_stage);
        tvStageDatetime  = (TextView)findViewById(R.id.tv_stage_datetime);
        tvStation = (TextView)findViewById(R.id.tv_station);
        tvProcessedBy = (TextView)findViewById(R.id.tv_processed_by);
        llLoadSuccess.setVisibility(View.VISIBLE);
        llLoadFailed.setVisibility(View.GONE);
        Intent i = getIntent();
        String id = i.getStringExtra(getResources().getString(R.string.intent_str_id));
        loadIncidentDetailFull(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void loadIncidentDetailFull(String id){
        String tag_req_incident_detail_full= getResources().getString(R.string.tag_request_incident_detail_full);
        String url = getResources().getString(R.string.url_api).concat(getResources().getString(R.string.endpoint_incident_detail_full)).concat("/").concat(id).concat("/");
        final ProgressDialog pDialog = new ProgressDialog(IncidentDetail.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug_http_response), response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(IncidentDetail.this,getResources().getString(R.string.notif_error_connection),Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_incident_detail_full);
    }

    void parseData(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
                if(severity.equals(getResources().getString(R.string.severity_success))){
                    JSONObject data = jsonObject.getJSONObject(getResources().getString(R.string.json_tag_data));
                    JSONArray incident = data.getJSONArray(getResources().getString(R.string.json_tag_incident));
                    JSONArray sender = data.getJSONArray(getResources().getString(R.string.json_tag_sender));
                    JSONArray officer = data.getJSONArray(getResources().getString(R.string.json_tag_officer));
                    JSONArray stage = data.getJSONArray(getResources().getString(R.string.json_tag_stage));
                    JSONArray station = data.getJSONArray(getResources().getString(R.string.json_tag_station));
                    if(incident.length() > 0){
                        Incident laporanInsiden = new Incident(
                                Integer.parseInt(incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                Integer.parseInt(incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_sender))),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_image)),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_description)),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_latitude)),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_longitue)),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_received_at)),
                                Integer.parseInt(incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_last_stage))),
                                incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_last_stage_datetime)),
                                Integer.parseInt(incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_processed_by))),
                                Integer.parseInt(incident.getJSONObject(0).getString(getResources().getString(R.string.json_tag_station)))
                        );
                        User senderUser = new User(
                                Integer.parseInt(sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_username)),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_password)),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_full_name)),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_address)),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_phone)),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_email)),
                                Boolean.parseBoolean(sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_is_officer))),
                                Integer.parseInt(sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_station))),
                                sender.getJSONObject(0).getString(getResources().getString(R.string.json_tag_last_login))
                        ) ;
                        User officerUser = new User(
                                Integer.parseInt(officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_username)),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_password)),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_full_name)),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_address)),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_phone)),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_email)),
                                Boolean.parseBoolean(officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_is_officer))),
                                Integer.parseInt(officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_station))),
                                officer.getJSONObject(0).getString(getResources().getString(R.string.json_tag_last_login))
                        );
                        PoliceStation policeStation = new PoliceStation(
                                Integer.parseInt(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_nama_kantor)),
                                Double.valueOf(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_latitude))),
                                Double.valueOf(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_longitue))),
                                station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_address)) 
                        );
                        Stage laporanStage = new Stage(
                                Integer.parseInt(stage.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                stage.getJSONObject(0).getString(getResources().getString(R.string.json_tag_stage))
                        );

                        tvDescription.setText(laporanInsiden.getDescription());
                        tvReceivedAt.setText(laporanInsiden.getReceivedAt());
                        tvLatitude.setText(laporanInsiden.getLatitude());
                        tvLongitude.setText(laporanInsiden.getLongitude());
                        tvSender.setText(senderUser.getFull_name());
                        tvSenderAddress.setText(senderUser.getAddress());
                        tvPhone.setText(senderUser.getPhone());
                        tvStage.setText(laporanStage.getStage());
                        tvStageDatetime.setText(laporanInsiden.getLastStageDatetime());
                        tvStation.setText(policeStation.getStationName());
                        tvProcessedBy.setText(officerUser.getFull_name());
                    }
                }
            }
        }catch(JSONException e){
            Log.d("JSON error :",  e.getMessage());
        }
    }


}
