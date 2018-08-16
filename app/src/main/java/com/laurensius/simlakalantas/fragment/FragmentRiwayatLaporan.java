package com.laurensius.simlakalantas.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.laurensius.simlakalantas.AppPelapor;
import com.laurensius.simlakalantas.IncidentDetail;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.adapter.AdapterIncident;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.listener.IncidentListener;
import com.laurensius.simlakalantas.model.Incident;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentRiwayatLaporan extends Fragment {

    private RecyclerView rvRiwayatLaporan;
    private AdapterIncident adapterIncident = null;
    RecyclerView.LayoutManager mLayoutManager;
    List<Incident> listIncident = new ArrayList<>();

    private LinearLayout llContent, llNoContent;
    private ImageView ivNoContent;
    private TextView tvNoContent;

    public FragmentRiwayatLaporan() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterRiwayatLaporan = inflater.inflate(R.layout.fragment_riwayat_laporan, container, false);
        llNoContent = (LinearLayout)inflaterRiwayatLaporan.findViewById(R.id.ll_no_content);
        ivNoContent = (ImageView)inflaterRiwayatLaporan.findViewById(R.id.iv_no_content);
        tvNoContent = (TextView)inflaterRiwayatLaporan.findViewById(R.id.tv_no_content);
        llContent = (LinearLayout)inflaterRiwayatLaporan.findViewById(R.id.ll_content);
        rvRiwayatLaporan = (RecyclerView)inflaterRiwayatLaporan.findViewById( R.id.rv_riwayat_laporan);
        return inflaterRiwayatLaporan;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llNoContent.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
        rvRiwayatLaporan.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvRiwayatLaporan.setLayoutManager(mLayoutManager);
        adapterIncident = new AdapterIncident(listIncident);
        adapterIncident.notifyDataSetChanged();
        rvRiwayatLaporan.setAdapter(adapterIncident);
        rvRiwayatLaporan.addOnItemTouchListener(new IncidentListener(getActivity(), new IncidentListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int childAdapterPosition) {
                Incident incident = adapterIncident.getItem(childAdapterPosition);
                Toast.makeText(getActivity(),incident.getDescription(),Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), IncidentDetail.class);
                i.putExtra(getResources().getString(R.string.intent_str_id),String.valueOf(incident.getId()) );
                i.putExtra(getResources().getString(R.string.intent_str_type),getResources().getString(R.string.intent_str_pelapor));
                startActivity(i);
            }
        }));
        loadRiwayatLaporan();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadRiwayatLaporan(){
        String tag_req_incident_select_by_sender = getResources().getString(R.string.tag_request_incident_by_sender);
        String url = getResources().getString(R.string.url_api).concat(getResources().getString(R.string.endpoint_incident_select_by_sender)).concat(String.valueOf(AppPelapor.userPelapor.getId())).concat("/");
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
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
                            listIncident.add(new Incident(
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_id))),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_sender))),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_image)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_description)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_latitude)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_longitue)),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_received_at)),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_last_stage))),
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_last_stage_datetime)),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_processed_by))),
                                    Integer.parseInt(data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_station)))));
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
        adapterIncident.notifyDataSetChanged();
    }
}
