package kr.o3selab.homecoco.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.Models.OrderItem;
import kr.o3selab.homecoco.Models.Service;
import kr.o3selab.homecoco.R;

public class OrderActivity extends AppCompatActivity {

    @Bind(R.id.order_search_panel)
    LinearLayout mSearchPanel;
    @Bind(R.id.order_item_list)
    LinearLayout mItemList;
    @Bind(R.id.order_phone_field)
    EditText mPhoneField;

    private String[] mServiceNames = Service.getServices();
    private HashSet<OrderItem> mOrders = new HashSet<>();
    private int mInitalFlag = 0;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            initialization();
        }

        if (mPhoneField.getText().toString().length() == 11) {
            initialization();
        }
    }

    void initialization() {

        mInitalFlag = 0;
        mOrders = new HashSet<>();
        mItemList.removeAllViewsInLayout();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("주문내역을 불러오는 중입니다.");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mSearchPanel.setVisibility(View.GONE);
        }

        FirebaseDatabase rootDatabase = FirebaseDatabase.getInstance();
        DatabaseReference[] databaseReferences = new DatabaseReference[mServiceNames.length];

        for (int i = 0; i < mServiceNames.length; i++) {
            databaseReferences[i] = rootDatabase.getReference(mServiceNames[i]);
        }

        if (mPhone != null) {
            for (DatabaseReference reference : databaseReferences) {
                Query query = reference.orderByChild("mPhoneNumber").equalTo(mPhone);
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        } else if (Constants.isAdmin) {
            for (DatabaseReference reference : databaseReferences) {
                reference.addListenerForSingleValueEvent(valueEventListener);
            }
        } else {
            for (DatabaseReference reference : databaseReferences) {
                Query query = reference.orderByChild("mUid").equalTo(user.getUid());
                query.addListenerForSingleValueEvent(valueEventListener);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (mInitalFlag == mServiceNames.length) break;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                }

                LinkedList<OrderItem> list = new LinkedList<>(mOrders);
                Collections.sort(list, new Comparator<OrderItem>() {
                    @Override
                    public int compare(OrderItem o1, OrderItem o2) {
                        if (o1.mRegdate > o2.mRegdate)
                            return -1;
                        else if (o1.mRegdate < o2.mRegdate)
                            return 1;
                        else
                            return 0;
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });

                int location = 0;
                for (OrderItem orderItem : list) {
                    addOrderItem(orderItem, location++);
                }

            }
        }).start();

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null) {
                mInitalFlag++;
                return;
            }

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                OrderItem item = snapshot.getValue(OrderItem.class);
                if (item.mRegdate != null) mOrders.add(item);
            }

            mInitalFlag++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            mInitalFlag++;
        }
    };

    void addOrderItem(final OrderItem orderItem, final int location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getLayoutInflater().inflate(R.layout.order_item, null);

                ImageView typeIcon = (ImageView) view.findViewById(R.id.order_item_image);
                TextView typeName = (TextView) view.findViewById(R.id.order_item_type);
                switch (orderItem.mServiceType) {
                    case Service.HOME_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_01);
                        typeName.setText(getString(R.string.cleaning_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_01));
                        break;
                    case Service.REPAIR_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_02);
                        typeName.setText(getString(R.string.repair_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_02));
                        break;
                    case Service.INTERIOR_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_03);
                        typeName.setText(getString(R.string.interior_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_03));
                        break;
                    case Service.QUESTION_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_04);
                        typeName.setText(getString(R.string.question_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_04));
                        break;
                    case Service.SOLUTION_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_05);
                        typeName.setText(getString(R.string.solution_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_05));
                        break;
                    case Service.EBF_SERVICE:
                        typeIcon.setImageResource(R.drawable.s_orderlist_06);
                        typeName.setText(getString(R.string.ebf_title));
                        typeName.setTextColor(getResources().getColor(R.color.order_06));
                        break;
                }

                TextView buildingType = (TextView) view.findViewById(R.id.order_item_building_type);
                if (orderItem.mHomeType != null) buildingType.setText(orderItem.mHomeType);
                else buildingType.setText("");
                if (orderItem.mEBFType != null)
                    buildingType.setText(buildingType.getText() + " " + orderItem.mEBFType);
                else buildingType.setText(buildingType.getText());

                TextView phone = (TextView) view.findViewById(R.id.order_item_phone);
                phone.setText(orderItem.mPhoneNumber);

                TextView address = (TextView) view.findViewById(R.id.order_item_address);
                if (orderItem.mAddress != null && !orderItem.mAddress.equals("시/구 동/면/읍"))
                    address.setText(orderItem.mAddress);
                else address.setText("");
                if (orderItem.mDetailAddress != null) {
                    if (!address.getText().equals(""))
                        address.setText(address.getText() + " " + orderItem.mDetailAddress);
                    else
                        address.setText(orderItem.mDetailAddress);
                }

                TextView status = (TextView) view.findViewById(R.id.order_item_status);
                status.setText(orderItem.mStatus);

                TextView request = (TextView) view.findViewById(R.id.order_item_request);
                if (orderItem.mRequest != null && !orderItem.mRequest.equals(""))
                    request.setText(orderItem.mRequest);
                else request.setText("내용이 존재하지 않습니다.");

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                        intent.putExtra("order", orderItem);

                        startActivity(intent);
                    }
                });

                mItemList.addView(view, location);
            }
        });
    }

    @OnClick(R.id.order_back_button)
    void onBackButton() {
        this.finish();
    }

    @OnClick(R.id.order_search_button)
    void searchOnClick() {

        /* 전화번호 입력 체크 */

        if (mPhoneField.getText().toString().length() < 1) {
            showAlertDialog(getString(R.string.service_request_phone_error));
            mPhoneField.setBackgroundResource(R.drawable.input_form_error);
            mPhoneField.requestFocus();
            mPhoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && mPhoneField.getText().toString().length() > 1)
                        mPhoneField.setBackgroundResource(R.drawable.input_form_ok);
                }
            });

            return;

        } else if (mPhoneField.getText().toString().length() != 11) {
            showAlertDialog(getString(R.string.service_request_phone_error_with_phonenumber));
            mPhoneField.setBackgroundResource(R.drawable.input_form_error);
            mPhoneField.requestFocus();
            mPhoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && mPhoneField.getText().toString().length() == 11)
                        mPhoneField.setBackgroundResource(R.drawable.input_form_ok);
                }
            });

            return;

        } else {
            String phone = mPhoneField.getText().toString();
            mPhone = phone;

            String prefix = phone.substring(0, 3);
            String infix = phone.substring(3, 7);
            String postfix = phone.substring(7, 11);

            phone = prefix + "-" + infix + "-" + postfix;

            if (!phone.matches(Constants.phoneRegex)) {
                showAlertDialog(getString(R.string.service_request_phone_error_with_phonenumber));
                mPhoneField.setBackgroundResource(R.drawable.input_form_error);
                mPhoneField.requestFocus();
                mPhoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String phone = mPhoneField.getText().toString();
                        String prefix = phone.substring(0, 3);
                        String infix = phone.substring(3, 7);
                        String postfix = phone.substring(7, 11);
                        phone = prefix + "-" + infix + "-" + postfix;
                        if (!hasFocus && phone.matches(Constants.phoneRegex))
                            mPhoneField.setBackgroundResource(R.drawable.input_form_ok);
                    }
                });
                mPhone = null;
                return;
            }
        }

        initialization();
    }

    void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.default_ok, null)
                .show();
    }

}
