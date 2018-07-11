package com.laurensius.simlakalantas.fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.AppPelapor;
import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.appcontroller.AppController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import java.util.Timer;
import java.util.TimerTask;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class FragmentPetaSebaranPolsek extends Fragment {

    private MapView mvSebaranPolsek = null;
    private ScaleBarOverlay mScaleBarOverlay;
    private IMapController mapController;

    Marker userMarker;

    double recent_lat = 0.0;
    double recent_lon = 0.0;

    Timer timer = new Timer();

    public FragmentPetaSebaranPolsek() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterSebaranPolsek = inflater.inflate(R.layout.fragment_peta_sebaran_polsek, container, false);
        mvSebaranPolsek = (MapView)inflaterSebaranPolsek.findViewById(R.id.mv_sebaranpolsek);
        return inflaterSebaranPolsek;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        mvSebaranPolsek.setTileSource(TileSourceFactory.MAPNIK);
        mvSebaranPolsek.setBuiltInZoomControls(true);
        mvSebaranPolsek.setMultiTouchControls(true);
        //Kontrol Skala
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mScaleBarOverlay = new ScaleBarOverlay(mvSebaranPolsek);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(width / 2, 10);
        mvSebaranPolsek.getOverlays().add(mScaleBarOverlay);
        mvSebaranPolsek.invalidate();

        recent_lat = AppPelapor.lat;
        recent_lon = AppPelapor.lat;

        userMarker = new Marker(mvSebaranPolsek);

        setCenter(-6.96662256,108.45397988);

        class MyTimerTask extends TimerTask {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        removeUserMarker();
                        Log.d(getResources().getString(R.string.debug_lat), String.valueOf(AppPelapor.lat));
                        Log.d(getResources().getString(R.string.debug_lon), String.valueOf(AppPelapor.lon));
                        setUserMarker(AppPelapor.lat,AppPelapor.lon);
                        recent_lat = AppPelapor.lat;
                        recent_lon = AppPelapor.lon;
                    }
                });
            }
        }
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1000,5000);

        loadPoliceStation();

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

    void setCenter(double lat,double lon){
        mapController = mvSebaranPolsek.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(lat, lon);
        mapController.setCenter(startPoint);
    }

    void setPoliceStation(final String title, final double lat, final double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_kantor_polisi, null);
        final Marker markerPoliceStation = new Marker(mvSebaranPolsek);
        markerPoliceStation.setPosition(new GeoPoint(lat, lon));
        markerPoliceStation.setIcon(iconMarker);
        markerPoliceStation.setTitle(title);
        markerPoliceStation.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Location titikAwal = new Location(getResources().getString(R.string.provider_titikawal));
                titikAwal.setLatitude(recent_lat);
                titikAwal.setLongitude(recent_lon);
                Location titikAkhir = new Location(getResources().getString(R.string.provider_titikakhir));
                titikAkhir.setLatitude(lat);
                titikAkhir.setLongitude(lon);
                String jarak = String.valueOf(hitungJarak(titikAwal,titikAkhir));
                Log.d(getResources().getString(R.string.debug_jarak) , jarak);
                marker.showInfoWindow();
                return false;
            }
        });
        mvSebaranPolsek.getOverlays().add(markerPoliceStation);
        mvSebaranPolsek.invalidate();
    }

    void setUserMarker(double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_my_location, null);
        userMarker.setIcon(iconMarker);
        userMarker.setPosition(new GeoPoint(lat, lon));
        userMarker.setTitle(getResources().getString(R.string.marker_current_loc));
        userMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                userMarker.showInfoWindow();
                return false;
            }
        });
        mvSebaranPolsek.getOverlays().add(userMarker);
        mvSebaranPolsek.invalidate();
    }

    void removeUserMarker(){
        mvSebaranPolsek.getOverlays().remove(userMarker);
        mvSebaranPolsek.invalidate();
    }

    float hitungJarak(Location titikAwal, Location titikAkhir){
        return titikAwal.distanceTo(titikAkhir);
    }

    public void loadPoliceStation(){
        String tag_req_sation_list = getResources().getString(R.string.tag_request_station_list);
        String url = getResources().getString(R.string.url_api).concat(getResources().getString(R.string.endpoint_station_list));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
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
                        Toast.makeText(getActivity(),getResources().getString(R.string.notif_error_connection),Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_sation_list);
    }

    void parseData(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
                if(severity.equals(getResources().getString(R.string.severity_success))){
                    JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
                    if(data.length() > 0){
                        for(int x = 0;x < data.length(); x++){
                            JSONObject objPoliceStation = data.getJSONObject(x);
                            setPoliceStation(objPoliceStation.getString(getResources().getString(R.string.json_tag_nama_kantor)),
                            Double.parseDouble(objPoliceStation.getString(getResources().getString(R.string.json_tag_latitude))),
                            Double.parseDouble(objPoliceStation.getString(getResources().getString(R.string.json_tag_longitue))));
                            Log.d("Iterasi ke : ",  String.valueOf(x));
                        }
                    }
                }
            }
        }catch(JSONException e){
            Log.d("JSON error :",  e.getMessage());
        }
    }

}
