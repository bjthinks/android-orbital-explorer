package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class HelpActivity extends AppCompatActivity {

    private Tracker tracker;
    private String title;

    @Override
    protected void onCreate(Bundle savedState) {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        tracker = application.getTracker();

        super.onCreate(savedState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        title = extras.getString("title");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar a = getSupportActionBar();
        if (a != null)
            a.setDisplayHomeAsUpEnabled(true);

        WebView webview = (WebView) findViewById(R.id.help_webview);
        webview.getSettings().setDefaultTextEncodingName("utf-8");
        String url = extras.getString("url");
        webview.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tracker.setScreenName("Help " + title);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
