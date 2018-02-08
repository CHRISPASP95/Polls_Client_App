package com.example.christospaspalieris.polls_client_app;

/**
 * Created by Christos Paspalieris on 20-Oct-17.
 */

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static String TAG = "Service";
    public static final String TOKEN_BROADCAST = "tokenbroadcast";

    FirebaseDatabase database = FirebaseDatabase.getInstance();// my Firebase Instance
    DatabaseReference myRef = database.getReference("Tokens"); // my Reference to this Instance

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        getApplicationContext().sendBroadcast(new Intent(TOKEN_BROADCAST));

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        //storeToken(refreshedToken);

    }

    private void sendRegistrationToServer(String refreshedToken) {
        myRef.push().setValue(refreshedToken);
    }

//    private void storeToken(String token){
//        SharedPrefManager.getInstance(getApplicationContext()).setToken(token);
//    }
}
