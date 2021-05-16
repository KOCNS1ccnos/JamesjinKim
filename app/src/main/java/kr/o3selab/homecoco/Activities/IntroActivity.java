package kr.o3selab.homecoco.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.intro_back_button)
    void introOnClick() {
        this.finish();
    }
}
