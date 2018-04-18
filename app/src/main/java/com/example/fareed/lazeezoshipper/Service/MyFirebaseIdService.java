package com.example.fareed.lazeezoshipper.Service;

import com.example.fareed.lazeezoshipper.Common.Common;
import com.example.fareed.lazeezoshipper.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by fareed on 18/04/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        if(Common.currentShipper!=null)
            updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {
        if(Common.currentShipper!=null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(refreshedToken, true);
            tokens.child(Common.currentShipper.getPhone()).setValue(data);
        }
    }
}
