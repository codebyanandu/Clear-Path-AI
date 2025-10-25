package com.nextgen.clearpathai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

public class AnimalAlertActivity extends AppCompatActivity {

    ImageView startIV;
    ImageView stopIV;
    MediaPlayer player;
    LinearLayout backLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_alert);

        startIV = findViewById(R.id.startImageView);
        stopIV = findViewById(R.id.stopImageView);
        backLL = findViewById(R.id.alertBackLL);

        startIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startIV.setVisibility(View.GONE);
                stopIV.setVisibility(View.VISIBLE);


                startSound("b.mp3");

            }
        });

        stopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopSound();
            }
        });

        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void stopSound(){
        player.stop();

        stopIV.setVisibility(View.GONE);
        startIV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AnimalAlertActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}