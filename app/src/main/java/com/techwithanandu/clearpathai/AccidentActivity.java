package com.techwithanandu.clearpathai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccidentActivity extends AppCompatActivity {

    private static final String TAG = "AccidentActivity";

    TextView timerTV;
    TextView safeTV;
    MediaPlayer player;

    GlobalPreference globalPreference;
    String latitude,longitude;
    String ip,uid;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();
        latitude = globalPreference.getLatitude();
        longitude = globalPreference.getLongitude();


        timerTV = findViewById(R.id.timerTextView);
        safeTV = findViewById(R.id.safeTV);

        startSound("b.mp3");

        location = "Accident Location\nhttps://www.google.com/maps?q="+latitude+ ','+longitude+"\n\nClick the above link to view in map";

        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTV.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTV.setText("Alert");

                player.stop();
                sendAlert();
            }
        };


        countDownTimer.start();

        safeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countDownTimer.cancel();

                player.stop();

                timerTV.setText("Alert");

                finish();
            }
        });

    }


    private void startSound(String filename) {
        AssetFileDescriptor afd = null;
        try {
            afd = getResources().getAssets().openFd(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new MediaPlayer();
        try {
            assert afd != null;
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        onPause();
    }

    /** Accident Alert */
    private void sendAlert() {

        final SmsManager smsManager = SmsManager.getDefault();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ URLs.EMG_CONTACTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String phone = jsonObject.getString("phone");
                        Log.d(TAG, "onResponse: "+phone);
                        try {
                            smsManager.sendTextMessage(phone, null, location , null, null);
                            Toast.makeText(getApplicationContext(), "Accident Detected", Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat",String.valueOf(latitude));
                params.put("lon",String.valueOf(longitude));
                params.put("userid", uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Can't go Back", Toast.LENGTH_SHORT).show();
    }
}