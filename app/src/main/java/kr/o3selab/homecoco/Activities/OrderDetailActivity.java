package kr.o3selab.homecoco.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.Models.OrderItem;
import kr.o3selab.homecoco.Models.Service;
import kr.o3selab.homecoco.R;

public class OrderDetailActivity extends AppCompatActivity {

    OrderItem mOrderItem;

    @Bind(R.id.order_detail_title)
    TextView mTitle;
    @Bind(R.id.order_detail_select_image_layout)
    LinearLayout mImageList;
    @Bind(R.id.order_detail_image_progress)
    AVLoadingIndicatorView mImageProgress;
    @Bind(R.id.order_detail_request_field)
    TextView mRequestView;
    @Bind(R.id.order_detail_category_field)
    LinearLayout mCategoryField;
    @Bind(R.id.order_detail_type_e)
    ImageView mTypeE;
    @Bind(R.id.order_detail_type_b)
    ImageView mTypeB;
    @Bind(R.id.order_detail_type_f)
    ImageView mTypeF;
    @Bind(R.id.order_detail_home_type)
    LinearLayout mTypeField;
    @Bind(R.id.order_detail_type_apart)
    ImageView mTypeApart;
    @Bind(R.id.order_detail_type_business)
    ImageView mTypeBusiness;
    @Bind(R.id.order_detail_type_store)
    ImageView mTypeStore;
    @Bind(R.id.order_detail_home_type_detail)
    TextView mTypeDetailField;
    @Bind(R.id.order_detail_reservation)
    TextView mTypeReservation;
    @Bind(R.id.order_detail_address)
    LinearLayout mAddress;
    @Bind(R.id.order_detail_address1_text)
    TextView mAddress1;
    @Bind(R.id.order_detail_address_detail_field)
    TextView mDetailAddress;
    @Bind(R.id.order_detail_phone_field)
    TextView mPhone;
    @Bind(R.id.order_detail_video)
    TextView mVideo;
    @Bind(R.id.order_detail_regdate)
    TextView mRegdate;
    @Bind(R.id.order_detail_status)
    LinearLayout mStatus;
    @Bind(R.id.order_detail_status_text)
    TextView mStatusText;
    @Bind(R.id.order_detail_status_modify)
    LinearLayout mStatusModify;
    @Bind(R.id.order_detail_delete)
    LinearLayout mDelete;

    int mOrderStatus = 0;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ButterKnife.bind(this);
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        mOrderItem = (OrderItem) getIntent().getSerializableExtra("order");

        // ?????????
        switch (mOrderItem.mServiceType) {
            case Service.HOME_SERVICE:
                mTitle.setText("????????????");
                break;
            case Service.REPAIR_SERVICE:
                mTitle.setText("?????? ??????/??????");
                break;
            case Service.INTERIOR_SERVICE:
                mTitle.setText("????????????/????????????");
                break;
            case Service.QUESTION_SERVICE:
                mTitle.setText("???????????? ???????????????");
                break;
            case Service.SOLUTION_SERVICE:
                mTitle.setText("??????/?????? ?????????");
                break;
            case Service.EBF_SERVICE:
                mTitle.setText("??????/??????/??????");
                break;
        }

        // ??????(?????????)
        if (mOrderItem.mImages != null && mOrderItem.mImages.size() != 0) {
            mImageProgress.setVisibility(View.VISIBLE);
            for (String image : mOrderItem.mImages) {
                getImage(image);
            }
        }

        // ????????????
        if (mOrderItem.mRequest != null && !mOrderItem.mRequest.equals("")) {
            mRequestView.setText(mOrderItem.mRequest);
        } else {
            mRequestView.setVisibility(View.GONE);
        }

        // ????????????
        if (mOrderItem.mEBFType != null) {
            switch (mOrderItem.mEBFType) {
                case "????????????":
                    mTypeE.setImageResource(R.drawable.s_category_01_on);
                    break;

                case "????????????":
                    mTypeB.setImageResource(R.drawable.s_category_02_on);
                    break;

                case "????????????":
                    mTypeF.setImageResource(R.drawable.s_category_03_on);
                    break;
            }
        } else {
            mCategoryField.setVisibility(View.GONE);
        }

        // ????????????
        if (mOrderItem.mHomeType != null) {
            switch (mOrderItem.mHomeType) {
                case "?????????/??????":
                    mTypeApart.setImageResource(R.drawable.s_building_01_on);
                    break;

                case "?????????":
                    mTypeBusiness.setImageResource(R.drawable.s_building_02_on);
                    break;

                case "????????????":
                    mTypeStore.setImageResource(R.drawable.s_building_03_on);
                    break;
            }
        } else {
            mTypeField.setVisibility(View.GONE);
        }

        // ??????????????????
        if (mOrderItem.mHomeTypeDetail != null)
            mTypeDetailField.setText(mOrderItem.mHomeTypeDetail);
        else mTypeDetailField.setVisibility(View.GONE);

        // ????????????
        if (mOrderItem.mReservationTime != null)
            mTypeReservation.setText(mOrderItem.mReservationTime);
        else mTypeReservation.setVisibility(View.GONE);

        // ??????
        if (mOrderItem.mAddress != null && !mOrderItem.mAddress.equals(" ")) {
            mAddress1.setText(mOrderItem.mAddress);
        } else {
            mAddress.setVisibility(View.GONE);
        }

        // ????????????
        if (mOrderItem.mDetailAddress != null && !mOrderItem.mDetailAddress.equals("")) {
            mDetailAddress.setText(mOrderItem.mDetailAddress);
        } else {
            mDetailAddress.setVisibility(View.GONE);
        }

        // ?????????
        mPhone.setText(mOrderItem.mPhoneNumber);

        // ?????????
        mRegdate.setText(new SimpleDateFormat("yyyy??? MM??? dd??? HH??? mm??? ss???").format(new Date(mOrderItem.mRegdate)));

        // ?????????
        if (mOrderItem.mVideo != null && !mOrderItem.mVideo.equals("")) {
            mVideo.setText(mOrderItem.mVideo);
            mVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(mOrderItem.mServiceType + "/" + mOrderItem.mPhoneNumber + "/" + mOrderItem.mVideo);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "video/*");
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            mVideo.setVisibility(View.GONE);
        }

        // ??????
        mStatusText.setText(mOrderItem.mStatus);

        // ?????? -> ???????????????
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> admins = (ArrayList<String>) dataSnapshot.getValue();
                    if (admins.contains(user.getEmail())) adminMode();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    void adminMode() {
        mStatusModify.setVisibility(View.VISIBLE);
        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] status = {"????????????", "?????????", "????????????"};

                switch (mOrderItem.mStatus) {
                    case "????????????":
                        mOrderStatus = 0;
                        break;
                    case "?????????":
                        mOrderStatus = 1;
                        break;
                    case "????????????":
                        mOrderStatus = 2;
                        break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                builder.setSingleChoiceItems(status, mOrderStatus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOrderStatus = which;
                        mOrderItem.mStatus = status[mOrderStatus];

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(mOrderItem.mServiceType + "/" + mOrderItem.mRegdate);
                        databaseReference.setValue(mOrderItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mAlertDialog.dismiss();
                                mStatusText.setText(status[mOrderStatus]);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mAlertDialog.dismiss();
                                Toast.makeText(OrderDetailActivity.this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        });

        mDelete.setVisibility(View.VISIBLE);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                builder.setTitle("??????");
                builder.setMessage("??????????????? ?????????????????????????\n??????????????? ????????? ????????? ?????????.");
                builder.setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(mOrderItem.mServiceType + "/" + mOrderItem.mRegdate);
                        databaseReference.removeValue();

                        OrderDetailActivity.this.finish();
                    }
                });
                builder.setNegativeButton(R.string.default_cancel, null);
                builder.setCancelable(true);
                builder.show();
            }
        });

    }

    @OnClick(R.id.order_detail_back_button)
    void backOnClick() {
        this.finish();
    }

    void getImage(String image) {
        mImageProgress.smoothToShow();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(mOrderItem.mServiceType + "/" + mOrderItem.mPhoneNumber + "/" + image);
        try {
            final File file = File.createTempFile("image", ".jpg");
            storageReference
                    .getFile(file)
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mImageProgress.show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

                                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                                final ImageView imageView = new ImageView(OrderDetailActivity.this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Constants.DpToPixel(OrderDetailActivity.this, 200), Constants.DpToPixel(OrderDetailActivity.this, 200));
                                layoutParams.setMargins(Constants.DpToPixel(OrderDetailActivity.this, 7), Constants.DpToPixel(OrderDetailActivity.this, 7), Constants.DpToPixel(OrderDetailActivity.this, 7), Constants.DpToPixel(OrderDetailActivity.this, 7));
                                imageView.setLayoutParams(layoutParams);
                                imageView.setImageBitmap(scaledBitmap);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(OrderDetailActivity.this, OrderDetailImageView.class);
                                        intent.setData(Uri.fromFile(file));
                                        startActivity(intent);
                                    }
                                });

                                mImageList.addView(imageView);
                                mImageProgress.hide();
                            } catch (Exception ignored) {
                                mImageProgress.hide();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
