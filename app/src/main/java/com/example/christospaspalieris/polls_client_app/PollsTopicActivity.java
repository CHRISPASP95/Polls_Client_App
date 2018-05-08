package com.example.christospaspalieris.polls_client_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;


public class PollsTopicActivity extends AppCompatActivity {

  static String TAG = "PollsTopicActivity";
  public String userId = "", Level = "", QuestionKey = "", getTappedQuestion;
  private RecyclerView userQuestionsList;
  DatabaseReference userPolls, pollsRef, dbRef;
  Context mContext;
  PollHandler pollHandler;
  Map<String,Object> pollsKeysAnswered = new HashMap<>();

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra("User_Level",Level);
    startActivity(intent);
    this.finish();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(dbRef!=null){
      pollHandler.setAnsweredKeys(pollsKeysAnswered);
      dbRef.removeValue(); //!!!!!
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_polls_topic);
    mContext = PollsTopicActivity.this;

    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      Level = bundle.getString("poll_level");
      Log.d(TAG + "TOPIC", Level);
      pollHandler = new PollHandler(Level);
      userPolls = FirebaseDatabase.getInstance().getReference("USERS").child(userId).child(Level).child("UnAnswered");
    }
    pollsRef = FirebaseDatabase.getInstance().getReference("polls");
    userQuestionsList = (RecyclerView) findViewById(R.id.poll_list);
    userQuestionsList.setHasFixedSize(true);
    userQuestionsList.setLayoutManager(new LinearLayoutManager(mContext));

    Log.d(TAG + "USERID", userId);
    Runnable runnable = () -> {

      if (userPolls != null) {
        final FirebaseRecyclerAdapter<String, QuestionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, QuestionViewHolder>(
            String.class,
            R.layout.question_poll,
            QuestionViewHolder.class,
            userPolls
        ) {
          @Override
          protected void populateViewHolder(final QuestionViewHolder viewHolder, final String model, int position) {
            QuestionKey = getRef(position).getKey();
            Log.d(TAG + "getUser", QuestionKey);
            Log.d(TAG + "userPolls.getRef()", String.valueOf(userPolls.getRef()));
            pollsRef.child(Level).child(QuestionKey).child("Question").addValueEventListener(new ValueEventListener() {
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

            viewHolder.BtnClick.setOnClickListener((v) -> {
              dbRef = getRef(viewHolder.getQuestionPosition());
              getTappedQuestion = dbRef.getKey();
              pollsKeysAnswered.put(getTappedQuestion, getTappedQuestion);
              Intent poll_activity = new Intent(PollsTopicActivity.this, Poll_Activity.class);
              Log.d(TAG + " getUser", String.valueOf(getTappedQuestion));
              Log.d(TAG + " From the adapter", String.valueOf(dbRef));
              Log.d(TAG + " TOPIC", Level);
              Log.d(TAG + " getTappedQuestion", String.valueOf(getTappedQuestion));
              Bundle mBundle = new Bundle();
              mBundle.putString("poll_level", Level);
              mBundle.putString("getTappedQuestion", getTappedQuestion);
              poll_activity.putExtras(mBundle);
              startActivity(poll_activity);
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
    };

    Thread thread = new Thread(runnable);
    thread.start();

    thread.interrupt();
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