package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;

public class WebViewActivity extends AppCompatActivity {

    private Context _context;
    private String from;
    private String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        from = getIntent().getExtras().getString("EXTRA_FROM");
        url = getIntent().getExtras().getString("EXTRA_URL");
        initControls();
    }

    private void initControls() {
        _context = WebViewActivity.this;
        toolBar();
        initView();
    }

    private void toolBar() {
        if (from.equalsIgnoreCase("paynow")) {
            setTitle("Rental Payment");
        } else {
            setTitle("AutoPay Setup");
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        ProgressDialog.showProgress(_context);
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("Page Started >> ", url);
                ProgressDialog.dismissProgress();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ProgressDialog.dismissProgress();
                Log.e("OverrideUrlLoading>> ", url);
                if (from.equalsIgnoreCase("paynow")) {
                    if (url.contains("/payment/done")) {
                        goToRentalPaymentScreen();
                    }
                } else {
                    //https://portaldev.invictusmanagement.net/payment/AutoPaySetSuceessfully
                    if (url.contains("/payment/autopaysetsuceessfully")) {
                        goToRentalPaymentScreen();
                    }
                }
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(url);
    }

    private void goToRentalPaymentScreen() {
        if (from.equalsIgnoreCase("paynow")) {
            Toast.makeText(_context, "Thank You! Payment successfully done.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(_context, "Thank You! Auto Payment Setup Successfully.",
                    Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(WebViewActivity.this, RentalPaymentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}