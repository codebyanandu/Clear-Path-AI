package com.nextgen.clearpathai.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nextgen.clearpathai.AccidentActivity;
import com.nextgen.clearpathai.ForgotPasswordActivity;
import com.nextgen.clearpathai.GlobalPreference;
import com.nextgen.clearpathai.LoginActivity;
import com.nextgen.clearpathai.R;
import com.nextgen.clearpathai.URLs;
import com.nextgen.clearpathai.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    View view;
    TextView userNameTV;
    TextView locationTV;
    CircleImageView userIV;
    ImageView sosIV;
    LinearLayout callLL;
    LinearLayout alertLL;
    private String ip,uid;
    GlobalPreference globalPreference;
    String refNumber;
    String location;
    String latitude,longitude;
    Geocoder geocoder;
    String city, address, state;
    List<Address> addresses;
    Double lat,lng;

    SensorManager sm;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private List<Sensor> list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalPreference = new GlobalPreference(getContext());
        ip = globalPreference.getIP();
        uid = globalPreference.getID();
        latitude = globalPreference.getLatitude();
        longitude = globalPreference.getLongitude();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        if (getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    10);
        }

        userNameTV = view.findViewById(R.id.userNameTextView);
        userIV = view.findViewById(R.id.userIconImageView);
        sosIV = view.findViewById(R.id.sosImageView);
        locationTV = view.findViewById(R.id.locationTextView);
        callLL = view.findViewById(R.id.callLL);
        alertLL = view.findViewById(R.id.alertLL);

        Log.d(TAG, "onLocation"+" location "+latitude +" "+longitude);

        location = "Accident Location\nhttps://www.google.com/maps?q="+latitude+ ','+longitude+"\n\nClick the above link to view in map";

        if (!uid.equals("")){
            getUserDetails();
        }

       /** ---------- getting current address ---------- */
        if (!latitude.equals("") || !longitude.equals("")){

             fetchCurrentAddress();
        }

        sosIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  Intent intent = new Intent(getContext(), AccidentActivity.class);
                startActivity(intent);*/

                Intent callIntent= new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+refNumber));
                startActivity(callIntent);


                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(refNumber,null,"Immediately need a help\n"+location,null, null);

                Toast.makeText(getContext(),"Alert Sent",Toast.LENGTH_LONG).show();

            }
        });

        callLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent= new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+refNumber));
                startActivity(callIntent);

            }
        });

        alertLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(refNumber,null,"Immediately need a help\n"+location,null, null);

                Toast.makeText(getContext(),"Message Sent",Toast.LENGTH_LONG).show();

            }
        });

        userIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout();
            }
        });

        monitorSensorValues();


        return view;
    }

    private void fetchCurrentAddress() {

        lat = Double.valueOf(latitude);
        lng = Double.valueOf(longitude);

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(lat, lng, 1);

            Log.d(TAG, "currentAddress: "+addresses);

           // address = addresses.get(0).getAddressLine(0);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();

            Log.d(TAG, "currentAddress: "+address +" "+city+" "+state);

            locationTV.setText(address);



        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void getUserDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ URLs.USER_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject jsonObject = array.getJSONObject(0);

                    String name = jsonObject.getString("name");
                    String image = jsonObject.getString("image");
                    refNumber = jsonObject.getString("reference_number");

                    userNameTV.setText(name);
                    globalPreference.saveNumber(refNumber);

                    Log.d(TAG, "imageResponse: "+image);

                    if (!image.equals("no image")) {
                        Glide.with(getContext())
                                .load("http://" + URLs.MAIN_URL + "user_tbl/uploads/" + image)
                                .into(userIV);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                Map<String,String> params = new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void logout() {

        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        globalPreference.saveLoginStatus(false);

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * Detecting accident with sensor
     */

    private void monitorSensorValues() {

        /* Get a SensorManager instance */
        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(list.size()>0){
            sm.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(getContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
    }


    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            //textView1.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);

            Sensor mySensor = event.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                final float x = event.values[0];
                final float y = event.values[1];
                float z = event.values[2];
                Log.d(TAG, "onSensorChanged x: " + x);
                Log.d(TAG, "onSensorChanged y: " + y);
                Log.d(TAG, "onSensorChanged z: " + z);

                float gvalue = (float) (Math.sqrt((x * x) + (y * y) + (z * z)) / 9.8);


                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 1000) {
                    lastUpdate = curTime;

                    System.out.println("G value" + gvalue);

                    if (gvalue > 1.5 ) {

                        Intent intent = new Intent(getContext(), AccidentActivity.class);
                        startActivity(intent);


                    }

                }

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        latitude = globalPreference.getLatitude();
        longitude = globalPreference.getLongitude();

        Log.d(TAG, "onLocation "+" onResume: " + latitude +" "+longitude);


        if (!latitude.equals("") || !longitude.equals("")){

            fetchCurrentAddress();
        }
    }
}