package com.example.rathin.testing;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.google.firebase.auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class fragment_attendance_take extends android.support.v4.app.Fragment implements View.OnClickListener {
    public String path = "sdcard/camera_app/";
    public String filename = "cam_image.jpg";
    private String selectedImagePath;
    private static final String URI = "uri";
    private Button btCamera;
    private ImageView mImageView;
    Uri uri;
    private StorageReference storage;
    static final int CAM_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_take, container, false);
        btCamera = (Button) rootView.findViewById(R.id.btCamera);
        mImageView = (ImageView) rootView.findViewById(R.id.mImageView);
        storage = FirebaseStorage.getInstance().getReference();
        btCamera.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());


        return rootView;

    }

    public void onClick(View v) {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getFile();
        //FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,FileProvider.getUriForFile(getActivity(), "com.example.rathin.testing.fileprovider", file));
            } else{
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }

            startActivityForResult(camera_intent, CAM_REQUEST);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(URI, "camera");
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                progressDialog.setMessage("Image is Uploading");
                progressDialog.show();
                Log.d(URI, "camera1");
                String path = selectedImagePath+"/" + filename;
                Log.d(URI,path + " URI");
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytedata = baos.toByteArray();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new     Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                StorageReference mountainsRef = storage.child(imageFileName+".jpg");
                UploadTask uploadTask = mountainsRef.putBytes(bytedata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Your attendance is taken..", Toast.LENGTH_SHORT).show();

                    }
                });
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

}
