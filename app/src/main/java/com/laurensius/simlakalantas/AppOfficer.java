package com.laurensius.simlakalantas;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.laurensius.simlakalantas.fragment.FragmentLaporanOfficer;
import com.laurensius.simlakalantas.fragment.FragmentOfficer;
import com.laurensius.simlakalantas.fragment.FragmentPemberitahuan;
import com.laurensius.simlakalantas.fragment.FragmentProfil;
import com.laurensius.simlakalantas.model.User;

import org.json.JSONArray;
import org.json.JSONException;

public class AppOfficer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private Fragment fragment;
    private Dialog dialBox;

    private LocationManager locationManager;
    private LocationListener listener;

    public static double lat;
    public static double lon;

    public static User userOfficer;

    private String waiting;
    private String onprocess;
    private String finish;

    public static String stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_officer);

        loadSharedPreferences();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        View header = navigationView.getHeaderView(0);
        TextView tvHeaderUsername = (TextView)header.findViewById(R.id.tv_header_username);
        TextView tvHeaderEmail = (TextView)header.findViewById(R.id.tv_header_email);
        tvHeaderUsername.setText(userOfficer.getFull_name());
        tvHeaderEmail.setText(userOfficer.getEmail());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadSharedPreferences();
                View header = navigationView.getHeaderView(0);
                TextView tvHeaderUsername = (TextView)header.findViewById(R.id.tv_header_username);
                TextView tvHeaderEmail = (TextView)header.findViewById(R.id.tv_header_email);
                tvHeaderUsername.setText(userOfficer.getFull_name());
                tvHeaderEmail.setText(userOfficer.getEmail());
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fl_officer, new FragmentOfficer());
        tx.commit();

        dialBox = createDialogBox();

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

        waiting = getResources().getString(R.string.stage_waiting);
        onprocess = getResources().getString(R.string.stage_process);
        finish = getResources().getString(R.string.stage_finish);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            dialBox.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configureGPS();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_officer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        jalankanFragment(item.getItemId());
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        jalankanFragment(item.getItemId());
        return true;
    }
    public void jalankanFragment(int id) {
        fragment = null;
        if (id == R.id.nav_beranda) {
            fragment = new FragmentOfficer();
        } else if (id == R.id.nav_laporan_masuk) {
            stage = waiting;
            fragment = new FragmentLaporanOfficer();
        } else if (id == R.id.nav_laporan_proses) {
            stage = onprocess;
            fragment = new FragmentLaporanOfficer();
        } else if (id == R.id.nav_riwayat_laporan) {
            stage = finish;
            fragment = new FragmentLaporanOfficer();
        } else if (id == R.id.nav_pemberitahuan) {
            fragment = new FragmentPemberitahuan();
        } else if (id == R.id.nav_profil) {
            fragment = new FragmentProfil();
        } else if (id == R.id.nav_keluar) {
            new AlertDialog.Builder(AppOfficer.this)
                    .setTitle(getResources().getString(R.string.confirm_logout_title))
                    .setMessage(getResources().getString(R.string.confirm_logout_body))
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            stopService(new Intent(getBaseContext(), ServiceNotification.class));
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
                            SharedPreferences.Editor editorPreferences = sharedPreferences.edit();
                            editorPreferences.clear();
                            editorPreferences.commit();
                            Intent i = new Intent(AppOfficer.this, Login.class);
                            startActivity(i);
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_officer, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    void loadSharedPreferences(){
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        String sharedpref_data_user = sharedPreferences.getString(getResources().getString(R.string.sharedpref_data_user),null);
        if(sharedpref_data_user != null){
            try{
                JSONArray jsonArray = new JSONArray(sharedpref_data_user);
                userOfficer = new User(
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
            }catch (JSONException e){ }
        }
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.confirm_exit_title))
                .setMessage(getResources().getString(R.string.confirm_exit_body))
                .setPositiveButton(getResources().getString(R.string.confirm_ya), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.confirm_tidak), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }
}

