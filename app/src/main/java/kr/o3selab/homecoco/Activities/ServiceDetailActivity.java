package kr.o3selab.homecoco.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.wx.wheelview.widget.WheelViewDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.BuildConfig;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.Models.Extras;
import kr.o3selab.homecoco.Models.OrderItem;
import kr.o3selab.homecoco.Models.Service;
import kr.o3selab.homecoco.R;
import kr.o3selab.homecoco.Utils.CapturePhotoUtils;
import kr.o3selab.homecoco.Utils.HideKeyboard;
import me.grantland.widget.AutofitTextView;

public class ServiceDetailActivity extends AppCompatActivity {

    @Bind(R.id.report_top_title)
    TextView mTitle;

    @Bind(R.id.report_body_image_button)
    View mImageView;
    @Bind(R.id.report_body_image_select_layout)
    LinearLayout imageListLayout;

    @Bind(R.id.report_body_request)
    EditText mRequest;

    @Bind(R.id.report_body_category)
    View mCategory;
    @Bind(R.id.report_body_category_e)
    ImageView eImageView;
    @Bind(R.id.report_body_category_b)
    ImageView bImageView;
    @Bind(R.id.report_body_category_f)
    ImageView fImageView;

    @Bind(R.id.report_body_home_type)
    View mHomeType;
    @Bind(R.id.report_body_home_type_apart)
    ImageView apartImageView;
    @Bind(R.id.report_body_home_type_business)
    ImageView businessImageView;
    @Bind(R.id.report_body_home_type_store)
    ImageView storeImageView;

    @Bind(R.id.report_body_home_type_detail)
    View mHomeTypeDetail;
    @Bind(R.id.report_body_home_type_detail_text)
    AutofitTextView mHomeTypeDetailTextView;

    @Bind(R.id.report_body_reservation)
    View mReservation;
    @Bind(R.id.report_body_reservation_text)
    TextView mReservationTime;

    @Bind(R.id.report_body_address)
    View mAddress;
    @Bind(R.id.report_body_address_detail)
    EditText mAddressDetail;
    @Bind(R.id.report_body_address_address1_text)
    TextView address1TextView;
    @Bind(R.id.report_body_address_address2_text)
    TextView address2TextView;
    @Bind(R.id.report_body_address_address2)
    View address2View;

    @Bind(R.id.report_body_phone)
    EditText phoneField;
    @Bind(R.id.report_body_agree_check)
    CheckBox agreeBox;
    @Bind(R.id.report_body_agree_text)
    AutofitTextView agreeText;

    private String mServiceName;

    private final int NOTIFICATION_ID = 1004;

    private final int TAKE_PICTURE_REQUEST = 1000;
    private final int SELECT_PICTURE_REQUEST = 1001;
    private final int TAKE_VIDEO_REQUEST = 1002;
    private final int SELECT_VIDEO_REQUEST = 1003;
    private final int SEARCH_SELECT_TIME = 1004;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    private final int TYPE_APART = 100;
    private final int TYPE_BUSINESS = 200;
    private final int TYPE_STORE = 300;
    private final int TYPE_E = 400;
    private final int TYPE_B = 500;
    private final int TYPE_F = 600;

    private int mEBFTypeN = 0;
    private int mHomeTypeN = 0;
    private int mAddress1Type = 0;
    private int mAddress2Type = 0;
    private CharSequence[] mAddress2TypeArray;

    private ProgressDialog mProgressDialog;
    private AlertDialog mSelectTypeDialog;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private AlertDialog mAlert;
    private AlertDialog mAlertDetail;

    private String mPhone;

    private HashMap<Bitmap, String> mImages;
    private Uri mPhotoUri = null;
    private Uri mVideo = null;
    private String mVideoFileName = null;

    private StorageReference mStorageReference;
    private String mHomeTypeDetailText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mServiceName = intent.getStringExtra(Extras.SERVICE_NAME);

        mImageView.setVisibility(intent.getIntExtra(Extras.ORDER_PICTURE, View.GONE));
        mRequest.setVisibility(intent.getIntExtra(Extras.ORDER_REQUEST, View.GONE));
        mCategory.setVisibility(intent.getIntExtra(Extras.ORDER_CATEGORY, View.GONE));
        mHomeType.setVisibility(intent.getIntExtra(Extras.ORDER_TYPE, View.GONE));
        mHomeTypeDetail.setVisibility(View.GONE);
        mReservation.setVisibility(intent.getIntExtra(Extras.ORDER_RESERVATION, View.GONE));
        mAddress.setVisibility(intent.getIntExtra(Extras.ORDER_ADDRESS, View.GONE));
        mAddressDetail.setVisibility(intent.getIntExtra(Extras.ORDER_DETAILED_ADDRESS, View.GONE));

        switch (mServiceName) {
            case Service.HOME_SERVICE:
                mTitle.setText(getResources().getString(R.string.cleaning_title));
                mRequest.setHint(R.string.service_input_request_content_cleaning);
                break;
            case Service.REPAIR_SERVICE:
                mTitle.setText(getResources().getString(R.string.repair_title));
                mRequest.setHint(R.string.service_input_request_content_repair);
                break;
            case Service.INTERIOR_SERVICE:
                mTitle.setText(getResources().getString(R.string.interior_title));
                mRequest.setHint(R.string.service_input_request_content_interior);
                break;
            case Service.QUESTION_SERVICE:
                mTitle.setText(getResources().getString(R.string.question_title));
                mRequest.setHint(R.string.service_input_request_content_question);
                break;
            case Service.SOLUTION_SERVICE:
                mTitle.setText(getResources().getString(R.string.solution_title));
                mRequest.setHint(R.string.service_input_request_content_solution);
                break;
            case Service.EBF_SERVICE:
                mTitle.setText(getResources().getString(R.string.ebf_title));
                break;
        }

        mImages = new HashMap<>();
        mStorageReference = FirebaseStorage.getInstance().getReference(mServiceName);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.service_send_service_message));
        mProgressDialog.setCancelable(false);

        agreeText.setText(Html.fromHtml(getString(R.string.service_check_agree)));
        HideKeyboard.init(this);
    }

    @OnClick(R.id.report_top_back_button)
    void onBackButton() {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.service_back_message)
                .setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ServiceDetailActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.default_cancel, null)
                .show();
    }

    public void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.default_ok, null)
                .show();
    }

    @OnClick(R.id.report_body_image_button)
    void selectPictureOnClick() {

        View view = getLayoutInflater().inflate(R.layout.dialog_select_picture_type, null);

        View takePicture = view.findViewById(R.id.take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            try {
                                File image = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                                mPhotoUri = FileProvider.getUriForFile(ServiceDetailActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
                                mSelectTypeDialog.dismiss();
                            } catch (Exception e) {
                                Constants.printLog(e);
                                Toast.makeText(ServiceDetailActivity.this, R.string.service_take_picture_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(ServiceDetailActivity.this, R.string.service_permission_denied, Toast.LENGTH_SHORT).show();
                    }
                };

                new TedPermission(ServiceDetailActivity.this)
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });

        View selectPicture = view.findViewById(R.id.select_picture);
        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageListLayout.getChildCount() == 10) {
                    showAlertDialog(getString(R.string.service_select_pciture_over));
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_REQUEST);
                mSelectTypeDialog.dismiss();

            }
        });

        View takeVideo = view.findViewById(R.id.take_video);
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mVideo != null) {
                    showAlertDialog(getString(R.string.service_select_movie_over));
                    return;
                }

                mSelectTypeDialog.dismiss();

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File video = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                                    mVideo = FileProvider.getUriForFile(ServiceDetailActivity.this, BuildConfig.APPLICATION_ID + ".provider", video);

                                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024 * 1024 * 10));
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideo);

                                    startActivityForResult(intent, TAKE_VIDEO_REQUEST);
                                } catch (Exception e) {
                                    Constants.printLog(e);
                                    Toast.makeText(ServiceDetailActivity.this, R.string.service_take_video_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        new AlertDialog.Builder(ServiceDetailActivity.this)
                                .setMessage(R.string.service_select_video_message)
                                .setCancelable(true)
                                .setPositiveButton(R.string.default_ok, onClickListener)
                                .show();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(ServiceDetailActivity.this, R.string.service_permission_denied, Toast.LENGTH_SHORT).show();
                    }
                };

                new TedPermission(ServiceDetailActivity.this)
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
            }
        });

        View selectVideo = view.findViewById(R.id.select_video);
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideo == null) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/mp4");
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO_REQUEST);
                    mSelectTypeDialog.dismiss();
                } else showAlertDialog(getString(R.string.service_select_movie_over));
            }
        });

        View closeButton = view.findViewById(R.id.select_dialog_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectTypeDialog.dismiss();
            }
        });

        mSelectTypeDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        mSelectTypeDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SEARCH_SELECT_TIME && resultCode == 0) {
            if (data == null) return;
            mReservationTime.setText(data.getStringExtra(Extras.SELECTED_DATE_TIME));
            return;
        }

        if (requestCode == TAKE_PICTURE_REQUEST) {
            resultCode = RESULT_OK;
            Intent intent = new Intent();
            intent.setData(mPhotoUri);
            data = intent;
            mPhotoUri = null;
        }

        if ((requestCode == SELECT_PICTURE_REQUEST || requestCode == TAKE_PICTURE_REQUEST) && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                String fileName = System.currentTimeMillis() + ".jpg";

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (requestCode == TAKE_PICTURE_REQUEST)
                    CapturePhotoUtils.insertImage(getContentResolver(), bitmap, fileName, "");

                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                final ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Constants.DpToPixel(this, 60), Constants.DpToPixel(this, 60));
                layoutParams.setMargins(Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7));
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(scaledBitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final View view = getLayoutInflater().inflate(R.layout.dialog_picture, null);
                        ImageView pictureView = (ImageView) view.findViewById(R.id.dialog_picture);
                        pictureView.setImageBitmap(scaledBitmap);

                        final AlertDialog alertDialog = new AlertDialog.Builder(ServiceDetailActivity.this)
                                .setView(view)
                                .setCancelable(true)
                                .create();

                        pictureView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(ServiceDetailActivity.this)
                                .setMessage(R.string.service_remove_picture)
                                .setCancelable(true)
                                .setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imageListLayout.removeView(imageView);
                                        mImages.remove(scaledBitmap);
                                    }
                                })
                                .setNegativeButton(R.string.default_cancel, null)
                                .show();

                        return true;
                    }
                });

                imageListLayout.addView(imageView);
                mImages.put(scaledBitmap, fileName);
            } catch (Exception e) {
                Constants.printLog(e);
            }
        }


        if ((requestCode == TAKE_VIDEO_REQUEST || requestCode == SELECT_VIDEO_REQUEST) && resultCode != RESULT_CANCELED && data != null) {
            try {
                mVideo = data.getData();
                final Uri uri = mVideo;
                final String fileName = System.currentTimeMillis() + ".mp4";

                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.video_file);
                Bitmap bitmap = drawable.getBitmap();

                final ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Constants.DpToPixel(this, 60), Constants.DpToPixel(this, 60));
                layoutParams.setMargins(Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7), Constants.DpToPixel(this, 7));
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog(getString(R.string.service_not_show_video));
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(ServiceDetailActivity.this)
                                .setMessage(R.string.service_remove_video)
                                .setCancelable(true)
                                .setPositiveButton(R.string.default_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imageListLayout.removeView(imageView);
                                        mVideo = null;
                                        mVideoFileName = null;
                                    }
                                })
                                .setNegativeButton(R.string.default_cancel, null)
                                .show();

                        return true;
                    }
                });

                imageListLayout.addView(imageView);

                mVideo = uri;
                mVideoFileName = fileName;

            } catch (Exception e) {
                Constants.printLog(e);
            }

        } else {
            mVideo = null;
            mVideoFileName = null;
        }
    }

    @OnClick(R.id.report_body_category_e)
    void eOnClick() {
        mEBFTypeN = TYPE_E;
        eImageView.setImageResource(R.drawable.s_category_01_on);
        bImageView.setImageResource(R.drawable.s_category_02_off);
        fImageView.setImageResource(R.drawable.s_category_03_off);
    }

    @OnClick(R.id.report_body_category_b)
    void bOnClick() {
        mEBFTypeN = TYPE_B;
        eImageView.setImageResource(R.drawable.s_category_01_off);
        bImageView.setImageResource(R.drawable.s_category_02_on);
        fImageView.setImageResource(R.drawable.s_category_03_off);
    }

    @OnClick(R.id.report_body_category_f)
    void fOnClick() {
        mEBFTypeN = TYPE_F;
        eImageView.setImageResource(R.drawable.s_category_01_off);
        bImageView.setImageResource(R.drawable.s_category_02_off);
        fImageView.setImageResource(R.drawable.s_category_03_on);
    }

    @OnClick(R.id.report_body_home_type_apart)
    void apartOnClick() {
        mHomeTypeN = TYPE_APART;
        apartImageView.setImageResource(R.drawable.s_building_01_on);
        businessImageView.setImageResource(R.drawable.s_building_02_off);
        storeImageView.setImageResource(R.drawable.s_building_03_off);
        homeTypeDetailClick();
    }

    @OnClick(R.id.report_body_home_type_business)
    void businessOnClick() {
        mHomeTypeN = TYPE_BUSINESS;
        apartImageView.setImageResource(R.drawable.s_building_01_off);
        businessImageView.setImageResource(R.drawable.s_building_02_on);
        storeImageView.setImageResource(R.drawable.s_building_03_off);
        homeTypeDetailClick();
    }

    @OnClick(R.id.report_body_home_type_store)
    void storeOnClick() {
        mHomeTypeN = TYPE_STORE;
        apartImageView.setImageResource(R.drawable.s_building_01_off);
        businessImageView.setImageResource(R.drawable.s_building_02_off);
        storeImageView.setImageResource(R.drawable.s_building_03_on);
        homeTypeDetailClick();
    }

    void homeTypeDetailClick() {
        if (!mServiceName.equals(Service.INTERIOR_SERVICE)) return;

        mHomeTypeDetail.setVisibility(View.VISIBLE);

        mHomeTypeDetailText = null;
        mHomeTypeDetailTextView.setText("세부공간");

        mHomeTypeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WheelViewDialog dialog = new WheelViewDialog(ServiceDetailActivity.this);
                dialog.setTitle("세부공간을 선택해주세요")
                        .setButtonText("확인")
                        .setDialogStyle(getResources().getColor(R.color.colorPrimary))
                        .setOnDialogItemClickListener(new WheelViewDialog.OnDialogItemClickListener() {
                            @Override
                            public void onItemClick(int position, String s) {
                                mHomeTypeDetailText = s;
                                mHomeTypeDetailTextView.setText(mHomeTypeDetailText);
                            }
                        })
                        .setCount(5);

                switch (mHomeTypeN) {
                    case TYPE_APART:
                        dialog.setItems(Arrays.asList(getResources().getStringArray(R.array.home)));
                        break;
                    case TYPE_BUSINESS:
                        dialog.setItems(Arrays.asList(getResources().getStringArray(R.array.business)));
                        break;
                    case TYPE_STORE:
                        dialog.setItems(Arrays.asList(getResources().getStringArray(R.array.store)));
                        break;
                }

                dialog.show();
            }
        });
    }

    @OnClick(R.id.report_body_address_address1)
    void address1OnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(Constants.address1, mAddress1Type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAddress1Type = which;
                address1TextView.setText(Constants.address1[mAddress1Type]);
                address2TextView.setText(R.string.service_select_address2);

                switch (which) {
                    case 0:
                        mAddress2TypeArray = getResources().getStringArray(R.array.c_dongnam);
                        break;
                    case 1:
                        mAddress2TypeArray = getResources().getStringArray(R.array.c_subok);
                        break;
                    case 2:
                        mAddress2TypeArray = getResources().getStringArray(R.array.asan);
                        break;
                }

                address2View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDetailActivity.this);
                        builder.setSingleChoiceItems(mAddress2TypeArray, mAddress2Type, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAddress2Type = which;
                                address2TextView.setText(mAddress2TypeArray[which]);
                                mAlertDetail.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        mAlertDetail = builder.create();
                        mAlertDetail.show();
                    }
                });
                mAlert.dismiss();
            }
        });

        builder.setCancelable(false);
        mAlert = builder.create();
        mAlert.show();
    }

    @OnClick(R.id.report_body_reservation)
    void reservationOnClick() {
        Intent intent = new Intent(this, SelectTimeActivity.class);
        intent.putExtra(Extras.REQUEST_DATE_TIME, mReservationTime.getText());
        startActivityForResult(intent, SEARCH_SELECT_TIME);
    }

    @OnClick(R.id.report_body_agree_text)
    void runTermsOnClick() {
        startActivity(new Intent(ServiceDetailActivity.this, TermsActivity.class));
    }

    @OnClick(R.id.report_body_send_button)
    void sendButton() {
        try {
            OrderItem orderItem = new OrderItem();

            /* 전화번호 입력 체크 */
            if (phoneField.getText().toString().length() < 1) {
                showAlertDialog(getString(R.string.service_request_phone_error));
                phoneField.setBackgroundResource(R.drawable.input_form_error);
                phoneField.requestFocus();
                phoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && phoneField.getText().toString().length() > 1)
                            phoneField.setBackgroundResource(R.drawable.input_form);
                    }
                });

                return;

            } else if (phoneField.getText().toString().length() != 11) {
                showAlertDialog(getString(R.string.service_request_phone_error_with_phonenumber));
                phoneField.setBackgroundResource(R.drawable.input_form_error);
                phoneField.requestFocus();
                phoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && phoneField.getText().toString().length() == 11)
                            phoneField.setBackgroundResource(R.drawable.input_form);
                    }
                });

                return;

            } else {
                String phone = phoneField.getText().toString();
                mPhone = phone;

                String prefix = phone.substring(0, 3);
                String infix = phone.substring(3, 7);
                String postfix = phone.substring(7, 11);

                phone = prefix + "-" + infix + "-" + postfix;

                if (!phone.matches(Constants.phoneRegex)) {
                    showAlertDialog(getString(R.string.service_request_phone_error_with_phonenumber));
                    phoneField.setBackgroundResource(R.drawable.input_form_error);
                    phoneField.requestFocus();
                    phoneField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            String phone = phoneField.getText().toString();
                            String prefix = phone.substring(0, 3);
                            String infix = phone.substring(3, 7);
                            String postfix = phone.substring(7, 11);
                            phone = prefix + "-" + infix + "-" + postfix;
                            if (!hasFocus && phone.matches(Constants.phoneRegex))
                                phoneField.setBackgroundResource(R.drawable.input_form);
                        }
                    });
                    return;
                }

                phoneField.setBackgroundResource(R.drawable.input_form);
            }

            /* 이용동의 체크 */
            if (!agreeBox.isChecked()) {
                showAlertDialog(getString(R.string.service_select_agree));
                return;
            }

            /* 데이터 전송 */

            mProgressDialog.show();

            orderItem.mDeviceID = Constants.deviceID;
            orderItem.mServiceType = mServiceName;
            orderItem.mRegdate = System.currentTimeMillis();

            if (mRequest.getVisibility() == View.VISIBLE)
                orderItem.mRequest = mRequest.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) orderItem.mUserEmail = user.getEmail();

            switch (mEBFTypeN) {
                case TYPE_E:
                    orderItem.mEBFType = "시설경비";
                    break;
                case TYPE_B:
                    orderItem.mEBFType = "미화관리";
                    break;
                case TYPE_F:
                    orderItem.mEBFType = "소방";
                    break;
            }

            switch (mHomeTypeN) {
                case TYPE_APART:
                    orderItem.mHomeType = "아파트/주택";
                    break;
                case TYPE_BUSINESS:
                    orderItem.mHomeType = "기업체";
                    break;
                case TYPE_STORE:
                    orderItem.mHomeType = "상업시설";
                    break;
            }

            orderItem.mHomeTypeDetail = mHomeTypeDetailText;
            orderItem.mAddress = address1TextView.getText().toString() + " " + address2TextView.getText().toString();
            orderItem.mAddress = orderItem.mAddress.replace("시/구", "");
            orderItem.mAddress = orderItem.mAddress.replace("동/면/읍", "");
            orderItem.mDetailAddress = mAddressDetail.getText().toString();
            orderItem.mPhoneNumber = mPhone;

            if (mReservation.getVisibility() == View.VISIBLE) {
                if (!mReservationTime.getText().toString().contains("선택해주세요")) {
                    orderItem.mReservationTime = mReservationTime.getText().toString();
                }
            }

            /* 데이터 전송 -> 이미지 전송 */
            ArrayList<String> images = new ArrayList<>();

            for (Bitmap bitmap : mImages.keySet()) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                StorageReference imageReference = mStorageReference.child(mPhone + "/" + mImages.get(bitmap));

                imageReference.putBytes(data);
                images.add(mImages.get(bitmap));
            }
            orderItem.mImages = images;


            /* 데이터 전송 -> 비디오 전송 */
            if (mVideo != null) {
                StorageReference videoReference = mStorageReference.child(mPhone + "/" + mVideoFileName);

                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setContentTitle(getString(R.string.service_send_video_title));
                mBuilder.setContentText(getString(R.string.service_send_video_message));
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);

                UploadTask uploadTask = videoReference.putFile(mVideo);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((double) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() * 100);
                        mBuilder.setProgress(100, progress, false);
                        mBuilder.setContentText("업로드 중...(" + progress + "%)");
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    }
                });
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.getResult() != null) {
                            mBuilder.setContentTitle(getString(R.string.service_send_video_ok_title));
                            mBuilder.setContentText(getString(R.string.service_send_video_ok_message));
                        } else {
                            mBuilder.setContentTitle(getString(R.string.service_send_video_cancel_title));
                            mBuilder.setContentText(getString(R.string.service_send_video_cancel_message));
                        }
                        mBuilder.setProgress(0, 0, false);
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    }
                });
            }

            orderItem.mVideo = mVideoFileName;
            orderItem.mStatus = "접수완료";

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                orderItem.mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(mServiceName).child(String.valueOf(orderItem.mRegdate));
            databaseReference.setValue(orderItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(ServiceDetailActivity.this, R.string.service_send_complete, Toast.LENGTH_SHORT).show();
                        ServiceDetailActivity.this.finish();
                    } else {
                        Toast.makeText(ServiceDetailActivity.this, R.string.service_send_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            mProgressDialog.dismiss();
            Constants.printLog(e);
        }
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
