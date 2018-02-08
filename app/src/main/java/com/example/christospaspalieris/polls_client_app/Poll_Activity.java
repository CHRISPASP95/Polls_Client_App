package com.example.christospaspalieris.polls_client_app;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;


public class Poll_Activity extends AppCompatActivity {

  DatabaseReference Poll_Ref;
  DatabaseReference Results_Poll_Ref;


  String POLL_KEY = "", TOPIC = "";
  int counter1 = 0, counter2 = 0, counter3 = 0;


  ProgressBar data;

  TextView q1;
  Button Btn_Submit;

  RadioButton rb1_q1, rb2_q1, rb3_q1;
  RadioGroup radioGroup1;

  private static String TAG = "Poll_Activity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_poll_);
    Poll_Ref = FirebaseDatabase.getInstance().getReference("polls");
    Results_Poll_Ref = FirebaseDatabase.getInstance().getReference("Results");

    // mysocket.connect();

    // mysocket.emit("android_socket","Hello From Android App");

    q1 = (TextView) findViewById(R.id.question_1);

    Btn_Submit = (Button) findViewById(R.id.submit_result);

    rb1_q1 = (RadioButton) findViewById(R.id.btn1_question1);
    rb2_q1 = (RadioButton) findViewById(R.id.btn2_question1);
    rb3_q1 = (RadioButton) findViewById(R.id.btn3_question1);
    radioGroup1 = (RadioGroup) findViewById(R.id.radiogroup1);

    data = (ProgressBar) findViewById(R.id.test_progressbar);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      POLL_KEY = bundle.getString("get_tapped_question");
      Log.d(TAG + "POLL_KEY", POLL_KEY);

      TOPIC = bundle.getString("poll_topic");

      Log.d(TAG + "POLL_KEY", TOPIC);

      // GetQuestion(TOPIC,POLL_KEY);
    }

//        QuestionTopic = TOPIC;
//        Key = POLL_KEY;
    Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").addValueEventListener(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        q1.setText(String.valueOf(dataSnapshot.getValue()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    Poll_Ref.child(TOPIC).child(POLL_KEY).child("Choices").child("Choice1").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG + "Choice1", dataSnapshot.getKey());
        Log.d(TAG + "Choice1", String.valueOf(dataSnapshot.getValue()));
        rb1_q1.setText(String.valueOf(dataSnapshot.getValue()));
        // counter1 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb1_q1.getText().toString()).getValue()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    Poll_Ref.child(TOPIC).child(POLL_KEY).child("Choices").child("Choice2").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG + "Choice2", dataSnapshot.getKey());
        rb2_q1.setText(String.valueOf(dataSnapshot.getValue()));
        // counter2 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb2_q1.getText().toString()).getValue()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    Poll_Ref.child(TOPIC).child(POLL_KEY).child("Choices").child("Choice3").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG + "Choice3", dataSnapshot.getKey());
        rb3_q1.setText(String.valueOf(dataSnapshot.getValue()));
        data.setVisibility(View.GONE);
        // counter3 = Integer.parseInt(String.valueOf(dataSnapshot.child(rb3_q1.getText().toString()).getValue()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });


    Btn_Submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
//        Intent intent = new Intent(getApplicationContext(),PollsTopicActivity.class);
//        intent.putExtra("poll_topic", TOPIC);
//        startActivity(intent);
        finish();
      }
    });

    Log.d("EEE", "FDSFSFSFSS");

    //POLL_KEY = id erotisis
    //TOPIC = thema


  }

  private void GetQuestion(String QuestionTopic, String Key) {


  }


  @Override
  public void onBackPressed() {
  }


  public void RadioButtons_Selected(View view) {
    boolean checked = ((RadioButton) view).isChecked();

    switch (view.getId()) {
      case R.id.btn1_question1:
        if (checked) {
          rb2_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          counter1++;
          //Poll_Ref.child(POLL_KEY).child(TOPIC).child(question1_id).child("Question").child("Choice1").child(rb1_q1.getText().toString()).setValue(counter1);
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("answers").child("Choice1").child(rb1_q1.getText().toString()).setValue(counter1);
        }
        break;
      case R.id.btn2_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb3_q1.setEnabled(false);
          counter2++;
          //  Poll_Ref.child(POLL_KEY).child(TOPIC).child(question1_id).child("Question").child("Choice2").child(rb2_q1.getText().toString()).setValue(counter2);
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("answers").child("Choice2").child(rb2_q1.getText().toString()).setValue(counter2);
        }
        break;
      case R.id.btn3_question1:
        if (checked) {
          rb1_q1.setEnabled(false);
          rb2_q1.setEnabled(false);
          counter3++;
          //  Poll_Ref.child(POLL_KEY).child(TOPIC).child(question1_id).child("Question").child("Choice3").child(rb3_q1.getText().toString()).setValue(counter3);
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("question").setValue(q1.getText().toString());
          Results_Poll_Ref.child(TOPIC).child(POLL_KEY).child("Question").child("answers").child("Choice3").child(rb3_q1.getText().toString()).setValue(counter3);
        }
        break;


    }
  }

   /* public void Receive_Polls(final String TOPIC, final String Question_id){

        final HashMap<String,String> Poll_Values = new HashMap<>();
        //Log.d(TAG + "Question_id",Question_id);
        //Log.d(TAG + "TOPIC",TOPIC);
        Poll_Ref.child(POLL_KEY).child(TOPIC).addValueEventListener(new ValueEventListener() {
            @Override   //Log.d(TAG,dataSnapshot.getKey());
            public void onDataChange(DataSnapshot dataSnapshot) {

                Poll_Values.put("question", dataSnapshot.child(Question_id).child("Question").child("question").getValue().toString());
                Poll_Values.put("choice1", dataSnapshot.child(Question_id).child("Question").child("Choices").child("choice1").getValue().toString());
                Poll_Values.put("choice2", dataSnapshot.child(Question_id).child("Question").child("Choices").child("choice2").getValue().toString());
                Poll_Values.put("choice3", dataSnapshot.child(Question_id).child("Question").child("Choices").child("choice3").getValue().toString());

                if(Question_id.equals(question1_id)){
                    Log.d(TAG + "question1_id",Question_id);
                    Log.d(TAG + "question",Poll_Values.get("question"));

                    Log.d(TAG + "question",Poll_Values.get("choice1"));
                    Log.d(TAG + "question",Poll_Values.get("choice2"));
                    Log.d(TAG + "question",Poll_Values.get("choice3"));

                    q1.setText(Poll_Values.get("question"));
                    rb1_q1.setText(Poll_Values.get("choice1"));
                    rb2_q1.setText(Poll_Values.get("choice2"));
                    rb3_q1.setText(Poll_Values.get("choice3"));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


}


//    private Socket mysocket, mysocket_Choice1, mysocket_Choice2, mysocket_Choice3;
//    {
//        try {
//            mysocket = IO.socket("http://192.168.1.5:1337/");
//           // mysocket_Choice1 = IO.socket("http://192.168.1.5:1337/");
//           // mysocket_Choice2 = IO.socket("http://192.168.1.5:1337/");
//         //   mysocket_Choice3 = IO.socket("http://192.168.1.5:1337/");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

//    private Socket mysocket_Choice1;
//    {
//        try {
//            mysocket_Choice1 = IO.socket("http://192.168.1.5:1337/");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

//    private Socket mysocket_Choice2;
//    {
//        try {
//            mysocket_Choice2 = IO.socket("http://192.168.1.5:1337/");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

//    private Socket mysocket_Choice3;
//    {
//        try {
//            mysocket_Choice3 = IO.socket("http://192.168.1.5:1337/");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }