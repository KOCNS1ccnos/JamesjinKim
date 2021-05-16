package kr.o3selab.homecoco.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Activities.Member.LoginActivity;
import kr.o3selab.homecoco.Activities.Member.RegisterActivity;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.Models.Extras;
import kr.o3selab.homecoco.Models.Service;
import kr.o3selab.homecoco.R;
import kr.o3selab.homecoco.Services.FirebaseDatabaseService;
import me.grantland.widget.AutofitTextView;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.activity_main)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.left_drawer)
    FrameLayout mDrawerMenu;
    @Bind(R.id.main_menu_button)
    ImageView mMenuButton;

    @Bind(R.id.menu_login_status)
    FrameLayout mMemberStatus;
    @Bind(R.id.menu_member_status)
    LinearLayout mMemberButtonStatus;

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mIntent = new Intent(this, ServiceActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialization();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) mDrawerLayout.closeDrawer(mDrawerMenu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initialization() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            mMemberStatus.removeAllViews();

            View view = getLayoutInflater().inflate(R.layout.menu_status_login, null);
            AutofitTextView nameView = (AutofitTextView) view.findViewById(R.id.menu_login_name_label);
            AutofitTextView emailView = (AutofitTextView) view.findViewById(R.id.menu_login_email_label);

            nameView.setText(user.getDisplayName() + getString(R.string.main_menu_welcome_user));
            emailView.setText(user.getEmail());

            mMemberStatus.addView(view);

            mMemberButtonStatus.removeAllViews();

            View logoutView = getLayoutInflater().inflate(R.layout.menu_logout_button, null);
            logoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.main_menu_logout_alert);
                    builder.setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Constants.isAdmin) {
                                Constants.isAdmin = false;
                                Intent intent = new Intent(MainActivity.this, FirebaseDatabaseService.class);
                                stopService(intent);
                            }

                            FirebaseAuth.getInstance().signOut();
                            initialization();
                        }
                    });

                    builder.setNegativeButton(R.string.default_cancel, null);
                    builder.setCancelable(false);
                    builder.show();
                }
            });

            mMemberButtonStatus.addView(logoutView);

        } else {
            mMemberStatus.removeAllViews();
            mMemberStatus.addView(getLayoutInflater().inflate(R.layout.menu_status_logout, null));

            mMemberButtonStatus.removeAllViews();

            View loginView = getLayoutInflater().inflate(R.layout.menu_login_button, null);
            loginView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
            mMemberButtonStatus.addView(loginView);

            View registerView = getLayoutInflater().inflate(R.layout.menu_register_button, null);
            registerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                }
            });
            mMemberButtonStatus.addView(registerView);
        }
    }

    @OnClick(R.id.main_menu_button)
    void menuOnClick() {
        mDrawerLayout.openDrawer(mDrawerMenu);
    }

    @OnClick(R.id.main_menu_call_button)
    void menuCallOnClick() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:041-558-0880")));
    }

    @OnClick(R.id.main_menu_icon_01)
    void main01MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.HOME_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.main_menu_icon_02)
    void main02MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.REPAIR_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.main_menu_icon_03)
    void main03MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.INTERIOR_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.main_menu_icon_04)
    void main04MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.QUESTION_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.main_menu_icon_05)
    void main05MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.SOLUTION_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.main_menu_icon_06)
    void main06MenuOnClick() {
        mIntent.putExtra(Extras.SERVICE_NAME, Service.EBF_SERVICE);
        startActivity(mIntent);
    }

    @OnClick(R.id.menu_order_button)
    void menuOrderOnClick() {
        startActivity(new Intent(MainActivity.this, OrderActivity.class));
    }

    @OnClick(R.id.menu_intro_button)
    void menuIntroOnClick() {
        startActivity(new Intent(MainActivity.this, IntroActivity.class));
    }

    @OnClick(R.id.menu_help_button)
    void menuHelpOnClick() {
        startActivity(new Intent(MainActivity.this, HelpActivity.class));
    }

    @OnClick(R.id.menu_event_button)
    void menuEventOnClick() {
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        intent.putExtra("URL", "http://www.homecoco.com");
        startActivity(intent);
    }

    @OnClick(R.id.menu_calling_button)
    void menuCallingOnClick() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:041-558-0880")));
    }

    @OnClick(R.id.main_top_image)
    void menuImageOnClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.homecoco.com")));
    }

    /*@OnClick(R.id.menu_kakao_button)
    void menuKakaoOnClick() {

    }*/
}
