package com.example.rathin.testing;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class fragment_attendance_history extends Fragment implements CalendarListener{

    CustomCalendarView cv;
    boolean photo_flag;
    Calendar currentCalendar;
    String uEmail;
    ViewGroup.LayoutParams param;
    LinearLayout mLayout;

    FirebaseAuth firebaseAuth;
    Firebase mRef;
    DatabaseReference mTimeRef;
    Map<String,Object> map;
    long serverTime;

    SharedPreferences sp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_history, container, false);
        cv = (CustomCalendarView) rootView.findViewById(R.id.ccv_calendar_view);
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        cv.setCalendarListener(this);

        param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayout = (LinearLayout) rootView.findViewById(R.id.wrapper_layout);

        sp = getContext().getSharedPreferences("mypref",MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uEmail=user.getEmail();
        uEmail = uEmail.replaceAll("[-_$.,]","");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int count = sp.getInt("ImageCount",0);
        /*Check if exactly 3 images are taken or not. If yes, set a flag to true, otherwise false.*/
        if (count==3){
            photo_flag=true;
        } else{
            photo_flag=false;
        }
        Activity act = (Activity)getContext();
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                change_color();
            }
        });
    }

    /*change_color() method will set Decorators to modify a particular date in our CustomCalendarView*/
    public void change_color(){
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        cv.setDecorators(decorators);
        cv.refreshCalendar(currentCalendar);
    }

    /*onDateSelected is a listener implemented to get the selected date and retrieve the attendance data of that date*/
    @Override
    public void onDateSelected(Date date) {
        final String mFolderName = new SimpleDateFormat("MM-yy/dd").format(date);
        mRef = new Firebase("https://testing-9f5eb.firebaseio.com/ImageNames/"+uEmail+"/"+mFolderName);
        mLayout.removeAllViews();

        mRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               String[] temp = new String[50];
               int i=0;
               for (DataSnapshot ds: dataSnapshot.getChildren()) {
                   temp[i]=ds.getKey();
                   TextView tv = new TextView(getActivity());
                   tv.setText("Attendance no."+i+" taken at: "+temp[i]);
                   mLayout.addView(tv);
                   i++;
               }
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
        });
    }

    @Override
    public void onMonthChanged(Date date) {

    }


    /*ColorDecorator is the method which modifies the background color of dates, although it'snot the only thing it can do.*/
    public class ColorDecorator implements DayDecorator, com.google.firebase.database.ValueEventListener {
        @Override
        public void decorate(DayView dayView) {
            int color;
            //Get today's date from calendar
            String timeStamp = new SimpleDateFormat("yyyy-mm-dd").format(dayView.getDate());

            mTimeRef = FirebaseDatabase.getInstance().getReference("CurrentTime");

            //Get time from Firebase server
            mTimeRef.child("Time").setValue(ServerValue.TIMESTAMP);
            mTimeRef.addValueEventListener(this);

            String timestamp2 = new SimpleDateFormat("yyyy-mm-dd").format(serverTime);
            String parts[]= timeStamp.split("-");
            String parts2[]= timestamp2.split("-");
            int cur_year=Integer.parseInt(parts2[0]);
            int cur_month=Integer.parseInt(parts2[1]);
            int cur_day=Integer.parseInt(parts2[2]);
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            //if calendar's today's date is same as system's today's date, change the color.
            if(year==cur_year && month==cur_month && day==cur_day) {
                if(photo_flag) {
                    color = Color.parseColor("#00E676");
                }else{
                    color = Color.parseColor("#EF5350");
                }
                dayView.setBackgroundColor(color);
            }
        }

        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
            map = (Map<String,Object>) dataSnapshot.getValue();
            serverTime = (long) map.get("Time");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
