package com.laurensius.simlakalantas.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentProfil extends Fragment {

    private TextView tvNotifikasi;
    private EditText etNamaPengguna, etKataSandi, etNamaLengkap, etAlamat, etTelepon, etEmail;
    private Button btnUpdate;

    User u;

    public FragmentProfil() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterProfil = inflater.inflate(R.layout.fragment_profil, container, false);
        tvNotifikasi= (TextView) inflaterProfil.findViewById(R.id.tv_notifikasi);
        etNamaPengguna = (EditText)inflaterProfil.findViewById(R.id.et_namapengguna);
        etKataSandi = (EditText)inflaterProfil.findViewById(R.id.et_katasandi);
        etNamaLengkap = (EditText)inflaterProfil.findViewById(R.id.et_namalengkap);
        etAlamat = (EditText)inflaterProfil.findViewById(R.id.et_alamat);
        etTelepon = (EditText)inflaterProfil.findViewById(R.id.et_telepon);
        etEmail = (EditText)inflaterProfil.findViewById(R.id.et_email);
        btnUpdate = (Button)inflaterProfil.findViewById(R.id.btn_update);
        return inflaterProfil;
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
                etNamaPengguna.setText(u.getUsername());
                etKataSandi.setText(u.getPassword());
                etNamaLengkap.setText(u.getFull_name());
                etAlamat.setText(u.getAddress());
                etTelepon.setText(u.getPhone());
                etEmail.setText(u.getEmail());
            }catch (JSONException e){}
        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda akan mengubah detail profil Anda?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                validateUpdateProfile(
                                        etNamaPengguna.getText().toString().toString(),
                                        etKataSandi.getText().toString(),
                                        etNamaLengkap.getText().toString(),
                                        etAlamat.getText().toString(),
                                        etTelepon.getText().toString(),
                                        etEmail.getText().toString());
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void validateUpdateProfile(String username, String password, String full_name, String address, String phone, String email){
        if(username.equals(getResources().getString(R.string.param_no_text)) ||
                password.equals(getResources().getString(R.string.param_no_text)) ||
                full_name.equals(getResources().getString(R.string.param_no_text)) ||
                address.equals(getResources().getString(R.string.param_no_text)) ||
                phone.equals(getResources().getString(R.string.param_no_text)) ||
                email.equals(getResources().getString(R.string.param_no_text))){
            Toast.makeText(
                    getActivity(),
                    getResources().getString(R.string.notif_login_form_not_blank),
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_login_form_not_blank));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
        }else{
            updateProcess(username,password,full_name,address,phone,email);
        }
    }

    void updateProcess(String username, String password, String full_name, String address, String phone, String email){
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        String TAG_REQ_UDPATE_PROFIL = getResources().getString(R.string.tag_request_update_profil);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_profil_update))
                .concat(String.valueOf(u.getId()))
                .concat(getResources().getString(R.string.endpoint_slash));
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_username), username);
        params.put(getResources().getString(R.string.param_password), password);
        params.put(getResources().getString(R.string.param_full_name), full_name);
        params.put(getResources().getString(R.string.param_address), address);
        params.put(getResources().getString(R.string.param_phone), phone);
        params.put(getResources().getString(R.string.param_email), email);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DEBUG",response.toString());
                        pDialog.dismiss();
                        validateUpdateResponse(response);
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG_REQ_UDPATE_PROFIL);
    }

    void validateUpdateResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
            String message = jsonObject.getString(getResources().getString(R.string.json_tag_message));
            JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
            if(severity.equals(getResources().getString(R.string.severity_success))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severitySuccess));
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
