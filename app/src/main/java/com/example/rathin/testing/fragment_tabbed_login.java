package com.example.rathin.testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class fragment_tabbed_login extends Fragment implements View.OnClickListener{

    private AutoCompleteTextView actvEmail;
    private EditText etPassword;
    private Button btSignIn;
    private FirebaseAuth firebaseAuth;
    private TextView tvForgot;
    private ProgressDialog progressDialog;
    private ImageView iv_password_show;
    boolean show = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_login, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        firebaseAuth = FirebaseAuth.getInstance();
        actvEmail = (AutoCompleteTextView) rootView.findViewById(R.id.actvEmail);
        progressDialog = new ProgressDialog(getActivity());
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        tvForgot=(TextView)rootView.findViewById(R.id.tvForgot);
        iv_password_show=(ImageView)rootView.findViewById(R.id.iv_password_show);
        iv_password_show.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        btSignIn = (Button) rootView.findViewById(R.id.btSignIn);
        btSignIn.setOnClickListener(this);
        return rootView;
    }

    
    private void SignIn( ) {
        boolean valid = true;
        String email = actvEmail.getText().toString().trim();
        String Password = etPassword.getText().toString().trim();
        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            actvEmail.setError("Enter a valid email address");
        }else{
            actvEmail.setError(null);
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getActivity(), "Enter your Password", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.setMessage("Logging Please Wait..");
            progressDialog.show();
            firebaseAuth
                    .signInWithEmailAndPassword(email, Password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "You are Successfully Logged In", Toast.LENGTH_LONG).show();
                                Intent gotocam= new Intent(getActivity(),landing_page.class);
                                startActivity(gotocam);                                }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Logging In failed,Try again ", Toast.LENGTH_LONG).show();
                                actvEmail.setText("");
                                etPassword.setText("");
                                progressDialog.dismiss();
                            }
                        }
                    });
        }

    }



    @Override
    public void onClick(View v) {
        if(v== btSignIn) {
            SignIn();
        }
        if (v==tvForgot)
        {
            Intent rstpwd = new Intent(getActivity().getApplication(), ResetPasswordActivity.class);
            startActivity(rstpwd);

        }
        if(v==iv_password_show)
        {
            if(show){
                etPassword.setTransformationMethod(null);
                show = false;
            }else{
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                show = true;
            }

        }

    }
}
