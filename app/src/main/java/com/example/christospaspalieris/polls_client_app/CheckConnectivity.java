package com.example.christospaspalieris.polls_client_app;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;



/**
 * Created by Christos Paspalieris on 2/17/2018.
 */

public class CheckConnectivity {

  public boolean isConnected(Context context){

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

  public AlertDialog.Builder buildDialog(Context c, Activity activity){
    AlertDialog.Builder builder = new AlertDialog.Builder(c);
    builder.setTitle("No Internet Connection");
    builder.setMessage("You need to have Mobile Data or Wifi. Press ok to Exit");

    builder.setPositiveButton("Ok", (dialog, which) -> activity.finish());
    builder.setCancelable(false);
    return builder;
  }
}
