package kr.o3selab.homecoco.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Models.Date;
import kr.o3selab.homecoco.Models.Extras;
import kr.o3selab.homecoco.R;

public class SelectTimeActivity extends AppCompatActivity {

    @Bind(R.id.select_time_picker)
    DatePicker mDatePicker;

    @Bind(R.id.search_date_time_text)
    TextView mTimeTextView;

    String[] mTimeList;
    int mSelectedTime;
    AlertDialog mTimeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        ButterKnife.bind(this);

        mDatePicker.setMinDate(System.currentTimeMillis());
        mDatePicker.setMaxDate(System.currentTimeMillis() + 2592000000L);

        mTimeList = createTimeList();
        mSelectedTime = 0;

        checkIntentData();
    }

    private void checkIntentData() {
        Intent intent = getIntent();
        String requestString = intent.getStringExtra(Extras.REQUEST_DATE_TIME);

        if (requestString.contains("선택해주세요")) {
            return;
        }

        Date date = Date.getDateWithTimeToString(requestString);
        mDatePicker.updateDate(date.getYear(), date.getMonth(), date.getDate());
        mTimeTextView.setText(date.getType());

        for (int i = 0; i < 3; i++) {
            if (mTimeList[i].equals(mTimeTextView.getText())) {
                mSelectedTime = i;
                return;
            }
        }
    }

    @OnClick(R.id.search_date_time)
    void timeOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(mTimeList, mSelectedTime, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedTime = which;
                mTimeTextView.setText(mTimeList[which]);
                mTimeDialog.dismiss();
            }
        });
        mTimeDialog = builder.create();
        mTimeDialog.show();
    }

    @OnClick(R.id.search_date_ok)
    void okOnClick() {
        Intent intent = new Intent();
        intent.putExtra(Extras.SELECTED_DATE_TIME, Date.getStringToLong(Date.getTimeToInt(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth())) + " " + mTimeList[mSelectedTime]);

        this.setResult(0, intent);
        this.finish();
    }

    public String[] createTimeList() {
        String[] times = new String[3];

        times[0] = "[오전] 오전 9시 ~ 오후 1시";
        times[1] = "[오후] 오후 2시 ~ 오후 6시";
        times[2] = "[종일] 오전 9시 ~ 오후 5시";

        return times;
    }

    @Override
    public void onBackPressed() {
        this.setResult(-1);
        super.onBackPressed();
    }
}
