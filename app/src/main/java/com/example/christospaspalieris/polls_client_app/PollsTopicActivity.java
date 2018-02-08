package com.example.christospaspalieris.polls_client_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class PollsTopicActivity extends AppCompatActivity {

  static String TAG = "PollsTopicActivity";

  public String user_id = "", TOPIC = "";

  private RecyclerView userQuestionsList;

  DatabaseReference userpolls, pollsRef;

  String get_user = "", get_tapped_question;


  DatabaseReference dbRef;

  Context mcontext;

  PollHandler pollHandler;
  Map<String,Object> polls_keys_Answered = new HashMap<>();


  @Override
  public void onBackPressed() {

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      Intent intent = new Intent(this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(dbRef!=null){
      pollHandler.setAnsweredKeys(polls_keys_Answered);
      dbRef.removeValue(); //!!!!!!
     //startActivity(new Intent(getApplicationContext(),PollsTopicActivity.class));
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_polls_topic);
    mcontext = PollsTopicActivity.this;


    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      TOPIC = bundle.getString("poll_topic");



      Log.d(TAG + "TOPIC", TOPIC);

      pollHandler = new PollHandler(TOPIC);
      userpolls = FirebaseDatabase.getInstance().getReference("USERS").child(user_id).child(TOPIC).child("UnAnswered");

      pollsRef = FirebaseDatabase.getInstance().getReference("polls");
    }

    userQuestionsList = (RecyclerView) findViewById(R.id.poll_list);
    userQuestionsList.setHasFixedSize(true);
    userQuestionsList.setLayoutManager(new LinearLayoutManager(mcontext));

    Log.d(TAG + "USERID", user_id);

    if (userpolls != null) {
      final FirebaseRecyclerAdapter<String, QuestionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, QuestionViewHolder>(
          String.class,
          R.layout.question_poll,
          QuestionViewHolder.class,
          userpolls
      ) {
        @Override
        protected void populateViewHolder(final QuestionViewHolder viewHolder, final String model, int position) {

          get_user = getRef(position).getKey();
         // keys_UnAswered.put(get_user,get_user);



         // Log.d(TAG + "keys_UnAswered", keys_UnAswered.toString());
          Log.d(TAG + "get_user", get_user);
          Log.d(TAG + "userpolls.getRef()", String.valueOf(userpolls.getRef()));

          pollsRef.child(TOPIC).child(get_user).child("Question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              Log.d(TAG, String.valueOf(dataSnapshot.getRef()));
              Log.d(TAG, String.valueOf(dataSnapshot.getValue()));
              viewHolder.setQuestion_display(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
          });

          viewHolder.BtnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dbRef = getRef(viewHolder.getQuestionPosition());
              get_tapped_question = dbRef.getKey();
              polls_keys_Answered.put(get_tapped_question,get_tapped_question);


              Intent poll_activity = new Intent(PollsTopicActivity.this, Poll_Activity.class);

              Log.d(TAG + " get_user", String.valueOf(get_tapped_question));
              Log.d(TAG + " From the adapter", String.valueOf(dbRef));
              Log.d(TAG + " TOPIC", TOPIC);
              Log.d(TAG + " get_tapped_question", String.valueOf(get_tapped_question));

              Bundle mBundle = new Bundle();
              mBundle.putString("poll_topic", TOPIC);
              mBundle.putString("get_tapped_question", get_tapped_question);
              poll_activity.putExtras(mBundle);
              poll_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

              startActivity(poll_activity);
            }
          });
        }
      };

      firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
          userQuestionsList.scrollToPosition(firebaseRecyclerAdapter.getItemCount());
        }
      });

      userQuestionsList.setAdapter(firebaseRecyclerAdapter);
    }

  }

  public static class QuestionViewHolder extends RecyclerView.ViewHolder {

    TextView question_display;
    Button BtnClick;
    int position;

    public QuestionViewHolder(View itemView) {
      super(itemView);
      question_display = (TextView) itemView.findViewById(R.id.question_display_item);
      BtnClick = (Button) itemView.findViewById(R.id.click_btn);

    }

    public void setQuestion_display(String question_text) {
      question_display.setText(question_text);
    }

    public int getQuestionPosition() {
      position = getAdapterPosition();
      return position;
    }
  }

}