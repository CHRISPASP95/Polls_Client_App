package com.example.christospaspalieris.polls_client_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;
    EditText editEmail,editPass;
    Button btn_sign_in, btn_sign_up;
    private ProgressDialog progressLogin;


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isConnected(LoginActivity.this))buildDialog(LoginActivity.this).show();
        else {
            setContentView(R.layout.activity_login);
            mAuth = FirebaseAuth.getInstance();
            progressLogin = new ProgressDialog(this);



            dbReference = FirebaseDatabase.getInstance().getReference("USERS");

            editEmail = (EditText) findViewById(R.id.editEmail);
            editPass = (EditText) findViewById(R.id.editPassword);
            btn_sign_in = (Button) findViewById(R.id.signin);
            btn_sign_up = (Button) findViewById(R.id.signup);



            btn_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                   // createUser(editEmail.getText().toString(),editPass.getText().toString());
                }
            });

            btn_sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressLogin.setMessage("Signing In");
                    SignIn();
                    if(!TextUtils.isEmpty(editEmail.getText().toString().trim())&&!TextUtils.isEmpty(editPass.getText().toString().trim())) {
                        progressLogin.show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Please check your credentials",Toast.LENGTH_LONG).show();
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
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkUserExist();
                                progressLogin.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(),"User doesn't exists",Toast.LENGTH_LONG).show();
                                progressLogin.dismiss();
                            }
                        }
                    });
        }
    }



    public static boolean isConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile!=null && mobile.isConnectedOrConnecting()) || (wifi!=null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        }
        else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or Wifi. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setCancelable(false);


        return builder;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }



    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    String Topic = String.valueOf(dataSnapshot.child(user_id).child("topic").getValue());
                    //  FirebaseMessaging.getInstance().subscribeToTopic(Topic);
                    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    mainActivity.putExtra("User_Topic",Topic);
                    //mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainActivity);
                }else {
                    Toast.makeText(getApplicationContext(),"You have to setup your account",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


