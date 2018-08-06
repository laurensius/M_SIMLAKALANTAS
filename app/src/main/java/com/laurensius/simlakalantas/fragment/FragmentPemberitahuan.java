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
import com.laurensius.simlakalantas.adapter.AdapterNotif;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.listener.NotifListener;
import com.laurensius.simlakalantas.model.Notif;

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
        llNoContent.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
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
                Intent i = new Intent(getActivity(), IncidentDetail.class);
                i.putExtra(getResources().getString(R.string.intent_str_id),String.valueOf(notif.getIncident()) );
                i.putExtra(getResources().getString(R.string.intent_str_type),getResources().getString(R.string.intent_str_pelapor));
                startActivity(i);
            }
        }));
        loadPemberitahuan();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadPemberitahuan(){
        String tag_req_incident_select_by_sender = getResources().getString(R.string.tag_request_incident_by_sender);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_notif_select_recent))
                .concat(getResources().getString(R.string.json_tag_aim))
                .concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(AppPelapor.userPelapor.getId()))
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
                                    data.getJSONObject(x).getString(getResources().getString(R.string.json_tag_datetime))
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
