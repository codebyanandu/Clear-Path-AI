package com.techwithanandu.clearpathai.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.techwithanandu.clearpathai.EditProfileActivity;
import com.techwithanandu.clearpathai.GlobalPreference;
import com.techwithanandu.clearpathai.R;
import com.techwithanandu.clearpathai.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
   ProfileFragment
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = "ProfileFragment";

    View view;
    TextView nameTV;
    TextView emailTV;
    TextView numberTV;
    TextView refNumberTV;
    CircleImageView profileIV;
    LinearLayout editProfileLL;
    String userdata;
    private String ip,uid;
    GlobalPreference globalPreference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalPreference = new GlobalPreference(getContext());
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTV = view.findViewById(R.id.pNameTextView);
        emailTV = view.findViewById(R.id.pEmailTextView);
        numberTV = view.findViewById(R.id.pNumberTextView);
        refNumberTV = view.findViewById(R.id.pRefNumberTextView);
        profileIV = view.findViewById(R.id.profileIconImageView);
        editProfileLL = view.findViewById(R.id.editProfileLL);

        getUserDetails();

        editProfileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("userdata",userdata);
                startActivity(intent);
            }
        });


        return view;
    }

    private void getUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ URLs.USER_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                try{

                    userdata = response;

                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject jsonObject = array.getJSONObject(0);

                    String name = jsonObject.getString("name");
                    String number = jsonObject.getString("number");
                    String email = jsonObject.getString("email");
                    String refNumber = jsonObject.getString("reference_number");
                    String image = jsonObject.getString("image");

                    nameTV.setText(name);
                    numberTV.setText(number);
                    emailTV.setText(email);
                    refNumberTV.setText(refNumber);

                    if (!image.equals("no image")) {
                        Glide.with(getContext())
                                .load("http://" + URLs.IMAGES_URL + image)
                                .into(profileIV);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), ""+error, Toast.LENGTH_SHORT).show();
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
}