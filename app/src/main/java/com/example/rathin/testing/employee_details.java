package com.example.rathin.testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class employee_details extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    private EditText et_email,et_phone,et_birth,et_join;
    TextView tv_name;
    FirebaseAuth firebaseAuth;
    Map<String,String> map;
    private Firebase mRef;
    private ImageView iv_edit_phone;
    String uEmail;
    ProgressDialog mProgressDialog;


    @Override
    protected void onResume() {
        changeDetails();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uEmail = user.getEmail().replaceAll("[-_$.:,/]","");
        tv_name= (TextView) findViewById(R.id.et_employee_name);
        et_email=(EditText) findViewById(R.id.et_email);
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_birth= (EditText) findViewById(R.id.et_birthdate);
        et_join = (EditText) findViewById(R.id.et_joining_date);
        iv_edit_phone = (ImageView) findViewById(R.id.iv_edit_phone);
        iv_edit_phone.setOnClickListener(this);

        mRef=new Firebase("https://testing-9f5eb.firebaseio.com/Registration/"+uEmail);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_activity, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==iv_edit_phone){
            et_phone.setImeOptions(IME_ACTION_DONE);
            et_phone.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et_phone.setEnabled(true);
            et_phone.setOnEditorActionListener(this);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et_phone.setEnabled(false);
            return true;
        }else {
            return true;
        }
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

    public void changeDetails(){
        Log.v("Status","Showing ProgressDiaglog");
        final ProgressDialog mProgressDialog = ProgressDialog.show(employee_details.this,"","Fetching Details",false,true);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                map= dataSnapshot.getValue(Map.class);
                String email=map.get("email");
                String name=map.get("name");
                String number=map.get("number");
                String birthdate=map.get("birthdate");
                String joining = map.get("joiningdate");

                et_email.setText(email);
                et_phone.setText(number);
                tv_name.setText(name);
                et_birth.setText(birthdate);
                et_join.setText(joining);
                mProgressDialog.dismiss();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
