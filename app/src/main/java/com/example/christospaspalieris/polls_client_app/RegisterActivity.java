package com.example.christospaspalieris.polls_client_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Button buttonRegister;
    private EditText editTextEmail,editTextPassword,editTextUserName,editTextFirstName,editTextLastName, editTextAge;
    private String sex = "",topic = "";


    private RadioButton male,female;
    private RadioGroup choice_sex;

    private CheckBox weather, politics, sports;

    private ProgressDialog mProgress;


    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister=(Button)findViewById(R.id.buttonRegister);

        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextFirstName = (EditText)findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText)findViewById(R.id.editTextLastName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextAge = (EditText)findViewById(R.id.editAge);

        weather = (CheckBox)findViewById(R.id.register_weather);
        politics = (CheckBox)findViewById(R.id.register_politics);
        sports = (CheckBox)findViewById(R.id.register_sports);

        male = (RadioButton) findViewById(R.id.radiomale);
        female = (RadioButton) findViewById(R.id.radiofemale);
        choice_sex = (RadioGroup) findViewById(R.id.radiogroup);


        mProgress = new ProgressDialog(this);



        mAuth = FirebaseAuth.getInstance();


        dbReference = FirebaseDatabase.getInstance().getReference("USERS");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public void onTopicsSelected(View view){

        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.register_weather:

                if (checked)
                {
                    weather.setChecked(true);
                    Log.d(TAG,weather.getText().toString());
                    //  main.putExtra("Topic",weather_btn.getText().toString());
                    politics.setChecked(false);
                    sports.setChecked(false);
                    topic = weather.getText().toString();
                }
                break;
            case R.id.register_politics:
                // politics_btn.setChecked(true);
                if (checked)
                {
                    politics.setChecked(true);
                    Log.d(TAG,politics.getText().toString());
                    //  main.putExtra("Topic",politics_btn.getText().toString());
                    weather.setChecked(false);
                    sports.setChecked(false);
                    topic = politics.getText().toString();
                }
                break;
            case R.id.register_sports:
                // sports_btn.setChecked(true);
                if(checked)
                {
                    sports.setChecked(true);
                    Log.d(TAG,sports.getText().toString());
                    //  main.putExtra("Topic",sports_btn.getText().toString());

                    politics.setChecked(false);
                    weather.setChecked(false);

                    topic = sports.getText().toString();
                }
                break;
        }
    }

    private void registerUser() {

        String username = editTextUserName.getText().toString().trim();
        String FirstName = editTextFirstName.getText().toString().trim();
        String LastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();



        if(TextUtils.isEmpty(username)){
            Toast.makeText(getApplicationContext(),"Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(FirstName)){
            Toast.makeText(getApplicationContext(),"Please enter first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(LastName)){
            Toast.makeText(getApplicationContext(),"Please enter last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(age)){
            Toast.makeText(getApplicationContext(),"Please enter your age", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!male.isChecked() && !female.isChecked()){
            Toast.makeText(getApplicationContext(),"Please enter your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!weather.isChecked() && !politics.isChecked() && !sports.isChecked()){
            Toast.makeText(getApplicationContext(),"Please enter your topic", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgress.setMessage("Registering User and\nSigning In");
        mProgress.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SaveUserInfo();

                    mProgress.dismiss();

                    Intent educationIntent = new Intent(getApplicationContext(), MainActivity.class);
                    educationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(educationIntent);



                }
                else {
                    Toast.makeText(getApplicationContext()
                            ,"Error while login", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }

            }
        });
    }

    private void SaveUserInfo(){
        String username = editTextUserName.getText().toString().trim();
        String firstname = editTextFirstName.getText().toString().trim();
        String lastname = editTextLastName.getText().toString().trim();
        String email_address = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String age_group;
        if(Integer.valueOf(age) <18)
            age_group = "Children";
        else
            age_group = "Adults";

        if(male.isChecked())
            sex = "male";
        if(female.isChecked())
            sex = "female";

        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        FirebaseMessaging.getInstance().subscribeToTopic(sex);
        FirebaseMessaging.getInstance().subscribeToTopic(age_group);

        UserInformation userInformation = new UserInformation(username,firstname,lastname,email_address,password,age,sex,topic,age_group);
        FirebaseUser user = mAuth.getCurrentUser();

        dbReference.child(user.getUid()).setValue(userInformation);

    }
}
