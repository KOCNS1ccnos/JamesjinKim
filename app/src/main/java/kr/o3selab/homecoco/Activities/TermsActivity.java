package kr.o3selab.homecoco.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.R;
import kr.o3selab.homecoco.Terms.ViewPagerAdapter;

public class TermsActivity extends AppCompatActivity {

    @Bind(R.id.terms_pager)
    ViewPager viewPager;
    @Bind(R.id.terms_tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.terms_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        ButterKnife.bind(this);

        final String[] titles = {"이용약관", "개인정보 취급방침"};

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), titles, titles.length));
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        title.setText(titles[0]);
                        break;
                    case 1:
                        title.setText(titles[1]);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.terms_back_button)
    void backOnClick() {
        this.finish();
    }
}
