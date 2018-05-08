package com.example.christospaspalieris.polls_client_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Button buttonRegister;
    private EditText editTextEmail,editTextPassword,editTextUserName,editTextFirstName,editTextLastName, editTextAge;
    private String sex = "",level = "";
    private RadioButton male,female;
    private CheckBox student, junior, senior;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextFirstName = (EditText)findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText)findViewById(R.id.editTextLastName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextAge = (EditText)findViewById(R.id.editAge);
        student = (CheckBox)findViewById(R.id.register_student);
        junior = (CheckBox)findViewById(R.id.register_junior);
        senior = (CheckBox)findViewById(R.id.register_senior);
        male = (RadioButton) findViewById(R.id.radiomale);
        female = (RadioButton) findViewById(R.id.radiofemale);
        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference("USERS");
        buttonRegister.setOnClickListener((v)-> registerUser());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this,LoginActivity.class));

    }

    public void onTopicsSelected(View view){

        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.register_student:
                if (checked)
                {
                    student.setChecked(true);
                    junior.setChecked(false);
                    senior.setChecked(false);
                    level = "Student";
                }
                break;
            case R.id.register_junior:
                if (checked)
                {
                    junior.setChecked(true);
                    student.setChecked(false);
                    senior.setChecked(false);
                    level = "Junior";
                }
                break;
            case R.id.register_senior:
                if(checked)
                {
                    senior.setChecked(true);
                    junior.setChecked(false);
                    student.setChecked(false);
                    level = "Senior";
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
        if(!(male.isChecked() || female.isChecked())){
            Toast.makeText(getApplicationContext(),"Please enter your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(student.isChecked() || junior.isChecked() || senior.isChecked())){
            Toast.makeText(getApplicationContext(),"Please choose your level", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgress.setMessage("Registering User and\nSigning In");
        mProgress.show();


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task)-> {
            if(task.isSuccessful()){
                SaveUserInfo();
                mProgress.dismiss();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra("User_Level",level);
                startActivity(mainIntent);
                this.finish();
            }
            else {
                Toast.makeText(getApplicationContext(),"Error while login ", Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
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

        if(male.isChecked())
            sex = "male";
        if(female.isChecked())
            sex = "female";

        FirebaseMessaging.getInstance().subscribeToTopic("Informatics");
        FirebaseMessaging.getInstance().subscribeToTopic(sex);
        FirebaseMessaging.getInstance().subscribeToTopic(level);

        UserInformation userInformation = new UserInformation(username,firstname,lastname,email_address,password,age,sex,"Informatics",level);
        FirebaseUser user = mAuth.getCurrentUser();
        dbReference.child(user.getUid()).setValue(userInformation);

    }
}
