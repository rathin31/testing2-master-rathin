package com.example.rathin.testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class landing_page extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CardView cvAttendance, cvSales, cvLocation, cvCompTrack;
    DatabaseReference mFirebaseDatabase;
    ArrayList<String> past_attendance;
    boolean alreadyExecuted =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cvAttendance = (CardView) findViewById(R.id.cvAttendance);
        cvSales = (CardView) findViewById(R.id.cvSales);
        cvLocation = (CardView) findViewById(R.id.cvLocation);
        cvCompTrack = (CardView) findViewById(R.id.cvCompTrack);
        cvSales.setOnClickListener(this);
        cvAttendance.setOnClickListener(this);
        cvLocation.setOnClickListener(this);
        cvCompTrack.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(!alreadyExecuted){
            calendar_color();
            Log.v("Changing value",String.valueOf(alreadyExecuted));

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            Intent i = new Intent(getApplication(), tabbed_activity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Employee_Details) {
            Intent i = new Intent(getApplication(), employee_details.class);
            startActivity(i);
        } else if (id == R.id.nav_credits) {
            Intent i = new Intent(getApplication(), credits.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View v) {
        if (v == cvAttendance) {
            Intent i = new Intent(landing_page.this, tabbed_attendance.class);
            startActivity(i);

        }

        if (v == cvSales) {
            Intent asd = new Intent(landing_page.this, SaleStock.class);
            startActivity(asd);
        }
        if (v == cvLocation) {
            Intent loc = new Intent(landing_page.this, MapsActivity.class);
            startActivity(loc);
        }
        if (v == cvCompTrack) {

            Intent f = new Intent(landing_page.this, competition_tracking.class);
            startActivity(f);
        }

    }

    public void calendar_color() {
        Log.v("Inside","calendar_color");
        final ProgressDialog mProgressDialog =  new ProgressDialog(landing_page.this);
        mProgressDialog.setMessage("Just a sec please");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Attendance");
        String uEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("[-_$.,]", "");
        past_attendance = new ArrayList<String>();

        mFirebaseDatabase.child(uEmail + "/05-17").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
             @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {
                if ((boolean) ds.getValue()) {
                    past_attendance.add(ds.getKey());
                    Log.v("Starting", ds.getKey());
                }
            }
                 if(!alreadyExecuted) {
                     Log.v("Stopping", "out of onDataChange");
                     Set<String> attendanceSet = new HashSet<String>();
                     attendanceSet.addAll(past_attendance);
                     for (String temp: attendanceSet){
                         Log.v("Set Values",temp);
                     }

                     SharedPreferences sp = getSharedPreferences("mypref", MODE_PRIVATE);
                     SharedPreferences.Editor mEditor = sp.edit();
                     mEditor.putStringSet("attendanceSet", attendanceSet);
                     Log.v("fuck you", "comitting changes");
                     mEditor.commit();
                     alreadyExecuted =true;
                 }
                 mProgressDialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    }

}