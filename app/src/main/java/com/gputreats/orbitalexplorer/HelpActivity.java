package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        toolbar.setTitle(extras.getString("title"));
        setSupportActionBar(toolbar);
        ActionBar a = getSupportActionBar();
        if (a != null)
            a.setDisplayHomeAsUpEnabled(true);

        WebView webview = (WebView) findViewById(R.id.help_webview);
        webview.loadUrl(extras.getString("url"));
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
