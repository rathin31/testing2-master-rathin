package com.example.rathin.testing;



import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.stacktips.view.utils.CalendarUtils;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class fragment_attendance_history extends Fragment{

    public CustomCalendarView cv;
    public boolean photo_flag;
    public Calendar currentCalendar;
    public TextView tv;

    SharedPreferences sp;


    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth firebaseAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_history, container, false);
        cv = (CustomCalendarView) rootView.findViewById(R.id.ccv_calendar_view);
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        tv = (TextView) rootView.findViewById(R.id.section_label);
        sp = getContext().getSharedPreferences("mypref",MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Attendance");
        return rootView;
    }



    @Override
    public void onResume() {
        int count = sp.getInt("ImageCount",0);
        if (count==3){
            tv.setText("success");
            photo_flag=true;
        } else{
            tv.setText("count="+count);
            photo_flag=false;
        }
        change_color();
        super.onResume();
    }

    public void change_color(){
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        cv.setDecorators(decorators);
        cv.refreshCalendar(currentCalendar);
    }

    public class ColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            int color;
            String timeStamp = new SimpleDateFormat("yyyy-mm-dd").format(dayView.getDate());
            String timestamp2 = new SimpleDateFormat("yyyy-mm-dd").format(new Date());
            String parts[]= timeStamp.split("-");
            String parts2[]= timestamp2.split("-");
            int cur_year=Integer.parseInt(parts2[0]);
            int cur_month=Integer.parseInt(parts2[1]);
            int cur_day=Integer.parseInt(parts2[2]);
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);


            if(year==cur_year && month==cur_month && day==cur_day) {
                if(photo_flag) {
                    color = Color.parseColor("#00E676");


                    String photoflag="Completed";
                    String timeStamp1 = new SimpleDateFormat("/MM/dd-yy_HH:mm:ss a").format(new Date());
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String Email=user.getEmail();
                    String result = Email.replaceAll("[-_$.:,/]","");
                    String finalPhotoFlag= result+timeStamp1+" "+ "True";
                    Attendance attendance=new Attendance(photoflag);
                    mFirebaseDatabase.child(finalPhotoFlag).setValue(attendance);


                }else{
                    color = Color.parseColor("#EF5350");
                }
                dayView.setBackgroundColor(color);
            }
        }
    }
}
