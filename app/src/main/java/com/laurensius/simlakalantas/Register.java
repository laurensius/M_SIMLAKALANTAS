package com.laurensius.simlakalantas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.laurensius.simlakalantas.appcontroller.AppController;

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


public class Register extends AppCompatActivity {

    private TextView tvMasuk, tvNotifikasi;
    private EditText etNamaPengguna, etKataSandi, etNamaLengkap, etAlamat, etTelepon, etEmail;
    private Button btnDaftar;
    private ImageView ivKitas;

    private String url_api;
    private String endpoint_user_register;

    private String TAG;
    private String TAG_REQ_LOGIN;

    private Boolean is_taked = false;
    private String image;

    private static String IMAGE_DIRECTORY;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        IMAGE_DIRECTORY = getResources().getString(R.string.image_dir);
        url_api = getResources().getString(R.string.url_api);
        endpoint_user_register = getResources().getString(R.string.endpoint_user_register);
        TAG = AppController.class.getSimpleName();
        TAG_REQ_LOGIN = getResources().getString(R.string.tag_request_login);
        etNamaPengguna = (EditText)findViewById(R.id.et_namapengguna);
        etKataSandi = (EditText)findViewById(R.id.et_katasandi);
        etNamaLengkap = (EditText)findViewById(R.id.et_namalengkap);
        etAlamat = (EditText)findViewById(R.id.et_alamat);
        etTelepon = (EditText)findViewById(R.id.et_telepon);
        etEmail = (EditText)findViewById(R.id.et_email);
        ivKitas = (ImageView)findViewById(R.id.iv_kitas);
        ivKitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        btnDaftar = (Button)findViewById(R.id.btn_daftar);
        tvMasuk = (TextView)findViewById(R.id.tv_masuk);
        tvMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        tvNotifikasi = (TextView) findViewById(R.id.tv_notifikasi);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etNamaPengguna.getText().toString();
                String password = etKataSandi.getText().toString();
                String full_name = etNamaLengkap.getText().toString();
                String address = etAlamat.getText().toString();
                String phone = etTelepon.getText().toString();
                String email = etEmail.getText().toString();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BitmapDrawable drawable = (BitmapDrawable) ivKitas.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                validateRegister(username,password,full_name,address,phone,email,image);
            }
        });
    }

    void validateRegister(String username, String password, String full_name, String address, String phone, String email,String image){
        if(is_taked == false || username.equals(getResources().getString(R.string.param_no_text)) ||
        password.equals(getResources().getString(R.string.param_no_text)) ||
        full_name.equals(getResources().getString(R.string.param_no_text)) ||
        address.equals(getResources().getString(R.string.param_no_text)) ||
        phone.equals(getResources().getString(R.string.param_no_text)) ||
        email.equals(getResources().getString(R.string.param_no_text))){
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.notif_login_form_not_blank),
                    Toast.LENGTH_SHORT).show();
            tvNotifikasi.setVisibility(View.VISIBLE);
            tvNotifikasi.setText(getResources().getString(R.string.notif_login_form_not_blank));
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severityWarning));
        }else{
            registerProcess(username,password,full_name,address,phone,email,image);
        }
    }

    void registerProcess(String username, String password, String full_name, String address, String phone, String email,String image){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(getResources().getString(R.string.param_username), username);
        params.put(getResources().getString(R.string.param_password), password);
        params.put(getResources().getString(R.string.param_full_name), full_name);
        params.put(getResources().getString(R.string.param_address), address);
        params.put(getResources().getString(R.string.param_phone), phone);
        params.put(getResources().getString(R.string.param_email), email);
        params.put(getResources().getString(R.string.param_kitas), image);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_api.concat(endpoint_user_register), parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        pDialog.dismiss();
                        validateRegisterResponse(response);
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG_REQ_LOGIN);
    }

    void validateRegisterResponse(JSONObject jsonObject){
        try{
            String severity = jsonObject.getString(getResources().getString(R.string.json_tag_severity));
            String message = jsonObject.getString(getResources().getString(R.string.json_tag_message));
            JSONArray data = jsonObject.getJSONArray(getResources().getString(R.string.json_tag_data));
            if(severity.equals(getResources().getString(R.string.severity_success))){
                tvNotifikasi.setVisibility(View.VISIBLE);
                tvNotifikasi.setText(message);
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.severitySuccess));
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
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

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Register.this);
        pictureDialog.setTitle("Pilih sumber foto");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
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
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    ivKitas.setAdjustViewBounds(true);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(Register.this, "Simpan gambar berhasil", Toast.LENGTH_SHORT).show();
                    ivKitas.setImageBitmap(bitmap);
                    is_taked = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Register.this, "Simpan gambar gagal", Toast.LENGTH_SHORT).show();
                    is_taked = false;
                }
            }
        } else if (requestCode == CAMERA) {
            ivKitas.setAdjustViewBounds(true);
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ivKitas.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(Register.this, "Simpan gambar berhasil", Toast.LENGTH_SHORT).show();
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
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("Debug Image", f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}
