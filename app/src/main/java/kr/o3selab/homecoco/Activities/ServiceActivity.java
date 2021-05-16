package kr.o3selab.homecoco.Activities;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Models.Extras;
import kr.o3selab.homecoco.Models.Service;
import kr.o3selab.homecoco.R;
import me.grantland.widget.AutofitTextView;

public class ServiceActivity extends AppCompatActivity {

    @Bind(R.id.activity_service_title)
    TextView mTitleView;
    @Bind(R.id.activity_service_main_background)
    ImageView mMainBackgroundImageView;
    @Bind(R.id.activity_service_main_image)
    ImageView mMainImageView;
    @Bind(R.id.activity_service_main_content)
    AutofitTextView mMainContentView;

    private String mServiceName;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        ButterKnife.bind(this);

        mIntent = getIntent();
        mServiceName = mIntent.getStringExtra(Extras.SERVICE_NAME);

        if (mServiceName == null) {
            Toast.makeText(this, "서비스 이름이 수신되지 않았습니다. 다시 실행시켜 주세요", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        mIntent.setComponent(new ComponentName(this, ServiceDetailActivity.class));

        switch (mServiceName) {
            case Service.HOME_SERVICE:
                setContent(R.string.cleaning_title, R.drawable.s_visual_01_bg, R.drawable.s_visual_01, R.string.cleaning_introduce);
                setExtras(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
                break;
            case Service.REPAIR_SERVICE:
                setContent(R.string.repair_title, R.drawable.s_visual_02_bg, R.drawable.s_visual_02, R.string.repair_introduce);
                setExtras(View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE);
                break;
            case Service.INTERIOR_SERVICE:
                setContent(R.string.interior_title, R.drawable.s_visual_03_bg, R.drawable.s_visual_03, R.string.interior_introduce);
                setExtras(View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE);
                break;
            case Service.QUESTION_SERVICE:
                setContent(R.string.question_title, R.drawable.s_visual_04_bg, R.drawable.s_visual_04, R.string.question_introduce);
                setExtras(View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                break;
            case Service.SOLUTION_SERVICE:
                setContent(R.string.solution_title, R.drawable.s_visual_05_bg, R.drawable.s_visual_05, R.string.solution_introduce);
                setExtras(View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                break;
            case Service.EBF_SERVICE:
                setContent(R.string.ebf_title, R.drawable.s_visual_06_bg, R.drawable.s_visual_06, R.string.ebf_introduce);
                setExtras(View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE);
                break;
        }
    }

    private void setContent(int text, int main_back_image, int main_image, int content) {
        mTitleView.setText(text(text));
        mMainBackgroundImageView.setImageDrawable(image(main_back_image));
        mMainImageView.setImageDrawable(image(main_image));
        mMainContentView.setText(text(content));
    }

    private void setExtras(int picture, int request, int category,
                           int type, int reservation, int address, int detailedaddressed) {
        mIntent.putExtra(Extras.ORDER_PICTURE, picture);
        mIntent.putExtra(Extras.ORDER_REQUEST, request);
        mIntent.putExtra(Extras.ORDER_CATEGORY, category);
        mIntent.putExtra(Extras.ORDER_TYPE, type);
        mIntent.putExtra(Extras.ORDER_RESERVATION, reservation);
        mIntent.putExtra(Extras.ORDER_ADDRESS, address);
        mIntent.putExtra(Extras.ORDER_DETAILED_ADDRESS, detailedaddressed);
    }

    private Drawable image(int resource) {
        return ContextCompat.getDrawable(this, resource);
    }

    private String text(int resource) {
        return getResources().getString(resource);
    }

    @OnClick(R.id.activity_service_back_button)
    void onBackButtonClicked() {
        onBackPressed();
    }

    @OnClick(R.id.activity_service_go_detail)
    void onGoDetailClicked() {
        startActivity(mIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
