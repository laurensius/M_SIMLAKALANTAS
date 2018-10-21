package com.laurensius.simlakalantas.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.AppOfficer;
import com.laurensius.simlakalantas.AppPelapor;
import com.laurensius.simlakalantas.IncidentDetail;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.adapter.AdapterNotif;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.listener.NotifListener;
import com.laurensius.simlakalantas.model.Notif;
import com.laurensius.simlakalantas.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentPemberitahuan extends Fragment {

    private RecyclerView rvNotif;
    private AdapterNotif adapterNotif = null;
    RecyclerView.LayoutManager mLayoutManager;
    List<Notif> listNotif = new ArrayList<>();

    private LinearLayout llContent, llNoContent;
    private ImageView ivNoContent;
    private TextView tvNoContent;

    User u;
    
    public FragmentPemberitahuan() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterPemberitahuan = inflater.inflate(R.layout.fragment_pemberitahuan, container, false);
        llNoContent = (LinearLayout)inflaterPemberitahuan.findViewById(R.id.ll_no_content);
        ivNoContent = (ImageView)inflaterPemberitahuan.findViewById(R.id.iv_no_content);
        tvNoContent = (TextView)inflaterPemberitahuan.findViewById(R.id.tv_no_content);
        llContent = (LinearLayout)inflaterPemberitahuan.findViewById(R.id.ll_content);
        rvNotif = (RecyclerView)inflaterPemberitahuan.findViewById( R.id.rv_pemberitahuan);
        return inflaterPemberitahuan ;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editorPreferences;
        sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        String sharedpref_data_user = sharedPreferences.getString(getResources().getString(R.string.sharedpref_data_user),null);
        if(sharedpref_data_user != null){
            try{
                JSONArray jsonArray = new JSONArray(sharedpref_data_user);
                u = new User(
                        Integer.parseInt(jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_id))),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_username)),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_password)),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_full_name)),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_address)),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_phone)),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_email)),
                        Boolean.parseBoolean(jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_is_officer))),
                        Integer.parseInt(jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_station))),
                        jsonArray.getJSONObject(0).getString(getResources().getString(R.string.json_tag_last_login)));
            }catch (JSONException e){

            }
        }
        llNoContent.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
        rvNotif.setAdapter(null);
        rvNotif.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvNotif.setLayoutManager(mLayoutManager);
        adapterNotif = new AdapterNotif(listNotif);
        adapterNotif.notifyDataSetChanged();
        rvNotif.setAdapter(adapterNotif);
        rvNotif.addOnItemTouchListener(new NotifListener(getActivity(), new NotifListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int childAdapterPosition) {
                Notif notif = adapterNotif.getItem(childAdapterPosition);
                int id_notif = notif.getId();
                int id_incident = notif.getIncident();
                updateNotifOpen(String.valueOf(id_notif),String.valueOf(id_incident));
            }
        }));

        if(u.getIs_officer()){
            loadPemberitahuan(getResources().getString(R.string.json_tag_station), String.valueOf(AppOfficer.userOfficer.getStation()));
        }else{
            loadPemberitahuan(getResources().getString(R.string.json_tag_aim),String.valueOf(AppPelapor.userPelapor.getId()));
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateNotifOpen(String id_notif, final String id_incident){
        String tag_req_notif_update_open= getResources().getString(R.string.tag_request_incident_by_sender);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_notif_select_recent))
                .concat(id_notif)
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();

                        try{
                            if(response != null){
                                String severity = response.getString(getResources().getString(R.string.json_tag_severity));
                                if(severity.equals(getResources().getString(R.string.severity_success))){
                                    Intent i = new Intent(getActivity(), IncidentDetail.class);
                                    i.putExtra(getResources().getString(R.string.intent_str_id),id_incident);
                                    if(u.getIs_officer()){
                                        i.putExtra(getResources().getString(R.string.intent_str_type),getResources().getString(R.string.intent_str_officer));
                                    }else{
                                        i.putExtra(getResources().getString(R.string.intent_str_type),getResources().getString(R.string.intent_str_pelapor));
                                    }
                                    startActivity(i);
                                }else{
                                    Toast.makeText(getActivity(),"Terjadi gangguan mengambil detail notifikasi",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),"Terjadi gangguan mengambil detail notifikasi",Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            Toast.makeText(getActivity(),"Terjadi gangguan mengambil detail notifikasi",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(),"Terjadi gangguan mengambil detail notifikasi",Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_notif_update_open);
    }

    public void loadPemberitahuan(String agent, String id){
        String tag_req_incident_select_by_sender = getResources().getString(R.string.tag_request_incident_by_sender);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_notif_select_recent))
                .concat(agent)
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(id)
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(20))
                .concat(getResources().getString(R.string.endpoint_slash));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d("Response : ", response.toString());
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_incident_select_by_sender);
    }

    void parseData(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
                if(severity.equals(getResources().getString(R.string.severity_success))){
                    JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
                    if(data.length() > 0){
                        for (int x=0;x<data.length();x++){
                            listNotif.add(new Notif(
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_id))),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_aim))),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_incident))),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_station))),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_content)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_datetime)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_isopen))
                                    ));
                        }
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
        adapterNotif.notifyDataSetChanged();
    }

}
