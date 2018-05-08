package com.example.christospaspalieris.polls_client_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference dbReferenceUsers;
    private EditText editEmail,editPass;
    private Button btn_sign_in;
    private TextView btn_sign_up;
    private ProgressDialog progressLogin;
    private FirebaseUser user;
    private CheckConnectivity checkConnectivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressLogin = new ProgressDialog(this);
        checkConnectivity = new CheckConnectivity();

        if(!checkConnectivity.isConnected(LoginActivity.this)) {
            checkConnectivity.buildDialog(LoginActivity.this, LoginActivity.this).show();
        }
        else {
            setContentView(R.layout.activity_login);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            dbReferenceUsers = FirebaseDatabase.getInstance().getReference("USERS");
            editEmail = (EditText) findViewById(R.id.editEmail);
            editPass = (EditText) findViewById(R.id.editPassword);
            btn_sign_in = (Button) findViewById(R.id.signin);
            btn_sign_up = (TextView) findViewById(R.id.signup);
            Intent register = new Intent(getApplicationContext(),RegisterActivity.class);
            btn_sign_up.setOnClickListener((v)-> startActivity(register));
            btn_sign_in.setOnClickListener((v)-> {
                progressLogin.setMessage("Signing In");
                SignIn();
                if(!TextUtils.isEmpty(editEmail.getText().toString().trim())&&!TextUtils.isEmpty(editPass.getText().toString().trim())) {
                    progressLogin.show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please check your credentials",Toast.LENGTH_LONG).show();
                    progressLogin.dismiss();
                }
            });
        }
    }



    private void SignIn() {

        String getemail = editEmail.getText().toString().trim();
        String getpassword = editPass.getText().toString().trim();

        if(TextUtils.isEmpty(getemail)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(getpassword)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!TextUtils.isEmpty(getemail)&&!TextUtils.isEmpty(getpassword)) {
            mAuth.signInWithEmailAndPassword(getemail, getpassword)
                .addOnCompleteListener((task)-> {
                    if (task.isSuccessful()) {
                        checkUserExist();
                        progressLogin.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_LONG).show();
                        progressLogin.dismiss();
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"ONRESUME");
        if(user!=null)
        {
            progressLogin.setMessage("Signing In");
            progressLogin.show();
            checkUserExist();
        }else
            Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressLogin.dismiss();
        finish();
    }

    public void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        dbReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    String Level = String.valueOf(dataSnapshot.child(user_id).child("level").getValue());
                    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    mainActivity.putExtra("User_Level",Level);
                    startActivity(mainActivity);
                }else {
                    Toast.makeText(getApplicationContext(), "You have to setup your account", Toast.LENGTH_LONG).show();
                   progressLogin.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


