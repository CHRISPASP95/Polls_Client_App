package com.example.christospaspalieris.polls_client_app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by Christos Paspalieris on 12/22/2017.
 */

public class PollHandler {

    DatabaseReference user_keys;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String title;
    Map<String,Object> keys;
    Map<String,Object> AnsweredKeys;

    public PollHandler() {
    }

    public PollHandler(String title) {
        user_keys = FirebaseDatabase.getInstance().getReference("USERS");
        this.title = title;
    }

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
        user_keys.child(firebaseUser.getUid()).child(title).child("UnAnswered").updateChildren(keys);
    }

    public void setAnsweredKeys(Map<String,Object> answeredKeys){
        AnsweredKeys = answeredKeys;
        user_keys.child(firebaseUser.getUid()).child(title).child("Answered").updateChildren(AnsweredKeys);
    }

}
