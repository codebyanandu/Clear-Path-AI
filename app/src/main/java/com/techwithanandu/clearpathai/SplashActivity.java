package com.nextgen.clearpathai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class   SplashActivity extends AppCompatActivity {

    GlobalPreference globalPreference;
    boolean loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        globalPreference = new GlobalPreference(this);
        loginStatus = globalPreference.getLoginStatus();

        Log.d("TAG", "onCreate: "+loginStatus);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                if (loginStatus){
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                }else{
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);


            }
        },3000);
    }
}