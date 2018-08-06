package com.laurensius.simlakalantas.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.AppPelapor;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.model.FW;
import com.laurensius.simlakalantas.model.PoliceStation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class FragmentFormPelaporan extends Fragment {

    private EditText etKeterangan,etLatitude, etLongitude;
    private Button btnKirim;
    private ImageView ivFoto;

    private FW polisiTerdekat;

    private Boolean is_taked = false;
    private String image;

    private static String IMAGE_DIRECTORY;
    private int GALLERY = 1, CAMERA = 2;

    private PoliceStation[] policeStation;
    private float jarak[];

    double recent_lat = AppPelapor.lat;
    double recent_lon = AppPelapor.lat;
    Timer timer = new Timer();

    public FragmentFormPelaporan() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterFormPelaporan = inflater.inflate(R.layout.fragment_form_pelaporan, container, false);
        ivFoto = (ImageView)inflaterFormPelaporan.findViewById(R.id.iv_foto);
        etKeterangan = (EditText)inflaterFormPelaporan.findViewById(R.id.et_keterangan);
        etLatitude = (EditText)inflaterFormPelaporan.findViewById(R.id.et_latitude);
        etLongitude  = (EditText)inflaterFormPelaporan.findViewById(R.id.et_longitude);
        btnKirim = (Button)inflaterFormPelaporan.findViewById(R.id.btn_kirim);
        return inflaterFormPelaporan;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        IMAGE_DIRECTORY = getResources().getString(R.string.image_dir);
        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda akan mengirim laporan?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                validateKirimLaporan();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        class MyTimerTask extends TimerTask {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        recent_lat = AppPelapor.lat;
                        recent_lon = AppPelapor.lon;
                        etLatitude.setText(String.valueOf(recent_lat));
                        etLongitude.setText(String.valueOf(recent_lon));
                    }
                });
            }
        }
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1000,5000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle(getResources().getString(R.string.image_selector_title));
        String[] pictureDialogItems = {
                getResources().getString(R.string.image_selector_gallery),
                getResources().getString(R.string.image_selector_camera) };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.toast_save_image_success), Toast.LENGTH_SHORT).show();
                    ivFoto.setImageBitmap(bitmap);
                    is_taked = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.toast_save_image_failed), Toast.LENGTH_SHORT).show();
                    is_taked = false;
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get(getResources().getString(R.string.image_camera_data));
            ivFoto.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.toast_save_image_success), Toast.LENGTH_SHORT).show();
            is_taked = true;
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + getResources().getString(R.string.image_ext_jpg));
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity().getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{getResources().getString(R.string.image_mime_jpg)}, null);
            fo.close();
            Log.d(getResources().getString(R.string.debug_save_image), f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    void validateKirimLaporan(){
        if(is_taked == true && !etKeterangan.getText().toString().equals("")){
            loadPoliceStation();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapDrawable drawable = (BitmapDrawable) ivFoto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.toast_form_report_not_complete),Toast.LENGTH_LONG).show();
        }
    }

    void kirimLaporan(String sender,String image,String description,String latitude,String longitude,String station){
        String tag_req_incident_insert = getResources().getString(R.string.tag_request_incident_insert);
        String url = getResources().getString(R.string.url_api).concat(getResources().getString(R.string.endpoint_incident_insert));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_mengirim_laporan)
                .concat(polisiTerdekat.getPs().getStationName())
                .concat(getResources().getString(R.string.progress_mengirim_jarak))
                .concat( String.valueOf(polisiTerdekat.getDistance()))
                .concat(getResources().getString(R.string.progress_mengirim_dari_lokasi_anda)));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_sender), sender);
        params.put(getResources().getString(R.string.param_image), image);
        params.put(getResources().getString(R.string.param_description), description);
        params.put(getResources().getString(R.string.param_latitude), latitude);
        params.put(getResources().getString(R.string.param_longitude), longitude);
        params.put(getResources().getString(R.string.param_station), station);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url , parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(getResources().getString(R.string.debug_http_response),response.toString());
                        pDialog.dismiss();
                        try{
                            if(response.getString(getResources().getString(R.string.json_tag_severity)).equals(getResources().getString(R.string.severity_success))){
                                etKeterangan.setText("");
                                ivFoto.setAdjustViewBounds(false);
                                ivFoto.setImageResource(R.drawable.icon_add_photo);
                            }
                            Toast.makeText(getActivity(),response.getString(getResources().getString(R.string.json_tag_message)),Toast.LENGTH_LONG).show();
                        }catch (JSONException e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.debug_http_response),error.getMessage());
                        Log.d(getResources().getString(R.string.debug_http_response),error.toString());
                        pDialog.dismiss();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_req_incident_insert);
    }

    public void loadPoliceStation() {
        String tag_req_station_list = getResources().getString(R.string.tag_request_station_list);
        String url = getResources().getString(R.string.url_api).concat(getResources().getString(R.string.endpoint_station_list));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.progress_cari_kantor_polisi_terdekat));
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
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
                        Toast.makeText(getActivity(), getResources().getString(R.string.notif_error_connection), Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_station_list);
    }

    void parseData(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
                if(severity.equals(getResources().getString(R.string.severity_success))){
                    JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
                    if(data.length() > 0){
                        policeStation = new PoliceStation[data.length()];
                        jarak = new float[data.length()];
                        for(int x = 0;x < data.length(); x++){
                            JSONObject objPoliceStation = data.getJSONObject(x);
                            policeStation[x] = new PoliceStation(
                                    Integer.parseInt(objPoliceStation.getString(getResources().getString(R.string.json_tag_id))),
                                    objPoliceStation.getString(getResources().getString(R.string.json_tag_nama_kantor)),
                                    Double.valueOf(objPoliceStation.getString(getResources().getString(R.string.json_tag_latitude))),
                                    Double.valueOf(objPoliceStation.getString(getResources().getString(R.string.json_tag_longitue))),
                                    objPoliceStation.getString(getResources().getString(R.string.json_tag_address)));
                            Location titikAwal = new Location(getResources().getString(R.string.provider_titikawal));
                            titikAwal.setLatitude(recent_lat);
                            titikAwal.setLongitude(recent_lon);
                            Location titikAkhir = new Location(getResources().getString(R.string.provider_titikakhir));
                            titikAkhir.setLatitude(Double.valueOf(objPoliceStation.getString(getResources().getString(R.string.json_tag_latitude))));
                            titikAkhir.setLongitude(Double.valueOf(objPoliceStation.getString(getResources().getString(R.string.json_tag_longitue))));
                            jarak[x] = hitungJarak(titikAwal,titikAkhir);
                            String str_jarak = String.valueOf(jarak[x]);
                            Log.d(getResources().getString(R.string.debug_jarak) , str_jarak);
                        }
                        polisiTerdekat = floydWarshall(policeStation,jarak);
                        Log.d("Terdekat adalah ", polisiTerdekat.getPs().getStationName());
                        Log.d("Dengan Jarak ", String.valueOf(polisiTerdekat.getDistance()));
                        kirimLaporan(
                                String.valueOf(AppPelapor.userPelapor.getId()),
                                image,
                                etKeterangan.getText().toString(),
                                etLatitude.getText().toString(),
                                etLongitude.getText().toString(),
                                String.valueOf(polisiTerdekat.getPs().getId()));
                    }
                }
            }
        }catch(JSONException e){
            Log.d(getResources().getString(R.string.notif_error_json_response), e.getMessage());
        }
    }

    float hitungJarak(Location titikAwal, Location titikAkhir){
        return titikAwal.distanceTo(titikAkhir);
    }

    FW floydWarshall(PoliceStation[] ps,float[] j){
        PoliceStation dummyPoliceStation;
        float dummyJarak;
        for(int x=0;x<ps.length - 1;x++){
            for(int y= x + 1;y<ps.length;y++){
                if(j[x] > j[y]){
                    dummyPoliceStation = policeStation[x]; dummyJarak = jarak[x];
                    policeStation[x] = policeStation[y]; jarak[x] = jarak[y];
                    policeStation[y] = dummyPoliceStation; jarak[y] = dummyJarak;
                }
            }
        }
        FW fW = new FW(policeStation[0],jarak[0]);
        return fW;
    }
}
