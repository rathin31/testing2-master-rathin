package com.example.rathin.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.R.attr.data;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class fragment_attendance_take extends android.support.v4.app.Fragment implements View.OnClickListener, ValueEventListener {

    Button btCamera,btUpload, btCancel;
    ImageView mImageView;
    CardView cv;
    String selectedImagePath;
    LinearLayout mLayout;
    ProgressDialog progressDialog;
    int count=1;

    FirebaseAuth firebaseAuth;
    DatabaseReference mFirebaseDatabase,mTimeRef;
    StorageReference storage;

    static final int CAM_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String URI = "uri";
    public String path = "sdcard/camera_app/";
    public String filename = "cam_image.jpg";
    public Bitmap bitmap;
    public byte[] bytedata;
    private TimerTask ResetParam;
    Map<String,Object> map;
    long serverTime;
    String Email;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_take, container, false);
        btCamera = (Button) rootView.findViewById(R.id.btCamera);
        btUpload = (Button) rootView.findViewById(R.id.bt_upload);
        btCancel = (Button) rootView.findViewById(R.id.bt_cancel);
        mImageView = (ImageView) rootView.findViewById(R.id.mImageView);
        cv = (CardView) rootView.findViewById(R.id.cv_camera);
        mLayout = (LinearLayout) rootView.findViewById(R.id.parent_linear);
        storage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("ImageNames");
        mTimeRef = FirebaseDatabase.getInstance().getReference("CurrentTime");
        mTimeRef.child("Time").setValue(ServerValue.TIMESTAMP);
        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Email=user.getEmail();
        //Get time from Firebase server
        mTimeRef.addValueEventListener(this);

        btUpload.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btCamera.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());

        sp = getContext().getSharedPreferences("mypref",MODE_PRIVATE);
        editor= sp.edit();

        return rootView;
    }

    public void onClick(View v) {
         if (v == btCamera) {
             if(sp.getBoolean("camera_enabled",true)) {
                 Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 File file = getFile();
                 //FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
                 try {
                     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                         camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), "com.example.rathin.testing.fileprovider", file));
                     } else {
                         camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                     }

                     startActivityForResult(camera_intent, CAM_REQUEST);
                 } catch (Exception e) {
                     Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                 }
             }else{
                 Toast.makeText(getActivity(),"3 images of the day have already been taken. Thank you!",Toast.LENGTH_LONG).show();
             }
         }else if (v==btUpload){
             progressDialog.setMessage("Image is Uploading");
             progressDialog.show();

             Log.v("Outside onDataChange",new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(serverTime));
             String timeStamp = new SimpleDateFormat("/MM-yy/dd/HH:mm:ss").format(new Date(serverTime));
             Log.v("Before Upload",timeStamp);
             String imageFileName = Email + timeStamp ;
             imageFileName = imageFileName.replaceAll("[_$.,]","");
             mFirebaseDatabase.child(imageFileName).setValue(true);
             StorageReference mountainsRef = storage.child(imageFileName+".jpg");
             UploadTask uploadTask = mountainsRef.putBytes(bytedata);
             uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Uri downloadUrl = taskSnapshot.getDownloadUrl();
                     progressDialog.dismiss();
                     Toast.makeText(getActivity(), "Your attendance is taken..", Toast.LENGTH_SHORT).show();
                     if(count<3) {
                         Log.v("Image No", ""+count);
                         count++;
                         editor.putInt("ImageCount", count);
                         editor.commit();
                     }else{
                         Log.v("Count Status","More than 3");
                         Calendar dayNext = Calendar.getInstance();
                         dayNext.add(Calendar.DAY_OF_MONTH,1);
                         dayNext.set(Calendar.HOUR_OF_DAY,0);
                         dayNext.set(Calendar.MINUTE,0);
                         dayNext.set(Calendar.SECOND,0);
                         dayNext.set(Calendar.MILLISECOND,0);
                         String t1 = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(dayNext.getTime());
                         Log.v("t1",t1);
                         Log.v("serverTime",""+serverTime);
                         Log.v("dayNaext time",""+dayNext.getTimeInMillis());
                         long timeToMidnight = dayNext.getTimeInMillis() - serverTime;
                         String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeToMidnight),
                                 TimeUnit.MILLISECONDS.toMinutes(timeToMidnight) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToMidnight)),
                                 TimeUnit.MILLISECONDS.toSeconds(timeToMidnight) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToMidnight)));
                         Log.v("Time To Midnight",hms);
                         editor.putBoolean("camera_enabled",false);
                         editor.commit();
                         Toast.makeText(getActivity(),"3 images taken. Time till attendance is enabled again: "+hms,Toast.LENGTH_LONG).show();
                         Timer resetTimer = new Timer();
                         ResetParam= new TimerTask() {
                             @Override
                             public void run() {
                                 count=1;
                                 editor.putBoolean("camera_enabled",true);
                                 editor.putInt("ImageCount",1);
                                 editor.commit();
                             }
                         };
                         resetTimer.schedule(ResetParam,timeToMidnight);

                     }
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception exception) {
                 }
             });
             Activity act = (Activity)getContext();
             act.runOnUiThread(new Runnable(){
                 @Override
                 public void run() {
                         mLayout.setVisibility(View.GONE);
                         cv.setVisibility(View.VISIBLE);
                     }
             });
         }else if(v==btCancel){
             mLayout.setVisibility(View.GONE);
             cv.setVisibility(View.VISIBLE);
         }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(URI, "camera");
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Log.d(URI, "camera1");
                String path = selectedImagePath+"/" + filename;
                Log.d(URI,path + " URI");
                try{
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(path, options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,12, baos);
                    bytedata = baos.toByteArray();

                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    Bitmap newbitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

                    mImageView.setImageBitmap(newbitmap);
                }catch (Exception e){
                    Toast.makeText(getActivity(),"Oops!! Something went Wrong. Please Try again.",Toast.LENGTH_SHORT);
                }
                cv.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);




                // mImageView.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(URI, "camera2");
                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Log.d(URI, "camera3");
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Log.d(URI, "camera4");
        }
    }

    private File getFile() {
        File imagePath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            imagePath = new File(getActivity().getFilesDir(), "images");
            if (!imagePath.exists()) {
                imagePath.mkdir();
            }
            selectedImagePath=imagePath.getAbsolutePath();
        } else{
            imagePath = new File("sdcard/camera_app");
            if (!imagePath.exists()) {
                imagePath.mkdir();
            }
            selectedImagePath="sdcard/camera_app";
        }
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        File image_file = new File(imagePath, "cam_image.jpg" + ts);
        filename = "cam_image.jpg" + ts;
        return image_file;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        map = (Map<String,Object>)dataSnapshot.getValue();
        serverTime = (long) map.get("Time");
        Log.v("Inside onDataChange",new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(serverTime));
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
