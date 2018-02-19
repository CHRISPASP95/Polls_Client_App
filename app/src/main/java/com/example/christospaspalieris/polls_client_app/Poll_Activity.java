package com.example.christospaspalieris.polls_client_app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Poll_Activity extends AppCompatActivity {

  DatabaseReference Poll_Ref;
  DatabaseReference Results_Poll_Ref;
  String POLL_KEY = "", Level = "";
  int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0, counter5 = 0;
  ProgressBar data;
  TextView q1;
  Button Btn_Submit;
  RadioButton rb1_q1, rb2_q1, rb3_q1,rb4_q1, rb5_q1;
  RadioGroup radioGroup1;
  private static String TAG = "Poll_Activity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_poll_);
    Poll_Ref = FirebaseDatabase.getInstance().getReference("polls");
    Results_Poll_Ref = FirebaseDatabase.getInstance().getReference("Results");  q1 = (TextView) findViewById(R.id.question_1);
    Btn_Submit = (Button) findViewById(R.id.submit_result);
    rb1_q1 = (RadioButton) findViewById(R.id.btn1_question1);
    rb2_q1 = (RadioButton) findViewById(R.id.btn2_question1);
    rb3_q1 = (RadioButton) findViewById(R.id.btn3_question1);
    rb4_q1 = (RadioButton) findViewById(R.id.btn4_question1);
    rb5_q1 = (RadioButton) findViewById(R.id.btn5_question1);
    radioGroup1 = (RadioGroup) findViewById(R.id.radiogroup1);
    data = (ProgressBar) findViewById(R.id.test_progressbar);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      POLL_KEY = bundle.getString("getTappedQuestion");
      Log.d(TAG + "POLL_KEY", POLL_KEY);
      Level = bundle.getString("poll_level");
      Log.d(TAG + "POLL_KEY", Level);
    }

    Poll_Ref.child(Level).child(POLL_KEY).child("Question").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        q1.setText(String.valueOf(dataSnapshot.getValue()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    Poll_Ref.child(Level).child(POLL_KEY).child("Choices").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
       for(DataSnapshot d : dataSnapshot.getChildren()){
         ShowQuestion(d.getKey());
       }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });



    Btn_Submit.setOnClickListener(v ->finish());
  }

  private void ShowQuestion(String key) {
    if(key.equals("Choice1")){
      Poll_Ref.child(Level).child(POLL_KEY).child("Choices").child("Choice1").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG + "Choice1", dataSnapshot.getKey());
          Log.d(TAG + "Choice1", String.valueOf(dataSnapshot.getValue()));
          rb1_q1.setText(String.valueOf(dataSnapshot.getValue()));

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
    if(key.equals("Choice2")){
      Poll_Ref.child(Level).child(POLL_KEY).child("Choices").child("Choice2").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG + "Choice2", dataSnapshot.getKey());
          rb2_q1.setText(String.valueOf(dataSnapshot.getValue()));
          data.setVisibility(View.GONE);
          // counter2 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb2_q1.getText().toString()).getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
    if(key.equals("Choice3")){
      rb3_q1.setVisibility(View.VISIBLE);
      Poll_Ref.child(Level).child(POLL_KEY).child("Choices").child("Choice3").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG + "Choice3", dataSnapshot.getKey());
          rb3_q1.setText(String.valueOf(dataSnapshot.getValue()));

          // counter3 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb3_q1.getText().toString()).getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
    if(key.equals("Choice4")){
      rb4_q1.setVisibility(View.VISIBLE);
      Poll_Ref.child(Level).child(POLL_KEY).child("Choices").child("Choice4").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG + "Choice4", dataSnapshot.getKey());
          rb4_q1.setText(String.valueOf(dataSnapshot.getValue()));

          // counter3 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb3_q1.getText().toString()).getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
    if(key.equals("Choice5")){
      rb5_q1.setVisibility(View.VISIBLE);
      Poll_Ref.child(Level).child(POLL_KEY).child("Choices").child("Choice5").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG + "Choice5", dataSnapshot.getKey());
          rb5_q1.setText(String.valueOf(dataSnapshot.getValue()));

          // counter3 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb3_q1.getText().toString()).getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
  }

  @Override
  public void onBackPressed() {
  }


  public void RadioButtons_Selected(View view) {
    boolean checked = ((RadioButton) view).isChecked();
    SaveResults(checked,view.getId());
  }

  private void SaveResults(boolean checked, int id) {
    if(id == R.id.btn1_question1 || id == R.id.btn2_question1 || id == R.id.btn3_question1 || id == R.id.btn4_question1 || id == R.id.btn5_question1)
      Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());

    switch (id) {
      case R.id.btn1_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          rb4_q1.setEnabled(false);
          rb5_q1.setEnabled(false);
          counter1++;
          Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("answers").child("Choice1").child(rb1_q1.getText().toString()).setValue(counter1);
        }
        break;
      case R.id.btn2_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          rb4_q1.setEnabled(false);
          rb5_q1.setEnabled(false);
          counter2++;
          //Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("answers").child("Choice2").child(rb2_q1.getText().toString()).setValue(counter2);
        }
        break;
      case R.id.btn3_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          rb4_q1.setEnabled(false);
          rb5_q1.setEnabled(false);
          counter3++;
          // Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("answers").child("Choice3").child(rb3_q1.getText().toString()).setValue(counter3);
        }
        break;
      case R.id.btn4_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          rb4_q1.setEnabled(false);
          rb5_q1.setEnabled(false);
          counter4++;
          //Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("answers").child("Choice4").child(rb4_q1.getText().toString()).setValue(counter4);
        }
        break;
      case R.id.btn5_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          rb4_q1.setEnabled(false);
          rb5_q1.setEnabled(false);
          counter5++;
          // Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(Level).child(POLL_KEY).child("Question").child("answers").child("Choice5").child(rb5_q1.getText().toString()).setValue(counter5);
        }
        break;
    }
  }
}
