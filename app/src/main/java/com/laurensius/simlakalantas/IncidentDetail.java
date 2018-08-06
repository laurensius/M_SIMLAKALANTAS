package com.laurensius.simlakalantas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private LinearLayout llContent,llNoContent;
    private ImageView ivNoContent;
    private ImageView ivImage;
    private TextView tvNoContent;
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
    private Button btnPetunjukArah;
    private Button btnProses;

    public static Incident laporanInsiden;
    private User senderUser;
    private User officerUser;
    public static PoliceStation policeStation;
    private Stage laporanStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail);
        llContent = (LinearLayout)findViewById(R.id.ll_content) ;
        llNoContent = (LinearLayout)findViewById(R.id.ll_no_content) ;
        ivNoContent = (ImageView)findViewById(R.id.iv_no_content);
        tvNoContent = (TextView)findViewById(R.id.tv_no_content);
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
        btnPetunjukArah = (Button)findViewById(R.id.btn_petunjuk_arah);
        btnProses = (Button)findViewById(R.id.btn_proses);
        llContent.setVisibility(View.VISIBLE);
        llNoContent.setVisibility(View.GONE);
        Intent i = getIntent();
        String id = i.getStringExtra(getResources().getString(R.string.intent_str_id));
        String type = i.getStringExtra(getResources().getString(R.string.intent_str_type));
        loadIncidentDetailFull(id);

        if(type.equals(getResources().getString(R.string.intent_str_officer))){
            btnPetunjukArah.setVisibility(View.VISIBLE);
            btnPetunjukArah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IncidentDetail.this,DirectionMap.class);
                    startActivity(intent);
                }
            });
            btnProses.setVisibility(View.VISIBLE);
            btnProses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActionDialog();
                }
            });
        }else{
            btnPetunjukArah.setVisibility(View.GONE);
            btnProses.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showActionDialog(){
        AlertDialog.Builder actionDialog = new AlertDialog.Builder(IncidentDetail.this);
        actionDialog.setTitle(getResources().getString(R.string.image_selector_title));
        String[] actionDialogItems = {
                getResources().getString(R.string.action_selector_waiting),
                getResources().getString(R.string.action_selector_process),
                getResources().getString(R.string.action_selector_finish), };
        actionDialog.setItems(actionDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        officerUser.getId();
                        laporanInsiden.getId();
                        switch (which) {
                            case 0:
                                new AlertDialog.Builder(IncidentDetail.this)
                                    .setTitle("Konfirmasi")
                                    .setMessage("Apakah Anda akan mengubah status menjadi \"Menunggu Respon\"?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            updateStage(laporanInsiden.getId(), 1,officerUser.getId());
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                                break;
                            case 1:
                                new AlertDialog.Builder(IncidentDetail.this)
                                    .setTitle("Konfirmasi")
                                    .setMessage("Apakah Anda akan mengubah status menjadi \"Dalam Proses\"?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            updateStage(laporanInsiden.getId(), 2,officerUser.getId());
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                                break;
                            case 2:
                                new AlertDialog.Builder(IncidentDetail.this)
                                    .setTitle("Konfirmasi")
                                    .setMessage("Apakah Anda akan mengubah status menjadi \"Selesai\"?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        //      if($this->input->post('incident') == null &&
                                        //		$this->input->post('chronology') == null &&
                                        //		$this->input->post('accident_victim') == null &&
                                        //		$this->input->post('damage') == null &&
                                        //		$this->input->post('action') == null)
                                            updateStage(laporanInsiden.getId(), 3,officerUser.getId());
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                                break;
                        }
                    }
                });
        actionDialog.show();
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
                        llNoContent.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.GONE);
                        ivNoContent.setImageResource(R.mipmap.img_volley_err);
                        tvNoContent.setText(getResources().getString(R.string.notif_error_connection));
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_incident_detail_full);
    }

    public void updateStage(final int incidentId, final int newStage, int officerId){
        String tag_req_incident_detail_full= getResources().getString(R.string.tag_request_incident_detail_full);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_incident_update_stage))
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(incidentId))
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(newStage))
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(officerId))
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(IncidentDetail.this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d(getResources().getString(R.string.debug_http_response), response.toString());
                        try {
                            if (response != null) {
                                String severity = response.getString(getResources().getString(R.string.json_tag_severity));
                                if (severity.equals(getResources().getString(R.string.severity_success))) {
                                    Toast.makeText(getApplicationContext(),response.getString(getResources().getString(R.string.json_tag_message)),Toast.LENGTH_LONG).show();
                                    loadIncidentDetailFull(String.valueOf(incidentId));
                                    if(newStage==3){
                                        Intent i = new Intent(getApplicationContext(),FinalReport.class);
                                        i.putExtra(getResources().getString(R.string.intent_str_id),incidentId);
                                        startActivity(i);
                                    }
                                }else{
                                    llNoContent.setVisibility(View.VISIBLE);
                                    llContent.setVisibility(View.GONE);
                                    ivNoContent.setImageResource(R.mipmap.img_volley_err);
                                    tvNoContent.setText(response.getString(getResources().getString(R.string.json_tag_message)));
                                }
                            }else{
                                llNoContent.setVisibility(View.VISIBLE);
                                llContent.setVisibility(View.GONE);
                                ivNoContent.setImageResource(R.mipmap.img_json_parse_err);
                                tvNoContent.setText(getResources().getString(R.string.notif_error_json_response));
                            }
                        }catch(JSONException e){
                            llNoContent.setVisibility(View.VISIBLE);
                            llContent.setVisibility(View.GONE);
                            ivNoContent.setImageResource(R.mipmap.img_json_parse_err);
                            tvNoContent.setText(getResources().getString(R.string.notif_error_json_response));
                            Log.d(getResources().getString(R.string.notif_error_json_response), e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        llNoContent.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.GONE);
                        ivNoContent.setImageResource(R.mipmap.img_volley_err);
                        tvNoContent.setText(getResources().getString(R.string.notif_error_connection));
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
                        laporanInsiden = new Incident(
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
                        senderUser = new User(
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
                        officerUser = new User(
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
                        policeStation = new PoliceStation(
                                Integer.parseInt(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                                station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_nama_kantor)),
                                Double.valueOf(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_latitude))),
                                Double.valueOf(station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_longitue))),
                                station.getJSONObject(0).getString(getResources().getString(R.string.json_tag_address)) 
                        );
                        laporanStage = new Stage(
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

                        if(laporanInsiden.getLastStage() > 2){
                            btnProses.setVisibility(View.GONE);
                        }

                        byte[] imageBytes = Base64.decode(laporanInsiden.getImage(), Base64.DEFAULT);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        ivImage.setImageBitmap(decodedImage);
                        ivImage.setAdjustViewBounds(true);
                    }else{
                        llNoContent.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.GONE);
                        ivNoContent.setImageResource(R.mipmap.img_no_data);
                        tvNoContent.setText(getResources().getString(R.string.notif_content_no_data));
                    }
                }else{
                    llNoContent.setVisibility(View.VISIBLE);
                    llContent.setVisibility(View.GONE);
                    ivNoContent.setImageResource(R.mipmap.img_json_parse_err);
                    tvNoContent.setText(getResources().getString(R.string.notif_error_json_response));
                }
            }else{
                llNoContent.setVisibility(View.VISIBLE);
                llContent.setVisibility(View.GONE);
                ivNoContent.setImageResource(R.mipmap.img_json_parse_err);
                tvNoContent.setText(getResources().getString(R.string.notif_error_json_response));
            }
        }catch(JSONException e){
            llNoContent.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            ivNoContent.setImageResource(R.mipmap.img_json_parse_err);
            tvNoContent.setText(getResources().getString(R.string.notif_error_json_response));
            Log.d(getResources().getString(R.string.notif_error_json_response), e.getMessage());
        }
    }

}
