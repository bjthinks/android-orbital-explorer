package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        title = extras.getString("title");
        if (toolbar != null)
            toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar a = getSupportActionBar();
        if (a != null)
            a.setDisplayHomeAsUpEnabled(true);

        WebView webview = (WebView) findViewById(R.id.help_webview);
        if (webview != null) {
            webview.getSettings().setDefaultTextEncodingName("utf-8");
            String url = extras.getString("url");
            webview.loadUrl(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.setScreenName("Help " + title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
