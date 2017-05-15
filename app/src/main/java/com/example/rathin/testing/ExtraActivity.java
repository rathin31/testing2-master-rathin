package com.example.rathin.testing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtraActivity extends AppCompatActivity implements View.OnClickListener{


    private Button button4;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(this);



    }

    public void  Extraact()
    {


        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Attendance");
       // String photoflag="";
        String timeStamp = new SimpleDateFormat("/dd-MM-yy_HH:mm:ss a").format(new Date());
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String Email=user.getEmail();
        String result = Email.replaceAll("[-_$.:,/]","");
        String finalPhotoFlag= result+timeStamp;
        //Attendance attendance=new Attendance(photoflag);
        mFirebaseDatabase.child(finalPhotoFlag);
    }

    @Override
    public void onClick(View v) {
        if(v==button4)
        {
            Extraact();
        }
    }
}
