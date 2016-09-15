package com.gputreats.orbitalexplorer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        title = extras.getString("title");
        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
        setSupportActionBar(toolbar);

        WebView webview = (WebView) findViewById(R.id.help_webview);
        if (webview != null) {
            webview.setBackgroundColor(Color.BLACK);
            WebSettings settings = webview.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            settings.setJavaScriptEnabled(true);
            String url = extras.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    ? "url" : "url-v19");
            webview.loadUrl(url + "?v=" + BuildConfig.VERSION_NAME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.setScreenName("Help " + title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
