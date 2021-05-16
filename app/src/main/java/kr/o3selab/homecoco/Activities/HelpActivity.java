package kr.o3selab.homecoco.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.help_back_button)
    void backOnClick() {
        this.finish();
    }
}
