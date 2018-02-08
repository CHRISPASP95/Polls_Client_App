package com.example.christospaspalieris.polls_client_app;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
  FirebaseAuth mAuth;
  DatabaseReference user_info, results_keys_ref;
  Dialog Topics_Dialog;

  TextView no_poll;
  TextView get_Topic;

  CheckBox weather_btn, politics_btn, sports_btn;
  LinearLayout resultsLayout;

  Button btn_submit_topic, btn_next;

  String user_topic = "", user_id = "";
  String sex = "";
  String age_group = "";

  String title_result = "", id = "";
  int results_id = 0;

  PieChart results_PieChart;
  PieDataSet set;
  PieData data;
  List<PieEntry> users_results;
  List<String> result_keys;


  @Override
  public void onBackPressed() {
    // super.onBackPressed();
  }

  public String getTitleQuestion(String title) {
    return title;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(TAG, "onCreate");

    Bundle bundle = getIntent().getExtras();
    if (bundle != null)
    {
      user_topic = bundle.getString("User_Topic");
      results_keys_ref = FirebaseDatabase.getInstance().getReference("Results").child(user_topic);
    }


    result_keys = new ArrayList<>();
    resultsLayout = (LinearLayout) findViewById(R.id.results_layout);

    mAuth = FirebaseAuth.getInstance();
    user_id = mAuth.getCurrentUser().getUid();
    get_Topic = (TextView) findViewById(R.id.display_topic);
    no_poll = (TextView) findViewById(R.id.no_poll_available);
    btn_next = (Button) findViewById(R.id.btn_next);
    no_poll.setText("No Polls Available");

    users_results = new ArrayList<>();

    results_PieChart = (PieChart) findViewById(R.id.pie_graph_results);

    user_info = FirebaseDatabase.getInstance().getReference("USERS").child(user_id);

    user_info.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
          Log.d(TAG, String.valueOf(dataSnapshot1.getValue()));
          Log.d(TAG, String.valueOf(dataSnapshot1.getKey()));
          if (dataSnapshot1.getKey().equals("topic")) {
            user_topic = String.valueOf(dataSnapshot1.getValue());
            get_Topic.setText("You are subscribed  in: " + user_topic);
            Log.d(TAG + " user_topic", user_topic);

          }

          if (dataSnapshot1.getValue().equals(user_topic))
            no_poll.setVisibility(View.GONE);

          if (dataSnapshot1.getKey().equals("sex")) {
            sex = String.valueOf(dataSnapshot1.getValue());
            Log.d(TAG + " sex", sex);
          }
          if (dataSnapshot1.getKey().equals("age_group")) {
            age_group = String.valueOf(dataSnapshot1.getValue());
            Log.d(TAG + " age_group", age_group);
          }


        }
        FirebaseMessaging.getInstance().subscribeToTopic(user_topic);
        FirebaseMessaging.getInstance().subscribeToTopic(sex);
        FirebaseMessaging.getInstance().subscribeToTopic(age_group);


      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    btn_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Runnable runnable = new Runnable() {
          @Override
          public void run() {

            synchronized (this) {

              if (!user_topic.isEmpty()) {
                results_id++;
                if (results_id >= result_keys.size()) {
                  Log.d(TAG + "results_id", String.valueOf(results_id));
                  results_id = 0;
                }
                Log.d(TAG + " thread1", "Thread1 is called");
                Show_Results(results_id);
              }
            }
          }
        };

        Thread thread = new Thread(runnable);
        thread.start();

      }
    });


    user_info.child("topic").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        results_id = 0;
        get_Topic.setText("You are subscribed  in: " + user_topic);
        results_keys_ref = FirebaseDatabase.getInstance().getReference("Results").child(user_topic);
        Log.d(TAG + " user_topictest", user_topic);

        results_keys_ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG + " layout", String.valueOf(dataSnapshot));
            if (dataSnapshot.hasChildren()) {
              Log.d(TAG + " layout", "VISIBLE");
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

        if (!user_topic.isEmpty()) {
          results_keys_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              result_keys.clear();
              for (DataSnapshot results : dataSnapshot.getChildren()) {
                Log.d(TAG + " results", String.valueOf(results.getKey()));
                set_result_keys(results.getKey());
              }

              Runnable runnable2 = new Runnable() {

                @Override
                public void run() {
                  synchronized (this) {
                    Show_Results(results_id);
                    Log.d(TAG + " thread2", "Thread2 is called");
                  }

                }
              };

              Thread thread2 = new Thread(runnable2);
              thread2.start();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
          });
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    Log.d(TAG + "user_topic", get_Topic.getText().toString());



  }


  public void Show_Results(int results_id) {

    id = get_results_key(results_id);
    Log.d(TAG, String.valueOf(results_id));

    if (result_keys.size() > 0) {
      id = get_results_key(results_id);
      Log.d(TAG + " out result_keys", String.valueOf(id));
      results_keys_ref.child(id).child("Question").child("question").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          title_result = getTitleQuestion(String.valueOf(dataSnapshot.getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
      results_keys_ref.child(id).child("Question").child("answers").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot choicesValue) {

          Log.d(TAG + " choicesValue", String.valueOf(choicesValue.getRef()));
          users_results.clear();
          for (DataSnapshot each_choice : choicesValue.getChildren()) {
            Log.d(TAG + " getChildren", String.valueOf(choicesValue.getChildrenCount()));
            if (each_choice.getKey().equals("Choice1")) {
              String[] choice_parts = String.valueOf(each_choice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              users_results.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));
              Log.d(TAG + " choice_parts0", choice_parts[0]);
              Log.d(TAG + " choice_parts1", choice_parts[1]);
              Log.d(TAG + " Choice1", String.valueOf(each_choice.getValue()));

            } else if (each_choice.getKey().equals("Choice2")) {
              String[] choice_parts = String.valueOf(each_choice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              users_results.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));
              Log.d(TAG + " Choice2", String.valueOf(each_choice.getValue()));
            } else if (each_choice.getKey().equals("Choice3")) {
              String[] choice_parts = String.valueOf(each_choice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              users_results.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));
              Log.d(TAG + " Choice3", String.valueOf(each_choice.getValue()));
            }
          }


          set = new PieDataSet(users_results, "");
          set.setColors(ColorTemplate.MATERIAL_COLORS);

          Description description = new Description();
          description.setText(title_result);
          description.setTextSize(15f);

          Log.d(TAG + " size", String.valueOf(users_results.size()));
          Log.d(TAG + " id", id);
          Log.d(TAG + " set", set.toString());
          results_PieChart.clear();

          data = new PieData(set);
          results_PieChart.setData(data);

          results_PieChart.setDescription(description);
          results_PieChart.setDrawHoleEnabled(false);
//                    results_PieChart.notifyDataSetChanged();
//                    results_PieChart.invalidate();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
  }

  public void set_result_keys(String key) {
    result_keys.add(key);
    id = get_results_key(0);
    Log.d(TAG + " result_keys", String.valueOf(result_keys.size()));
    Log.d(TAG + " result_keys", String.valueOf(id));
  }

  public String get_results_key(int index) {
    Log.d(TAG + " get_results_key", String.valueOf(id));
    if ((index >= 0 && index < result_keys.size()))
      return result_keys.get(index);
    return "";
  }


  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "PAUSE");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.topic_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_item_topics:
        CreateTopicsDialog();
        break;

      case R.id.menu_item_check_topics:
        Intent intent = new Intent(MainActivity.this, PollsTopicActivity.class);
        intent.putExtra("poll_topic", user_topic);
        startActivity(intent);
        break;

      case R.id.menu_item_signout:
        mAuth.signOut();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Weather");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Politics");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Sports");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        break;

    }
    return super.onOptionsItemSelected(item);
  }

  public void CreateTopicsDialog() {
    Topics_Dialog = new Dialog(MainActivity.this);
    Topics_Dialog.setContentView(R.layout.topics_dialog);
    Topics_Dialog.show();

    weather_btn = (CheckBox) Topics_Dialog.findViewById(R.id.weather_checkBox);
    politics_btn = (CheckBox) Topics_Dialog.findViewById(R.id.politics_checkBox);
    sports_btn = (CheckBox) Topics_Dialog.findViewById(R.id.sports_checkBox);

    boolean isChecked_weather = getBooleanFromPreferences("isChecked_weather");
    boolean isChecked_politics = getBooleanFromPreferences("isChecked_politics");
    boolean isChecked_sports = getBooleanFromPreferences("isChecked_sports");

    weather_btn.setChecked(isChecked_weather);
    politics_btn.setChecked(isChecked_politics);
    sports_btn.setChecked(isChecked_sports);

    weather_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG + " onCheckedweather_btn", String.valueOf(isChecked));
        MainActivity.this.putInPreferences(isChecked, String.valueOf(weather_btn.getText()), "isChecked_weather", "weather");
        Topics_Dialog.dismiss();
      }
    });
    politics_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG + " onCheckedpolitics_btn", String.valueOf(isChecked));
        MainActivity.this.putInPreferences(isChecked, String.valueOf(politics_btn.getText()), "isChecked_politics", "politics");
        Topics_Dialog.dismiss();
      }
    });
    sports_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG + " onCheckedsports_btn", String.valueOf(isChecked));
        MainActivity.this.putInPreferences(isChecked, String.valueOf(sports_btn.getText()), "isChecked_sports", "sports");
        Topics_Dialog.dismiss();
      }
    });

    btn_submit_topic = (Button) Topics_Dialog.findViewById(R.id.submit_topic);
    btn_submit_topic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Topics_Dialog.dismiss();
      }
    });

  }

  public void onTopicsSelected(View view) {
    boolean checked = ((CheckBox) view).isChecked();
    // Check which checkbox was clicked
    switch (view.getId()) {
      case R.id.weather_checkBox:
        if (checked) {
          user_info.child("topic").setValue(weather_btn.getText());
          user_topic = weather_btn.getText().toString();
          results_PieChart.notifyDataSetChanged();
          results_PieChart.invalidate();
          weather_btn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(weather_btn.getText().toString());
          politics_btn.setChecked(false);
          sports_btn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(politics_btn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(sports_btn.getText().toString());
        }
        break;
      case R.id.politics_checkBox:

        if (checked) {
          user_info.child("topic").setValue(politics_btn.getText());
          user_topic = politics_btn.getText().toString();
          results_PieChart.notifyDataSetChanged();
          results_PieChart.invalidate();
          politics_btn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(politics_btn.getText().toString());
          weather_btn.setChecked(false);
          sports_btn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(sports_btn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(weather_btn.getText().toString());
        }
        break;
      case R.id.sports_checkBox:

        if (checked) {
          user_info.child("topic").setValue(sports_btn.getText());
          user_topic = sports_btn.getText().toString();
          results_PieChart.notifyDataSetChanged();
          results_PieChart.invalidate();
          sports_btn.setChecked(true);
          FirebaseMessaging.getInstance().subscribeToTopic(sports_btn.getText().toString());
          politics_btn.setChecked(false);
          weather_btn.setChecked(false);
          FirebaseMessaging.getInstance().unsubscribeFromTopic(politics_btn.getText().toString());
          FirebaseMessaging.getInstance().unsubscribeFromTopic(weather_btn.getText().toString());
        }
        break;
    }
  }

  public void putInPreferences(boolean isChecked, String CheckedValue, String Booleankey, String Stringkey) {
    SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(Booleankey, isChecked);
    editor.putString(Stringkey, CheckedValue);
    editor.apply();
  }

  public boolean getBooleanFromPreferences(String key) {
    SharedPreferences sharedPreferences = this.getPreferences(Activity.MODE_PRIVATE);
    Boolean isChecked = sharedPreferences.getBoolean(key, false);
    return isChecked;
  }

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
