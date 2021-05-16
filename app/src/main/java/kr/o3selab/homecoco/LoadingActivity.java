package kr.o3selab.homecoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.UUID;

import kr.o3selab.homecoco.Activities.MainActivity;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.Services.FirebaseDatabaseService;

public class LoadingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        SharedPreferences sharedPreferences = Constants.getSharedPreferences(this);
        String deviceID = sharedPreferences.getString(Constants.SHARED_APP_ID, "null");

        if (deviceID.equals("null")) {
            Constants.deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor editor = Constants.getEditor(this);
            editor.putString(Constants.SHARED_APP_ID, Constants.deviceID);
            editor.commit();
        } else {
            Constants.deviceID = deviceID;
        }

        sendTokenToServer();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("version");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Double version = dataSnapshot.getValue(Double.class);
                if(!version.equals(Constants.appVersion)) {
                    new AlertDialog.Builder(LoadingActivity.this)
                            .setMessage("새 버전이 출시되었습니다. 업데이트 후 이용해주세요.")
                            .setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    startActivity(intent);

                                    LoadingActivity.this.finish();
                                }
                            })
                            .setNegativeButton(R.string.default_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(LoadingActivity.this, "업데이트 후 이용해주세요. 프로그램을 종료합니다.", Toast.LENGTH_SHORT).show();
                                    LoadingActivity.this.finish();
                                }
                            })
                            .show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.addAuthStateListener(mAuthStateListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendTokenToServer() {
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Token");
        databaseReference.child(Constants.deviceID).setValue(token);
    }

    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            mAuth.removeAuthStateListener(this);

            final FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Constants.printLog("회원상태:로그인:아이디:" + user.getUid(), false);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> adminList = (List<String>) dataSnapshot.getValue();
                        if(adminList.contains(user.getEmail())) {
                            Constants.isAdmin = true;

                            Intent intent = new Intent(LoadingActivity.this, FirebaseDatabaseService.class);
                            startService(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                Constants.printLog("회원상태:로그아웃", false);
            }

            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            LoadingActivity.this.finish();
        }
    };
}
