package com.example.rathin.testing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.Map;

import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class employee_details extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    private EditText et1,et2,et3,et4,et5;
    private Firebase mRef;
    private ArrayList<String> musername;
    private ImageView iv_edit_mail, iv_edit_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        et1=(EditText) findViewById(R.id.et1);
        et3=(EditText) findViewById(R.id.et_email);
        et4=(EditText) findViewById(R.id.et_phone);
        et5=(EditText) findViewById(R.id.et5);
        iv_edit_mail= (ImageView) findViewById(R.id.iv_edit_mail);
        iv_edit_phone = (ImageView) findViewById(R.id.iv_edit_phone);

        iv_edit_phone.setOnClickListener(this);
        iv_edit_mail.setOnClickListener(this);

        mRef=new Firebase("https://testing-9f5eb.firebaseio.com/Registration");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map= dataSnapshot.getValue(Map.class);

                String email=map.get("email");
                String name=map.get("name");
                String number=map.get("number");
                String password=map.get("password");

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_activity, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v==iv_edit_mail){
            et3.setImeOptions(IME_ACTION_DONE);
            et3.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et3.setEnabled(true);
            et3.setOnEditorActionListener(this);


        }else if(v==iv_edit_phone){
            et4.setImeOptions(IME_ACTION_DONE);
            et4.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et4.setEnabled(true);
            et4.setOnEditorActionListener(this);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et3.setEnabled(false);
            et4.setEnabled(false);
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

}
