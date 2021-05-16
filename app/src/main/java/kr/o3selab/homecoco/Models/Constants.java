package kr.o3selab.homecoco.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {

    // 어플정보
    public static Double appVersion = 1.3;

    // 개인정보
    public static String deviceID;
    public static String uEmail;
    public static String uToken;
    public static boolean isAdmin = false;

    // 설정정보
    public static String MY_SHARED_PREF = "my_shared";
    public static String SHARED_APP_ID = "app_id";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    // 픽셀변환
    public static int DpToPixel(Context context, int DP) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
        return (int) px;
    }

    // 주소 리스트
    public static String[] address1 = {"천안시 동남구", "천안시 서북구", "아산시"};


    // 정규식
    public static String emailRegex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    public static String phoneRegex = "^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$";


    // 에러 리포팅
    private static StringBuilder logs = new StringBuilder();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    public static void printLog(String message, boolean isSend) {
        String result = "HomeCoCo" + "(" + sdf.format(new Date(System.currentTimeMillis())) + ") : " + message;
        Log.d("HomeCoCo", result);
        logs.append(result).append("\n");
        if (isSend) sendReport();
    }

    public static void printLog(String message) {
        printLog(message, false);
    }

    public static void printLog(Exception e) {
        printLog(e.getMessage(), true);
    }

    private static void sendReport() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("eLogs").push();
        myRef.child("uEmail").setValue(uEmail);
        myRef.child("uToken").setValue(uToken);
        myRef.child("DeviceID").setValue(deviceID);
        myRef.child("MODEL").setValue(Build.MODEL);
        myRef.child("MANUFACTURER").setValue(Build.MANUFACTURER);
        myRef.child("Android Version").setValue(Build.VERSION.RELEASE);
        myRef.child("Logs").setValue(logs.toString());
    }
}
