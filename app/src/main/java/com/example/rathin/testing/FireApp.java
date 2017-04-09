package com.example.rathin.testing;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Nisarg k Patel on 22-03-2017.
 */

public class FireApp extends Application {

    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);

    }



}
