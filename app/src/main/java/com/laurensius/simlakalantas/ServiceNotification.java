package com.laurensius.simlakalantas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.appcontroller.AppController;
import com.laurensius.simlakalantas.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceNotification extends Service {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    Timer timer = new Timer();

    User user;

    Boolean init = true;

    String recent_str = "";

    public ServiceNotification(){}

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("UNSUPPORTED OPERATION EXCEPTION");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        String sharedpref_data_user = sharedPreferences.getString(getResources().getString(R.string.sharedpref_data_user),null);

        if(sharedpref_data_user != null){
            try{
                JSONArray jsonArray = new JSONArray(sharedpref_data_user);
                user = new User(
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



        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if(user.getIs_officer()){
                                loadNotification("station",user.getStation());
                            }else{
                                loadNotification("aim",user.getId());
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void loadNotification(String agent, int id){
        String tag_req_notification = getResources().getString(R.string.tag_request_incident_by_station_stage);
        String url = getResources().getString(R.string.url_api)
                .concat(getResources().getString(R.string.endpoint_notif_select_recent))
                .concat(agent).concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(id)).concat(getResources().getString(R.string.endpoint_slash))
                .concat(String.valueOf(1)).concat(getResources().getString(R.string.endpoint_slash));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response : ", response.toString());
                        notificationChecker(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getResources().getString(R.string.notif_error_json_response), "Eroor");
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_req_notification);
    }

    void notificationChecker(JSONObject jsonObject){
        String id;
        String aim;
        String station;
        String content;
        String datetime;
        try {
            id = jsonObject.getJSONArray("data").getJSONObject(0).getString("id");
            aim = jsonObject.getJSONArray("data").getJSONObject(0).getString("aim");
            station = jsonObject.getJSONArray("data").getJSONObject(0).getString("station");
            content = jsonObject.getJSONArray("data").getJSONObject(0).getString("content");
            datetime = jsonObject.getJSONArray("data").getJSONObject(0).getString("datetime");
            if(!recent_str.equals(id) && !init){
                if(user.getIs_officer()){
                    Log.d("notifikasi ","Buat notifikasi");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                    Intent ii = new Intent(getApplicationContext(), AppOfficer.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    bigText.bigText("Notification");
                    bigText.setBigContentTitle("Notification");
                    bigText.setSummaryText(content);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                    mBuilder.setContentTitle("Notification");
                    mBuilder.setContentText(content);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("notify_001",
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    mNotificationManager.notify(0, mBuilder.build());
                }else{
                    Log.d("notifikasi ","Buat notifikasi");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                    Intent ii = new Intent(getApplicationContext(), AppPelapor.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    bigText.bigText("Notification");
                    bigText.setBigContentTitle("Notification");
                    bigText.setSummaryText(content);
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                    mBuilder.setContentTitle("Notification");
                    mBuilder.setContentText(content);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("notify_001",
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
            init = false;
            recent_str = id;
        }catch (JSONException e){
            Log.d("Notification", e.getMessage().toString());
        }


    }

}