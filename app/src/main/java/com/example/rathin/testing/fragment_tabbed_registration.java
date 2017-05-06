
package com.example.rathin.testing;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class fragment_tabbed_registration extends Fragment implements View.OnClickListener{

    private EditText etPassword,etName,etEmail,etMobile,etPasswordConfirm;
    private Button btRegister;
    private ProgressDialog progressDialog;
    private DatabaseReference mFirebaseDatabase,mTimeRef;
    StorageReference mImageRef;
    private FirebaseAuth firebaseAuth;

    private DatePickerFragment datePickerFragment;
    private static Calendar dateTime = Calendar.getInstance();
    static int mYear, mMonth, mDay;
    static EditText displayDOB;
    static final int RESULT_LOAD_IMAGE=1;
    ImageView iv_upload;
    byte[] byteArray;
    long serverTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_registration, container, false);
        firebaseAuth =FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Registration");
        mImageRef = FirebaseStorage.getInstance().getReference("ProfilePictures");
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etMobile = (EditText) rootView.findViewById(R.id.etMobile);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPasswordConfirm = (EditText) rootView.findViewById(R.id.etPasswordConfirm);
        btRegister = (Button) rootView.findViewById(R.id.btRegister);
        displayDOB=(EditText)rootView.findViewById(R.id.displayDOB);
        iv_upload = (ImageView) rootView.findViewById(R.id.iv_upload);

        iv_upload.setOnClickListener(this);
        displayDOB.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        btRegister.setOnClickListener(this);

        return rootView;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year,month,day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day)
        {
            mYear = year;
            mMonth = month+1;
            mDay = day;
            dateTime.set(mYear,mMonth,mDay);
            displayDOB.setText(day+"-"+mMonth+"-"+year);
        }
    }


    private void SaveInformation(){
        String email= etEmail.getText().toString().trim();
        String password =etPassword.getText().toString().trim();
        String confirm =etPasswordConfirm.getText().toString().trim();
        String name= etName.getText().toString().trim();
        String number=etMobile.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(getContext(), "Enter Valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (password.length() < 8) {
            Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(confirm.equals(password))) {
            etPasswordConfirm.setError("Must be same as the password given above");

        }
        else if (TextUtils.isEmpty(number)) {
            Toast.makeText(getContext(), "Enter Number!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (number.length() != 10) {
            Toast.makeText(getContext(), "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((confirm.equals(password))) {
            progressDialog.setMessage("Registering.");
            progressDialog.show();
            sendVerificationEmail();
            firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getActivity(), "Welcome to DDB Mudra", Toast.LENGTH_SHORT).show();


                            if (!task.isSuccessful()) {
                                etName.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                                etPasswordConfirm.setText("");
                                etMobile.setText("");
                                displayDOB.setText("");
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "SignUp failed Do again", Toast.LENGTH_SHORT).show();

                            } else {
                                String name = etName.getText().toString().trim();
                                String email = etEmail.getText().toString().trim();
                                String password = etPassword.getText().toString().trim();
                                String number = etMobile.getText().toString().trim();
                                String birthdate=displayDOB.getText().toString().trim();
                                EncryptPassword enc_pass = new EncryptPassword();
                                try {
                                    password = enc_pass.SHA1Hash(password);
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                mTimeRef= FirebaseDatabase.getInstance().getReference("CurrentTime");
                                mTimeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        serverTime =(long)dataSnapshot.getValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                String joiningdate = new SimpleDateFormat("dd-MM-yyyy").format(serverTime);
                                UserInformation userInformation = new UserInformation(name, email, password, number,joiningdate,birthdate);
                                email = email.replaceAll("[-_$.:,/]","");
                                StorageReference myRef = mImageRef.child(email+".jpg");
                                UploadTask mTask = myRef.putBytes(byteArray);
                                mTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"Something went wrong:"+e.toString(),Toast.LENGTH_SHORT);
                                    }
                                });
                                mFirebaseDatabase.child(email).setValue(userInformation);
                                etName.setText("");
                                etEmail.setText("");
                                etPasswordConfirm.setText("");
                                etMobile.setText("");
                                displayDOB.setText("");
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else
        {
            etPasswordConfirm.setError("Must be same as the password given above");

        }

    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),"Verify your E-mail", Toast.LENGTH_SHORT).show();
                            }
                            else if(!task.isSuccessful())
                            {
                                Toast.makeText(getActivity(),"Do again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }
    @Override
    public void onClick(View v) {
        if (v == btRegister) {
            SaveInformation();


        }
        if(v==displayDOB) {
            datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getActivity().getFragmentManager(), "Select your birthdate");
        }

        if (v==iv_upload){
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap mBitap = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byteArray = baos.toByteArray();
            iv_upload.setImageBitmap(mBitap);
        }
    }
}