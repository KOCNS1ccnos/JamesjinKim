package kr.o3selab.homecoco.Activities.Member;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.R;
import kr.o3selab.homecoco.Utils.HideKeyboard;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.register_form_email)
    View emailLayout;
    @Bind(R.id.register_form_email_field)
    EditText emailField;
    @Bind(R.id.register_form_email_error)
    TextView emailError;

    @Bind(R.id.register_form_name)
    View nameLayout;
    @Bind(R.id.register_form_name_field)
    EditText nameField;
    @Bind(R.id.register_form_name_error)
    TextView nameError;

    @Bind(R.id.register_form_pw)
    View pwLayout;
    @Bind(R.id.register_form_pw_field)
    EditText pwField;
    @Bind(R.id.register_form_pw_field_error)
    TextView pwError;

    @Bind(R.id.register_form_re_pw)
    View rePwLayout;
    @Bind(R.id.register_form_re_pw_field)
    EditText rePwField;
    @Bind(R.id.register_form_re_pw_field_error)
    TextView rePwError;

    FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    String mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        // 로그인 로딩 창
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("회원가입 중 입니다");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        HideKeyboard.init(this);
    }

    @OnClick(R.id.register_form_register_button)
    void registerOnClick() {

        String email = emailField.getText().toString();
        mDisplayName = nameField.getText().toString();
        String pw = pwField.getText().toString();

        if (!email.matches(Constants.emailRegex)) {
            showAlertError(getString(R.string.member_form_email_error));
            return;
        } else if (nameField.getText().length() == 0) {
            showAlertError(getString(R.string.member_form_name_error));
            return;
        } else if (pwField.getText().length() < 6) {
            showAlertError(getString(R.string.member_form_password_error));
            return;
        } else if (!pwField.getText().toString().equals(rePwField.getText().toString())) {
            showAlertError(getString(R.string.member_form_repw_error));
            return;
        }

        mProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(mOnCompleteListener);
    }

    @OnFocusChange(R.id.register_form_email_field)
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

    @OnFocusChange(R.id.register_form_name_field)
    void nameOnFocusChange(boolean hasFocus) {
        if (nameField.getText().length() == 0)
            nameLayout.setBackgroundResource(R.drawable.login_form_error);
        else
            nameLayout.setBackgroundResource(R.drawable.login_form_ok);

        if (!hasFocus) {
            if (nameField.getText().length() == 6)
                nameError.setVisibility(View.VISIBLE);
            else
                nameError.setVisibility(View.GONE);
        }
    }

    @OnFocusChange(R.id.register_form_pw_field)
    void pwOnFocusChange(boolean hasFocus) {
        if (pwField.getText().length() < 6)
            pwLayout.setBackgroundResource(R.drawable.login_form_error);
        else
            pwLayout.setBackgroundResource(R.drawable.login_form_ok);

        if (!hasFocus) {
            if (pwField.getText().length() < 6)
                pwError.setVisibility(View.VISIBLE);
            else
                pwError.setVisibility(View.GONE);
        }
    }

    @OnFocusChange(R.id.register_form_re_pw_field)
    void repwOnFocusChange(boolean hasFocus) {

        if (!pwField.getText().toString().equals(rePwField.getText().toString()))
            rePwLayout.setBackgroundResource(R.drawable.login_form_error);
        else
            rePwLayout.setBackgroundResource(R.drawable.login_form_ok);

        if (!hasFocus) {
            if (!pwField.getText().toString().equals(rePwField.getText().toString()))
                rePwError.setVisibility(View.VISIBLE);
            else
                rePwError.setVisibility(View.GONE);
        }
    }

    private OnCompleteListener<AuthResult> mOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                final FirebaseUser user = task.getResult().getUser();
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mDisplayName)
                        .build();
                user.updateProfile(userProfileChangeRequest)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mAuth.signOut();
                                mAuth.signInWithEmailAndPassword(emailField.getText().toString(), pwField.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, mDisplayName + "님 홈코코에 오신것을 환영합니다.", Toast.LENGTH_SHORT).show();
                                            RegisterActivity.this.finish();
                                        } else {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });


            } else {
                mProgressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showAlertError(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .setCancelable(true)
                .show();
    }

}
