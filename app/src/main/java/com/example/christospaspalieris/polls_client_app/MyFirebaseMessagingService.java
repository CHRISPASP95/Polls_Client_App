package com.example.christospaspalieris.polls_client_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Christos Paspalieris on 21-Oct-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static String TAG = "MyMessagingService";

    private String[] vars;
    static String  Poll_Topic, get_collapse;


    private Map<String,Object> polls_keys = new HashMap<>();


    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            String click_action = remoteMessage.getData().get("click_action");
            get_collapse = remoteMessage.getCollapseKey();
            GetCollpseId(get_collapse);
            Log.d(TAG, "Message action: " + click_action);

            String Poll_keys;

            JSONObject data = new JSONObject(remoteMessage.getData());
            try {
                Poll_Topic = data.getString("poll_topic");
                Poll_keys = data.getString("poll_key");
                vars = Poll_keys.split(",");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < vars.length; i++){
                polls_keys.put(vars[i],vars[i]);
            }

            PollHandler pollHandler = new PollHandler(Poll_Topic);
            pollHandler.setKeys(polls_keys);

            notifyUser(title, message, click_action);
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();
//            String click_action = remoteMessage.getNotification().getClickAction();
//            get_collapse = remoteMessage.getCollapseKey();
//            Log.d(TAG, "Message action: " + click_action);
//
//
//
//            notifyUser(title, message, click_action);
//        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }

    public static int GetTopicIcon(){

        if(Poll_Topic.equals("Weather"))
            return R.mipmap.cloud;
        else if(Poll_Topic.equals("Politics"))
            return R.mipmap.politics;
        else if(Poll_Topic.equals("Sports"))
            return R.mipmap.sports;

        return R.mipmap.ic_launcher;
    }

    public static int GetCollpseId(String get_collapse){
        if(get_collapse.equals("Weather"))
            return 234;
        else if(get_collapse.equals("Politics"))
            return 235;
        else if(get_collapse.equals("Sports"))
            return 236;

        return 0;
    }

    public void notifyUser(String title, String notification, String click_action){

        Intent intent;
        Bundle mBundle = new Bundle();
        if(click_action.equals("PollsTopicActivity")){

            intent = new Intent(getApplicationContext(),PollsTopicActivity.class);
            //intent.putExtra("poll_key",vars);
           // intent.putExtra("poll_topic",Poll_Topic);
            mBundle.putString("poll_topic",Poll_Topic);
            mBundle.putStringArray("poll_key",vars);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(mBundle);




            Log.d(TAG,"Poll_Topic " + Poll_Topic);

            for(int i = 0 ; i < vars.length; i++){
                Log.d(TAG,"Poll_Keys " + vars[i]);
            }
        }else {
            Log.d(TAG,"IM MAIN");
            intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(title, notification,intent);
    }


}
