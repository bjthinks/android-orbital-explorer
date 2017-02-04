package com.gputreats.orbitalexplorer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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
        setContentView(R.layout.activity_help);

        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");

        setSupportActionBar((Toolbar) findViewById(R.id.help_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
