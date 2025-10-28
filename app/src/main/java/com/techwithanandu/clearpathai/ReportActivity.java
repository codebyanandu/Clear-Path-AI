package com.techwithanandu.clearpathai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "ReportActivity";

    EditText descriptionET;
    ImageView reportIV;
    Button reportBT;
    ImageView backIV;
    TextView titleTV;

    GlobalPreference globalPreference;
    String ip,uid;
    String latitude,longitude;

    /* image  */
    private Uri photoUri;
    String encodeImage;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;

    /* location */
    Geocoder geocoder;
    String city, address, state;
    List<Address> addresses;
    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();
        latitude = globalPreference.getLatitude();
        longitude = globalPreference.getLongitude();

        descriptionET = findViewById(R.id.descriptionEditText);
        reportIV = findViewById(R.id.reportImageView);
        reportBT = findViewById(R.id.reportButton);
        backIV = findViewById(R.id.BackImageButton);
        titleTV = findViewById(R.id.appBarTitleTextView);

        titleTV.setText("Report Accident");
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               finish();
            }
        });

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }

        reportBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportAccident();
            }
        });

        reportIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ReportActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    openCamera();
                }
            }
        });

    }


    private void openCamera() {

         /* Capturing image and uploading
          code can be used to capture image and upload it in android 12 and above devices with specific
          permissions in manifest file as manage storage and adding a service provider to fetch file path
         */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.aumento.anandu", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException{

        Log.d(TAG, "createImageFile: "+"success");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Log.e("OCRActivity", "Camera permission denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image capture was successful, process the captured image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                // Use the encoded image as needed
                reportIV.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reportAccident() {

        lat = Double.valueOf(latitude);
        lng = Double.valueOf(longitude);

        geocoder = new Geocoder(ReportActivity.this, Locale.getDefault());


        if (lat != 0 && lng != 0){
            try {

                addresses = geocoder.getFromLocation(lat, lng, 1);

                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();


            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ URLs.USER_DETAILS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG, "onResponse: "+response);

                    if (response.equals("success")){

                        Intent intent = new Intent(ReportActivity.this,HomeActivity.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(ReportActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error);
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", uid);
                    params.put("image", encodeImage);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("description", descriptionET.getText().toString());
                    params.put("location", address + ",\t" + city + ",\t" + state);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else {
            Log.d(TAG, "onResponse "+"no location");
            Toast.makeText(this, "Unable to fetch Location", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {
       Intent intent = new Intent(ReportActivity.this,HomeActivity.class);
       startActivity(intent);
       finish();
    }
}