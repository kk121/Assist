package com.krishna.assistsample.firebase;

import com.google.firebase.iid.FirebaseInstanceId;

public class FCMInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }
}
