package com.example.christospaspalieris.polls_client_app;

import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Results {
  private List<String> resultKeys;
  private String id, titleResult;
  private DatabaseReference resultsKeysRef;
  private List<PieEntry> usersResults;
  private PieDataSet set;
  private PieData data;
  private PieChart resultsPieChart;

  public Results(List<String> ResultKeys, String id, DatabaseReference resultsKeysRef, List<PieEntry> usersResults, PieChart resultsPieChart) {
    this.resultKeys = ResultKeys;
    this.id = id;
    this.resultsKeysRef = resultsKeysRef;
    this.usersResults = usersResults;
    this.resultsPieChart = resultsPieChart;
  }

  public void set_result_keys(String key) {
    resultKeys.add(key);
    id = get_results_key(0);
  }

  public String get_results_key(int index) {
    if ((index >= 0 && index < resultKeys.size()))
      return resultKeys.get(index);
    return "";
  }

  public String getTitleQuestion(String title) {
    return title;
  }

  public void Show_Results(int results_id) {
    id = get_results_key(results_id);
    if (resultKeys.size() > 0) {
      id = get_results_key(results_id);
      resultsKeysRef.child(id).child("Question").child("question").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          titleResult = getTitleQuestion(String.valueOf(dataSnapshot.getValue()));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
      resultsKeysRef.child(id).child("Question").child("answers").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot choicesValue) {
          usersResults.clear();
          for (DataSnapshot eachChoice : choicesValue.getChildren()) {

            if (eachChoice.getKey().equals("Choice1")) {
              String[] choice_parts = String.valueOf(eachChoice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              usersResults.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));

            } else if (eachChoice.getKey().equals("Choice2")) {
              String[] choice_parts = String.valueOf(eachChoice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              usersResults.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));

            } else if (eachChoice.getKey().equals("Choice3")) {
              String[] choice_parts = String.valueOf(eachChoice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              usersResults.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));

            }else if (eachChoice.getKey().equals("Choice4")) {
              String[] choice_parts = String.valueOf(eachChoice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              usersResults.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));

            }else if (eachChoice.getKey().equals("Choice5")) {
              String[] choice_parts = String.valueOf(eachChoice.getValue()).split("=");
              choice_parts[0] = choice_parts[0].substring(1, choice_parts[0].length()); //key
              choice_parts[1] = choice_parts[1].substring(0, choice_parts[1].length() - 1); //counter

              usersResults.add(new PieEntry(Float.parseFloat(choice_parts[1]), choice_parts[0]));
            }
          }

          set = new PieDataSet(usersResults, "");
          set.setColors(ColorTemplate.COLORFUL_COLORS);
          set.setValueTextSize(15f);
          resultsPieChart.clear();
          data = new PieData(set);
          resultsPieChart.setData(data);
          resultsPieChart.animateY(1000);
          resultsPieChart.invalidate();
          resultsPieChart.setDrawHoleEnabled(true);
          resultsPieChart.setUsePercentValues(true);


          Description description = new Description();
          description.setText(titleResult);
          description.setTextSize(15f);
          resultsPieChart.setDescription(description);
          description.setPosition(resultsPieChart.getRadius()*2, resultsPieChart.getRadius()*2 + 100);
          resultsPieChart.setDrawHoleEnabled(false);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
  }
}
