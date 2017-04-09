package com.example.rathin.testing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.*;
public class OtpLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                //Intent login = new Intent(OtpLogin.this, fragment_tabbed_login.class);
                //startActivity(login);
                Toast.makeText(getApplicationContext(), "Authentication successful for "
                       + phoneNumber, Toast.LENGTH_LONG).show();


            }

            @Override
            public void failure(DigitsException exception) {


                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });




    }
}
