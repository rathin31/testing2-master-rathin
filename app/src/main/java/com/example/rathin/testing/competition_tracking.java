package com.example.rathin.testing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class competition_tracking extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner_model, spinner_item, spinner_brands;
    DatabaseReference mFirebaseRef, mBrands;
    List<String> item_list,brand_list,model_list;
    ArrayAdapter<String> itemAdapter,brandAdapter,modelAdapter;
    String selected_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_tracking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ct_toolbar);
        setSupportActionBar(toolbar);

        spinner_brands = (Spinner) findViewById(R.id.spinner_brands);
        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        spinner_model = (Spinner) findViewById(R.id.spinner_model);

        item_list = new ArrayList<String>();
        brand_list = new ArrayList<String>();
        model_list = new ArrayList<String>();

        itemAdapter = new ArrayAdapter<String>(competition_tracking.this,android.R.layout.simple_spinner_dropdown_item,item_list);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item.setAdapter(itemAdapter);

        brandAdapter= new ArrayAdapter<String>(competition_tracking.this,android.R.layout.simple_spinner_dropdown_item,brand_list);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_brands.setAdapter(brandAdapter);

        modelAdapter = new ArrayAdapter<String>(competition_tracking.this,android.R.layout.simple_spinner_dropdown_item,model_list);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_model.setAdapter(modelAdapter);

        spinner_brands.setOnItemSelectedListener(this);
        spinner_model.setOnItemSelectedListener(this);
        spinner_item.setOnItemSelectedListener(this);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference("StockDetails");
        mFirebaseRef.child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    item_list.add(ds.getKey());
                    itemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds item_list to the action bar if it is present.
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
        } else if(id==R.id.action_logout){
            Intent i = new Intent(getApplication(),tabbed_activity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent==spinner_item){
            brand_list.clear();
            selected_item = spinner_item.getSelectedItem().toString();
            if(item_list.contains(selected_item)){
                mFirebaseRef.child(selected_item+"_Brands/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            brand_list.add(ds.getKey());
                            brandAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }else if (parent==spinner_brands){
            model_list.clear();
            String selected_brand = spinner_brands.getSelectedItem().toString();
            if(brand_list.contains(selected_brand)){
                mFirebaseRef.child(selected_brand+"/"+selected_item+"_Models/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            model_list.add(ds.getKey());
                            modelAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }else if (parent==spinner_model){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
