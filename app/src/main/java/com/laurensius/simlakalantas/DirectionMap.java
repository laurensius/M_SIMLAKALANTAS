package com.laurensius.simlakalantas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.Timer;


public class DirectionMap extends AppCompatActivity {

    private MapView mvDirection = null;
    private ScaleBarOverlay mScaleBarOverlay;
    private IMapController mapController;

    private LocationManager locationManager;
    private LocationListener listener;

    public static double lat;
    public static double lon;
    Marker incidentMarker, stationMarker, userMarker;

    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_map);
        mvDirection = (MapView)findViewById(R.id.mv_direction);
        mvDirection.setTileSource(TileSourceFactory.MAPNIK);
        mvDirection.setBuiltInZoomControls(true);
        mvDirection.setMultiTouchControls(true);
        //Kontrol Skala
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mScaleBarOverlay = new ScaleBarOverlay(mvDirection);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(width / 2, 10);
        mvDirection.getOverlays().add(mScaleBarOverlay);
        mvDirection.invalidate();

        stationMarker = new Marker(mvDirection);
        incidentMarker = new Marker(mvDirection);
        userMarker= new Marker(mvDirection);


        lat = 0.0;
        lon = 0.0;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.d(getResources().getString(R.string.debug_lat) , String.valueOf(lat));
                Log.d(getResources().getString(R.string.debug_lon) , String.valueOf(lon));
                removeUserMarker();
                setUserMarker(lat,lon);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configureGPS();
        configureStorage();

        setCenter(IncidentDetail.policeStation.getLatitude() , IncidentDetail.policeStation.getLongitude());
        setUserMarker(lat,lon);
        setStationMarker( IncidentDetail.policeStation.getLatitude() , IncidentDetail.policeStation.getLongitude());
        setIncidentMarker( Double.parseDouble(IncidentDetail.laporanInsiden.getLatitude()), Double.parseDouble( IncidentDetail.laporanInsiden.getLongitude()));


    }

    void setCenter(double lat,double lon){
        mapController = mvDirection.getController();
        mapController.setZoom(14);
        GeoPoint startPoint = new GeoPoint(lat, lon);
        mapController.setCenter(startPoint);
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
        mvDirection.getOverlays().add(userMarker);
        mvDirection.invalidate();
    }

    void setStationMarker(double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_kantor_polisi, null);
        stationMarker.setIcon(iconMarker);
        stationMarker.setPosition(new GeoPoint(lat, lon));
        stationMarker.setTitle(IncidentDetail.policeStation.getStationName());
        stationMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                stationMarker.showInfoWindow();
                return false;
            }
        });
        mvDirection.getOverlays().add(stationMarker);
        mvDirection.invalidate();
    }

    void setIncidentMarker(double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_insiden, null);
        incidentMarker.setIcon(iconMarker);
        incidentMarker.setPosition(new GeoPoint(lat, lon));
        incidentMarker.setTitle(IncidentDetail.laporanInsiden.getDescription());
        incidentMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                incidentMarker.showInfoWindow();
                return false;
            }
        });
        mvDirection.getOverlays().add(incidentMarker);
        mvDirection.invalidate();
    }

    void removeUserMarker(){
        mvDirection.getOverlays().remove(userMarker);
        mvDirection.invalidate();
    }

    void configureGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates(getResources().getString(R.string.provider_gps), 5000, 0, listener);
    }

    void configureStorage(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(getResources().getString(R.string.debug_permission), getResources().getString(R.string.debug_permisssion_storage_granted));
            } else {
                Log.v(getResources().getString(R.string.debug_permission), getResources().getString(R.string.debug_permisssion_storage_revoke));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
