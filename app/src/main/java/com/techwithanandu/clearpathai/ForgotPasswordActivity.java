package com.techwithanandu.clearpathai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailET;
    TextView resetPasswordTV;
    TextView backLoginTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailET = findViewById(R.id.forgotEmailEditText);
        resetPasswordTV = findViewById(R.id.resetPasswordTextView);
        backLoginTV = findViewById(R.id.backLoginTextView);

        resetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Apply your Forgot password logic

            }
        });

        backLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}