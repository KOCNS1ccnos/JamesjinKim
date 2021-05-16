package kr.o3selab.homecoco.Services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import kr.o3selab.homecoco.Models.Constants;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Constants.printLog("Refreshed Token: " + refreshedToken);
    }
}
