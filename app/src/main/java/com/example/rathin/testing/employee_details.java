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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class employee_details extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    private EditText et1,et_email,et_phone,et4,et5;
    private Firebase mRef;
    private ArrayList<String> musername;
    private ImageView iv_edit_mail, iv_edit_phone;

    private Button save;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        et1 = (EditText) findViewById(R.id.et1);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et5 = (EditText) findViewById(R.id.et5);
        save=(Button) findViewById(R.id.save) ;
        iv_edit_mail = (ImageView) findViewById(R.id.iv_edit_mail);
        iv_edit_phone = (ImageView) findViewById(R.id.iv_edit_phone);

        iv_edit_phone.setOnClickListener(this);
        iv_edit_mail.setOnClickListener(this);

        save.setOnClickListener(this);

        firebaseAuth =FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Employee Details");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_activity, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v==iv_edit_mail){
            et_email.setImeOptions(IME_ACTION_DONE);
            et_email.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et_email.setEnabled(true);
            et_email.setOnEditorActionListener(this);


        }else if(v==iv_edit_phone){
            et4.setImeOptions(IME_ACTION_DONE);
            et4.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et4.setEnabled(true);
            et4.setOnEditorActionListener(this);
        }
        if(v==save)
        {

            EditDetails();
        }
    }

    public void EditDetails()
    {

        String email = et_email.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uEmail=user.getEmail();
        String result = uEmail.replaceAll("[-_$.:,/]","");
        EmployeeEd employeeEd= new EmployeeEd(email,phone,result);

        mFirebaseDatabase.child(result).setValue(employeeEd);





    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et_email.setEnabled(false);
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
