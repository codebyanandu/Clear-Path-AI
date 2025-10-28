package com.techwithanandu.clearpathai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techwithanandu.clearpathai.Adapter.RouteAdapter;
import com.techwithanandu.clearpathai.ModelClass.RouteModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private static String TAG ="RouteActivity";

    RecyclerView routeRV;
    ArrayList<RouteModelClass> list;
    LinearLayout backLL;
    EditText locationET;
    LinearLayout noAccidentsLL;
    LinearLayout searchLL;

    GlobalPreference globalPreference;
    String ip,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        routeRV = findViewById(R.id.routeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        routeRV.setLayoutManager(layoutManager);

        getRoutes();

        backLL = findViewById(R.id.routeBackLL);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        noAccidentsLL = findViewById(R.id.noAccidentsLL);
        searchLL = findViewById(R.id.searchLL);
        locationET = findViewById(R.id.locationEditText);
        locationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchLocation(query);
                } else {

                    resetLocation();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getRoutes() {

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ URLs.ROUTE_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("failed")){

                    routeRV.setVisibility(View.GONE);
                    searchLL.setVisibility(View.GONE);
                    noAccidentsLL.setVisibility(View.VISIBLE);

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


                            list.add(new RouteModelClass(id,image,description,date,latitude,longitude,location));

                        }

                        RouteAdapter adapter = new RouteAdapter(list,RouteActivity.this);
                        routeRV.setAdapter(adapter);

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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void searchLocation(String query) {
        ArrayList<RouteModelClass> filteredList = new ArrayList<>();

        for (RouteModelClass locations : list) {
            if (locations.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(locations);
            }
        }

        if (filteredList.isEmpty()) {
            noAccidentsLL.setVisibility(View.VISIBLE);
            routeRV.setVisibility(View.GONE);
        } else {
            noAccidentsLL.setVisibility(View.GONE);
            routeRV.setVisibility(View.VISIBLE);
            RouteAdapter adapter = new RouteAdapter(filteredList, RouteActivity.this);
            routeRV.setAdapter(adapter);
        }
    }

    private void resetLocation() {
        if (list.isEmpty()) {
            noAccidentsLL.setVisibility(View.VISIBLE);
            routeRV.setVisibility(View.GONE);
        } else {
            noAccidentsLL.setVisibility(View.GONE);
            routeRV.setVisibility(View.VISIBLE);
            RouteAdapter adapter = new RouteAdapter(list, RouteActivity.this);
            routeRV.setAdapter(adapter);
        }
    }
}