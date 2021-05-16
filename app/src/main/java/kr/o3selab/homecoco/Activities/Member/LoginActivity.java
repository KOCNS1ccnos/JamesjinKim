package kr.o3selab.homecoco.Activities.Member;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.R;
import kr.o3selab.homecoco.Services.FirebaseDatabaseService;
import kr.o3selab.homecoco.Utils.HideKeyboard;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_form_email)
    View emailLayout;
    @Bind(R.id.login_form_password)
    View passwordLayout;

    @Bind(R.id.login_form_email_error)
    TextView emailError;
    @Bind(R.id.login_form_password_error)
    TextView passwordError;

    @Bind(R.id.login_form_email_login_button)
    View emailLoginButton;
    @Bind(R.id.login_form_google_button)
    View googleLoginButton;
    @Bind(R.id.login_form_facebook_button)
    View facebookLoginButton;
    @Bind(R.id.login_form_register_button)
    View registerButton;

    @Bind(R.id.login_form_email_field)
    EditText emailField;
    @Bind(R.id.login_form_password_field)
    EditText passwordField;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 로그인 로딩 창
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("로그인 중 입니다.");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        HideKeyboard.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) this.finish();
    }

    @OnClick(R.id.login_form_email_login_button)
    void emailLoginOnClick() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.length() == 0) {
            showWarningAlertDialog("이메일 주소를 입력해주세요!");
            return;
        } else if (!email.matches(Constants.emailRegex)) {
            showWarningAlertDialog("정확한 이메일 주소를 입력해주세요!");
            return;
        } else if (passwordField.length() == 0) {
            showWarningAlertDialog("패스워드를 입력해주세요!");
            return;
        } else if (passwordField.length() < 6) {
            showWarningAlertDialog("패스워드를 6자 이상 입력해주세요!");
            return;
        }

        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompleteListener);

    }

    @OnClick(R.id.login_form_register_button)
    void registerButtonOnClick() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnFocusChange(R.id.login_form_email_field)
    void emailOnFocusChange(boolean hasFocus) {
        String email = emailField.getText().toString();
        if (!email.matches(Constants.emailRegex))
            emailLayout.setBackgroundResource(R.drawable.login_form_error);
        else
            emailLayout.setBackgroundResource(R.drawable.login_form_ok);


        if (!hasFocus) {
            if (!email.matches(Constants.emailRegex))
                emailError.setVisibility(View.VISIBLE);
            else
                emailError.setVisibility(View.GONE);
        }
    }

    @OnFocusChange(R.id.login_form_password_field)
    void passwordOnFocusChange(boolean hasFocus) {
        if (passwordField.getText().length() < 6)
            passwordLayout.setBackgroundResource(R.drawable.login_form_error);
        else
            passwordLayout.setBackgroundResource(R.drawable.login_form_ok);

        if (!hasFocus) {
            if (passwordField.getText().length() < 6)
                passwordError.setVisibility(View.VISIBLE);
            else
                passwordError.setVisibility(View.GONE);
        }
    }

    void showWarningAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .setCancelable(true)
                .show();
    }

    OnCompleteListener<AuthResult> mOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull final Task<AuthResult> task) {
            mProgressDialog.dismiss();

            if (!task.isSuccessful()) {
                String errorClass = task.getException().getClass().getName().replace("com.google.firebase.auth.", "");
                Constants.printLog("LoginFailed:" + errorClass, false);

                switch (errorClass) {
                    case "FirebaseAuthInvalidUserException":
                        Toast.makeText(LoginActivity.this, "존재하지 않는 사용자 입니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "FirebaseAuthInvalidCredentialsException":
                        Toast.makeText(LoginActivity.this, "패스워드가 틀립니다.", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 사유 : " + errorClass, Toast.LENGTH_LONG).show();
                        break;
                }
            } else {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> adminList = (List<String>) dataSnapshot.getValue();
                        if (adminList.contains(task.getResult().getUser().getEmail())) {
                            Constants.isAdmin = true;

                            Intent intent = new Intent(LoginActivity.this, FirebaseDatabaseService.class);
                            startService(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                LoginActivity.this.finish();
            }
        }
    };


}
