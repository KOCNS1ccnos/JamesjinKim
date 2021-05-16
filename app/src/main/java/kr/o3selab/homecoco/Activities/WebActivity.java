package kr.o3selab.homecoco.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.o3selab.homecoco.R;

public class WebActivity extends AppCompatActivity {

    @Bind(R.id.webview_webview)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        if (url == null) this.finish();

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }

    @OnClick(R.id.webview_back_button)
    void backButtonOnClick() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) mWebView.goBack();
        else super.onBackPressed();
    }
}
