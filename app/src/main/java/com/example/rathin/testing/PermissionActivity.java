package com.example.rathin.testing;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.view.View;
import android.widget.Button;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;

public class PermissionActivity  extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 500;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // Button check_permission = (Button) findViewById(R.id.check_permission);
        //Button request_permission = (Button) findViewById(R.id.request_permission);

        if (!checkPermission()) {

            requestPermission();

        }
        else {

            Intent loc = new Intent(PermissionActivity.this, tabbed_activity.class);
            startActivity(loc);
        }
       // check_permission.setOnClickListener(this);
       // request_permission.setOnClickListener(this);


    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);


        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2==PackageManager.PERMISSION_GRANTED && result3==PackageManager.PERMISSION_GRANTED && result4==PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, RECEIVE_SMS, READ_PHONE_STATE,READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean storage=grantResults[4]==PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && smsAccepted && phoneAccepted && storage) {
                        //Snackbar.make(view, "Permission Granted, Now you can access application.", Snackbar.LENGTH_LONG).show();
                        Intent loc = new Intent(PermissionActivity.this, tabbed_activity.class);
                        startActivity(loc);
                    }
                    else {
                        Intent loc = new Intent(PermissionActivity.this, tabbed_activity.class);
                        startActivity(loc);
                        Snackbar.make(view, "Permission Denied, You cannot access", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA,  RECEIVE_SMS, READ_PHONE_STATE,READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }

                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PermissionActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}