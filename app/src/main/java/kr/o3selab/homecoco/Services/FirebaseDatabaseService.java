package kr.o3selab.homecoco.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArraySet;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.Set;

import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.R;

public class FirebaseDatabaseService extends Service {

    private DatabaseReference HomeCleaning;
    private DatabaseReference RepairService;
    private DatabaseReference InteriorService;
    private DatabaseReference QuestionService;
    private DatabaseReference SolutionService;
    private DatabaseReference EBFService;

    private ChildListener HomeListener;
    private ChildListener RepairListener;
    private ChildListener InteriorListener;
    private ChildListener QuestionListener;
    private ChildListener SolutionListener;
    private ChildListener EBFListener;

    private Set<String> mSet;

    @Override
    public void onCreate() {
        super.onCreate();

        Constants.printLog("Service onCreate");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSet = sharedPreferences.getStringSet("order", null);
        if (mSet == null) {
            mSet = new ArraySet<>();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("order", mSet);
            editor.commit();
        }

        HomeCleaning = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.HOME_SERVICE);
        RepairService = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.REPAIR_SERVICE);
        InteriorService = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.INTERIOR_SERVICE);
        QuestionService = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.QUESTION_SERVICE);
        SolutionService = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.SOLUTION_SERVICE);
        EBFService = FirebaseDatabase.getInstance().getReference(kr.o3selab.homecoco.Models.Service.EBF_SERVICE);

        HomeListener = new ChildListener(getResources().getString(R.string.cleaning_title));
        RepairListener = new ChildListener(getResources().getString(R.string.repair_title));
        InteriorListener = new ChildListener(getResources().getString(R.string.interior_title));
        QuestionListener = new ChildListener(getResources().getString(R.string.question_title));
        SolutionListener = new ChildListener(getResources().getString(R.string.solution_title));
        EBFListener = new ChildListener(getResources().getString(R.string.ebf_title));

        HomeCleaning.addChildEventListener(HomeListener);
        RepairService.addChildEventListener(RepairListener);
        InteriorService.addChildEventListener(InteriorListener);
        QuestionService.addChildEventListener(QuestionListener);
        SolutionService.addChildEventListener(SolutionListener);
        EBFService.addChildEventListener(EBFListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Constants.printLog("Service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Constants.printLog("Service onDestroy");
        HomeCleaning.removeEventListener(HomeListener);
        RepairService.removeEventListener(RepairListener);
        InteriorService.removeEventListener(InteriorListener);
        QuestionService.removeEventListener(QuestionListener);
        SolutionService.removeEventListener(SolutionListener);
        EBFService.removeEventListener(EBFListener);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Constants.printLog("remove task");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.clear();
        editor.putStringSet("order", mSet);
        editor.commit();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ChildListener implements ChildEventListener {

        String mTag;

        public ChildListener(String tag) {
            this.mTag = tag;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Constants.printLog("onChildAdded");
            if (dataSnapshot == null) return;

            String key = dataSnapshot.getKey();

            if (!mSet.contains(dataSnapshot.getKey())) {
                mSet.add(key);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.clear();
                editor.putStringSet("order", mSet);
                editor.commit();

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseDatabaseService.this)
                        .setContentTitle("주문접수")
                        .setContentText(mTag + " 카테고리에 주문이 접수 되었습니다.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(soundUri);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(new Random().nextInt(), builder.build());

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

}
