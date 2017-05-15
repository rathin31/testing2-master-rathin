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
import android.widget.TextView;
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
import java.util.Map;

public class SaleStock extends AppCompatActivity implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener{

    Spinner spinner_item, spinner_models;
    protected EditText  et_sp, et_sold;
    TextView tv_stock, tv_mrp,tv_cp, tv_profit, tv_discount;
    private Button button2;
    private DatabaseReference mFirebaseDatabase, itemRetrieve, modelRef;
    private FirebaseAuth firebaseAuth;
    List<String> item_list, model_list;
    ArrayAdapter<String> mobAdapter,itemAdapter;
    public Map<String,String> map;
    String selected_item,selected_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_stock_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        spinner_item.setOnItemSelectedListener(this);
        spinner_models = (Spinner) findViewById(R.id.spinner_models);
        spinner_models.setOnItemSelectedListener(this);

        item_list = new ArrayList<String>();
        model_list = new ArrayList<String>();

        tv_cp = (TextView) findViewById(R.id.tv_cp);
        tv_mrp = (TextView) findViewById(R.id.tv_mrp);
        tv_stock = (TextView) findViewById(R.id.tv_stock);
        tv_profit = (TextView) findViewById(R.id.tv_profit);
        tv_discount = (TextView) findViewById(R.id.tv_discount);

        et_sp = (EditText) findViewById(R.id.et_sp);
        et_sold = (EditText) findViewById(R.id.et_sold);
        button2 = (Button) findViewById(R.id.btSave);
        
        et_sp.addTextChangedListener(this);
        et_sold.addTextChangedListener(this);
        button2.setOnClickListener(this);

        itemAdapter = new ArrayAdapter<String>(SaleStock.this, android.R.layout.simple_spinner_item, item_list);
        itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item.setAdapter(itemAdapter);

        mobAdapter= new ArrayAdapter<String>(SaleStock.this,android.R.layout.simple_spinner_item,model_list);
        mobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_models.setAdapter(mobAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        itemRetrieve = mFirebaseDatabase.child("StockDetails/MyBrand/Items");
        itemRetrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    item_list.add(ds.getKey());
                    itemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
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
        float cp, sp, sold, mrp, temp, stock;
        sold = Float.valueOf(et_sold.getText().toString());
        cp = Float.valueOf(tv_cp.getText().toString());
        sp = Float.valueOf(et_sp.getText().toString());
        mrp = Float.valueOf(tv_mrp.getText().toString());
        stock = Float.valueOf(tv_stock.getText().toString());

        if (TextUtils.isEmpty(et_sp.getText().toString()) || TextUtils.isEmpty(et_sold.getText().toString())) {

        } else if (sold>stock) {

        }else{


            try {
                temp = (sp - cp) * sold;
                tv_profit.setText(Float.toString(temp));
            } catch (Exception e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
            temp = 0;
            try {
                temp = (mrp - sp)*100 / mrp;
                tv_discount.setText(Float.toString(temp));

            } catch (Exception e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendData() {
        ProgressDialog progressDialog = new ProgressDialog(SaleStock.this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        String CostPrice = tv_cp.getText().toString().trim();
        String SellingPrice = et_sp.getText().toString().trim();
        String UnitsSold = et_sold.getText().toString().trim();
        String Profit = tv_profit.getText().toString().trim();
        String MRP = tv_mrp.getText().toString().trim();
        String Disc = tv_discount.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String Email = user.getEmail();
        SalesDetails salesDetails = new SalesDetails(CostPrice, SellingPrice, UnitsSold, Profit, MRP, Disc);
        String timeStamp = new SimpleDateFormat("/dd_MM_yy  /hh:mm:ss a").format(new Date());
        String result = Email.replaceAll("[-_$.:,/]", "");
        String Finalstr = result + timeStamp;
        mFirebaseDatabase.child("SalesDetails/"+Finalstr).setValue(salesDetails);
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
        Log.d("Stats","inside OnItemSelected");

        if(parent == spinner_item) {
            selected_item = spinner_item.getSelectedItem().toString();
                model_list.clear();
                mFirebaseDatabase.child("StockDetails/MyBrand/"+selected_item+"_Models").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            model_list.add(ds.getKey());
                            mobAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        }else if(parent==spinner_models){
            selected_model = spinner_models.getSelectedItem().toString();
            if (model_list.contains(selected_model)) {
                mFirebaseDatabase.child("StockDetails/Brand_Details/MyBrand/" + selected_item + "/" + selected_model).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map = (Map<String, String>) dataSnapshot.getValue();
                        tv_cp.setText(String.valueOf(map.get("CostPrice")));
                        tv_mrp.setText(String.valueOf(map.get("MRP")));
                        tv_stock.setText(String.valueOf(map.get("Units")));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
