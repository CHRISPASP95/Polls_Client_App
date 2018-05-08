package com.example.christospaspalieris.polls_client_app;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

  private static String TAG = "MainActivity";
  private FirebaseAuth mAuth;
  private DatabaseReference userInfo, resultsKeysRef;
  private Dialog TopicsDialog;
  private TextView getTopic;
  private RadioButton studentBtn, juniorBtn, seniorBtn;
  private LinearLayout resultsLayout;
  private Button btnSubmitLevel, btnNext;
  private String userLevel = "", userId = "", sex = "", level = "", titleResult = "", id = "";
  private int resultsId = 0;
  private PieChart resultsPieChart;

  private List<PieEntry> usersResults;
  private List<String> resultKeys;
  private Results Resultsobj;

  @Override
  public void onBackPressed() {
  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(TAG, "onCreate");



    Bundle bundle = getIntent().getExtras();
    if (bundle != null)
    {
      userLevel = bundle.getString("User_Level");
      resultsKeysRef = FirebaseDatabase.getInstance().getReference("Results").child(userLevel);
    }

    Log.d(TAG + " userLevel", userLevel);

    resultKeys = new ArrayList<>();
    resultsLayout = (LinearLayout) findViewById(R.id.results_layout);



    mAuth = FirebaseAuth.getInstance();
    userId = mAuth.getCurrentUser().getUid();
    getTopic = (TextView) findViewById(R.id.display_topic);
    btnNext = (Button) findViewById(R.id.btn_next);

    usersResults = new ArrayList<>();
    resultsPieChart = (PieChart) findViewById(R.id.pie_graph_results);
    Resultsobj = new Results(resultKeys,id,resultsKeysRef,usersResults,resultsPieChart);
    userInfo = FirebaseDatabase.getInstance().getReference("USERS").child(userId);
    userInfo.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
          Log.d(TAG, String.valueOf(dataSnapshot1.getValue()));
          Log.d(TAG, String.valueOf(dataSnapshot1.getKey()));
          if (dataSnapshot1.getKey().equals("sex")) {
            sex = String.valueOf(dataSnapshot1.getValue());
            Log.d(TAG + " sex", sex);
          }
          if (dataSnapshot1.getKey().equals("level")) {
            level = String.valueOf(dataSnapshot1.getValue());
            Log.d(TAG + " level", level);
          }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("Informatics");
        FirebaseMessaging.getInstance().subscribeToTopic(sex);
        FirebaseMessaging.getInstance().subscribeToTopic(level);
        Log.d(TAG + " sex", sex);
        Log.d(TAG + " level", level);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    btnNext.setOnClickListener(v -> {
      Runnable runnable = ()-> {
        if (!userLevel.isEmpty()) {
          resultsId++;
          if (resultsId >= resultKeys.size()) {
            Log.d(TAG + "resultsId", String.valueOf(resultsId));
            resultsId = 0;
          }
          Log.d(TAG + " thread1", "Thread1 is called");
          Resultsobj.Show_Results(resultsId);
        }
      };
      Thread thread = new Thread(runnable);
      thread.start();

      thread.interrupt();
    });

    userInfo.child("level").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        resultsId = 0;
        getTopic.setText("You are subscribed  in: " + userLevel);
        resultsKeysRef = FirebaseDatabase.getInstance().getReference("Results").child(userLevel);
        Log.d(TAG + " user_topictest", userLevel);

        resultsKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG + " layout", String.valueOf(dataSnapshot));
            if (dataSnapshot.hasChildren()) {
              resultKeys.clear();
              for (DataSnapshot results : dataSnapshot.getChildren()) {
                Log.d(TAG + " results", String.valueOf(results.getKey()));

                Resultsobj.set_result_keys(results.getKey());
              }
              Runnable runnable2 = ()-> {
                Resultsobj.Show_Results(resultsId);
                Log.d(TAG + " thread2", "Thread2 is called");
              };
              Thread thread2 = new Thread(runnable2);
              thread2.start();
              thread2.interrupt();
              resultsLayout.setVisibility(View.VISIBLE);
            } else {
              Log.d(TAG + " layout", "GONE");
              resultsLayout.setVisibility(View.GONE);
            }
          }
          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
    Log.d(TAG + "user_topic", getTopic.getText().toString());
  }

  //show results



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.topic_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_item_levels:
        CreateTopicsDialog();
        break;

      case R.id.menu_item_check_topics:
        Intent intent = new Intent(MainActivity.this, PollsTopicActivity.class);
        intent.putExtra("poll_level", userLevel);
        if(userLevel!=null) {
          startActivity(intent);
          this.finish();//TODO:allagi
        }
        break;

      case R.id.menu_item_signout:
        mAuth.signOut();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userLevel);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        break;

    }
    return super.onOptionsItemSelected(item);
  }

  public void CreateTopicsDialog() {

    TopicsDialog = new Dialog(MainActivity.this);
    TopicsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    TopicsDialog.setContentView(R.layout.topics_dialog);

    TopicsDialog.show();

    studentBtn = (RadioButton) TopicsDialog.findViewById(R.id.student_radioBtn);
    juniorBtn = (RadioButton) TopicsDialog.findViewById(R.id.junior_radioBtn);
    seniorBtn = (RadioButton) TopicsDialog.findViewById(R.id.senior_radioBtn);

    switch (userLevel){
      case "Student":
        studentBtn.setChecked(true);
        break;
      case "Junior":
        juniorBtn.setChecked(true);
        break;
      case "Senior":
        seniorBtn.setChecked(true);
        break;
    }

    btnSubmitLevel = (Button) TopicsDialog.findViewById(R.id.submit_level);
    btnSubmitLevel.setOnClickListener((v)->TopicsDialog.dismiss());
  }

  public void onTopicsSelected(View view) {
    boolean checked = ((RadioButton) view).isChecked();
    // Check which checkbox was clicked
    switch (view.getId()) {
      case R.id.student_radioBtn://TODO:na ftiaxo function
        if (checked) {
          userInfo.child("level").setValue(studentBtn.getText());
          userLevel = studentBtn.getText().toString();
          resultsPieChart.notifyDataSetChanged();
          resultsPieChart.invalidate();
          studentBtn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(studentBtn.getText().toString());
          juniorBtn.setChecked(false);
          seniorBtn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(juniorBtn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(seniorBtn.getText().toString());
        }else {
          FirebaseMessaging.getInstance().unsubscribeFromTopic(studentBtn.getText().toString());

        }
        break;
      case R.id.junior_radioBtn:
        if (checked) {
          userInfo.child("level").setValue(juniorBtn.getText());
          userLevel = juniorBtn.getText().toString();
          resultsPieChart.notifyDataSetChanged();
          resultsPieChart.invalidate();
          juniorBtn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(juniorBtn.getText().toString());
          studentBtn.setChecked(false);
          seniorBtn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(seniorBtn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(studentBtn.getText().toString());
        }else {
          FirebaseMessaging.getInstance().unsubscribeFromTopic(juniorBtn.getText().toString());

        }
        break;
      case R.id.senior_radioBtn:
        if (checked) {
          userInfo.child("level").setValue(seniorBtn.getText());
          userLevel = seniorBtn.getText().toString();
          resultsPieChart.notifyDataSetChanged();
          resultsPieChart.invalidate();
          seniorBtn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(seniorBtn.getText().toString());
          juniorBtn.setChecked(false);
          studentBtn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(juniorBtn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(studentBtn.getText().toString());
        }else {
          FirebaseMessaging.getInstance().unsubscribeFromTopic(seniorBtn.getText().toString());

        }
        break;
    }
  }

//  public void putInPreferences(boolean isChecked, String Booleankey) {
//    SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
//    SharedPreferences.Editor editor = sharedPreferences.edit();
//    editor.putBoolean(Booleankey, isChecked);
//    editor.apply();
//  }
//
//  public boolean getBooleanFromPreferences(String key) {
//    SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
//    Boolean isChecked = sharedPreferences.getBoolean(key, false);
//    return isChecked;
//  }

//    public String getStringFromPreferences(String key){
//        SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
//        String getValue = sharedPreferences.getString(key,null);
//        return getValue;
//    }
//        web_test = (WebView) findViewById(R.id.test);
//        web_test.loadUrl("http://10.0.2.2:1337/check_polls");
//        web_test.getSettings().setJavaScriptEnabled(true);
//        web_test.setWebViewClient(new WebViewClient());


}
