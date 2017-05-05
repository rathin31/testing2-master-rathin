package com.example.rathin.testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleStock extends AppCompatActivity implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener{

    Spinner spinner_item, spinner_models;
    protected EditText et_cp, et_sp, et_sold, et_profit, et_mrp, et_discount;
    private Button button2;
    private DatabaseReference mFirebaseDatabase, itemRetrieve, modelRef;
    private FirebaseAuth firebaseAuth;
    List<String> items, item_detail;
    ArrayAdapter<String> mobAdapter, compAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_stock_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Stats","inside OnItemSelected");
                if (view == spinner_item) {
                    String selected_item = spinner_item.getSelectedItem().toString();

                    switch (selected_item) {
                        case "Mobiles":
                            modelRef = mFirebaseDatabase.child("Mobile_Models");
                            modelRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                        item_detail.add(ds.getKey());
                                        mobAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;
                        case "Computers":
                            Log.v("check","inside computers");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_models = (Spinner) findViewById(R.id.spinner_models);
        items = new ArrayList<String>();
        item_detail = new ArrayList<String>();

        et_cp = (EditText) findViewById(R.id.et_cp);
        et_sp = (EditText) findViewById(R.id.et_sp);
        et_sold = (EditText) findViewById(R.id.et_sold);
        et_profit = (EditText) findViewById(R.id.et_profit);
        et_mrp = (EditText) findViewById(R.id.et_mrp);
        button2 = (Button) findViewById(R.id.button2);
        et_discount = (EditText) findViewById(R.id.et_discount);

        et_cp.addTextChangedListener(this);
        et_sp.addTextChangedListener(this);
        et_sold.addTextChangedListener(this);
        button2.setOnClickListener(this);

        final ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(SaleStock.this, android.R.layout.simple_spinner_item, items);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item.setAdapter(itemAdapter);


        mobAdapter= new ArrayAdapter<String>(SaleStock.this,android.R.layout.simple_spinner_item,item_detail);
        mobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_models.setAdapter(mobAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("SalesStock");
        itemRetrieve = mFirebaseDatabase.child("Items");
        itemRetrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    items.add(ds.getKey());
                    itemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        spinner_models.setOnItemSelectedListener(this);


        /*Log.v("Status", "setting selection listener");
        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = spinner_item.getSelectedItem().toString();

                Log.v("Status","Entering Switch");

                switch (selected_item){
                    case "Mobiles":
                        item_detail= new ArrayList<String>();
                        item_detail.add("Default");
                        mobAdapter= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,item_detail);
                        mobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_models.setAdapter(mobAdapter);
                        modelRef = mFirebaseDatabase.child("Mobile_Models/Samsung");
                        modelRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    mobAdapter.add(ds.getKey());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mobAdapter.notifyDataSetChanged();
                    break;

                    case "Computers":
                        compAdapter = new ArrayAdapter<String>(NewSales.this, android.R.layout.simple_spinner_dropdown_item, item_detail);
                        compAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_models.setAdapter(compAdapter);
                        modelRef = mFirebaseDatabase.child("Computer_Models/Samsung");
                        modelRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    compAdapter.add(ds.getKey());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        compAdapter.notifyDataSetChanged();
                        break;
                    }
                }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.v("Status","Inside Listener");

            }
        });*/
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        float cp, sp, sold, mrp, temp;

        if (TextUtils.isEmpty(et_sp.getText().toString()) || TextUtils.isEmpty(et_sold.getText().toString())) {

        } else {

            cp = Float.valueOf(et_cp.getText().toString());
            sp = Float.valueOf(et_sp.getText().toString());
            sold = Float.valueOf(et_sold.getText().toString());
            mrp = Float.valueOf(et_mrp.getText().toString());
            try {
                temp = (sp - cp) * sold;
                et_profit.setText(Float.toString(temp));
            } catch (Exception e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
            temp = 0;
            try {
                temp = (mrp - sp) / mrp;
                et_discount.setText(Float.toString(temp));

            } catch (Exception e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendData() {
        ProgressDialog progressDialog = new ProgressDialog(SaleStock.this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        String CostPrice = et_cp.getText().toString().trim();
        String SellingPrice = et_sp.getText().toString().trim();
        String UnitsSold = et_sold.getText().toString().trim();
        String Profit = et_profit.getText().toString().trim();
        String MRP = et_mrp.getText().toString().trim();
        String Disc = et_discount.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String Email = user.getEmail();
        SalesDetails salesDetails = new SalesDetails(CostPrice, SellingPrice, UnitsSold, Profit, MRP, Disc);
        String timeStamp = new SimpleDateFormat("/dd_MM_yy  /hh:mm:ss a").format(new Date());
        String result = Email.replaceAll("[-_$.:,/]", "");
        String Finalstr = result + timeStamp;
        mFirebaseDatabase.child(Finalstr).setValue(salesDetails);
        progressDialog.dismiss();
        Toast.makeText(this, "Your data is Stored", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v == button2) {
            SendData();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v("Stats","inside OnItemSelected");
        if (view == spinner_item) {
            String selected_item = spinner_item.getSelectedItem().toString();

            switch (selected_item) {
                case "Mobiles":
                    modelRef = mFirebaseDatabase.child("Mobile_Models");
                    modelRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                item_detail.add(ds.getKey());
                            }
                            mobAdapter= new ArrayAdapter<String>(SaleStock.this,android.R.layout.simple_spinner_dropdown_item,item_detail);
                            mobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_models.setAdapter(mobAdapter);
                            mobAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case "Computers":
                    Log.v("check","inside computers");
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Log.v("Status","Nothing selected yet");
    }
}