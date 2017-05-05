package com.example.rathin.testing;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rathin.testing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LatLong extends Activity implements LocationListener,View.OnClickListener{

    LocationManager locationManager ;
    String provider;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;
    private Button Start,Stop;
    public double lati;
    public double longi;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lat_long);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Location");
        Start=(Button)findViewById(R.id.Start);
        Stop=(Button)findViewById(R.id.Stop);
        Start.setOnClickListener(this);
        Stop.setOnClickListener(this);

        // Getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if(provider!=null && !provider.equals("")){

            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }


    public void onLocationChanged(Location location) {

        TextView tvLongitude = (TextView) findViewById(R.id.tv_longitude);
        TextView tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude.setText("Longitude:" + location.getLongitude());
        tvLatitude.setText("Latitude:" + location.getLatitude());
        longi = location.getLongitude();
        lati = location.getLatitude();
        LocationDet locationdet = new LocationDet(lati, longi);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String Email = user.getEmail();
        String node = Email.replaceAll("[-_$.:,]", "");
        String timeStamp = new SimpleDateFormat("/dd_MM_yy  /hh:mm:ss a").format(new Date());
        String Finalstr = node + timeStamp;
        mFirebaseDatabase.child(Finalstr).setValue(locationdet);

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onClick(View v) {
        if(v==Start)
        {

        }
    }
}

