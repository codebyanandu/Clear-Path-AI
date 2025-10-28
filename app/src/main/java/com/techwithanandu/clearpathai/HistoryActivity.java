package com.techwithanandu.clearpathai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.techwithanandu.clearpathai.Adapter.HistoryAdapter;
import com.techwithanandu.clearpathai.ModelClass.HistoryModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private static String TAG ="HistoryActivity";

    RecyclerView historyRV;
    ArrayList<HistoryModelClass> list;

    GlobalPreference globalPreference;
    String ip,uid;
    ImageView backIV;
    TextView titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        historyRV = findViewById(R.id.historyRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        historyRV.setLayoutManager(layoutManager);

        backIV = findViewById(R.id.BackImageButton);
        titleTV = findViewById(R.id.appBarTitleTextView);

        getReportHistory();

        titleTV.setText("Report History");
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void getReportHistory() {

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ URLs.REPORT_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("failed")){
                    Toast.makeText(HistoryActivity.this, "No History Available", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String image = object.getString("image");
                            String description = object.getString("description");
                            String date = object.getString("date");
                            String latitude = object.getString("latitude");
                            String longitude = object.getString("longitude");
                            String location = object.getString("location");


                            list.add(new HistoryModelClass(id,image,description,date,latitude,longitude,location));

                        }

                        HistoryAdapter adapter = new HistoryAdapter(list,HistoryActivity.this);
                        historyRV.setAdapter(adapter);

                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            @Nullable
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}