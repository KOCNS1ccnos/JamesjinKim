package kr.o3selab.homecoco.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageSaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.Models.Constants;
import kr.o3selab.homecoco.R;

public class OrderDetailImageView extends AppCompatActivity {

    @Bind(R.id.order_detail_image_view)
    BigImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_image_view);

        ButterKnife.bind(this);

        Uri data = getIntent().getData();
        mImageView.showImage(data);
    }

    @OnClick(R.id.order_detail_image_save)
    void saveImageOnClick() {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("사진을 저장 중 입니다.")
                .setCancelable(false)
                .create();
        dialog.show();

        mImageView.setImageSaveCallback(new ImageSaveCallback() {
            @Override
            public void onSuccess(String uri) {
                dialog.dismiss();
                Toast.makeText(OrderDetailImageView.this, "갤러리에 사진을 저장했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Throwable t) {
                dialog.dismiss();
                Constants.printLog(t.getMessage(), true);
                Toast.makeText(OrderDetailImageView.this, "갤러리에 저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            mImageView.saveImageIntoGallery();
        } catch (SecurityException ignored) {

        }
    }
}
