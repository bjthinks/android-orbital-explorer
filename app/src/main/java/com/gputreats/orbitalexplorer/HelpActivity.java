package com.gputreats.orbitalexplorer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");

        Toolbar toolbar = findViewById(R.id.help_toolbar);
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WebView webview = findViewById(R.id.help_webview);
        if (webview != null) {
            webview.setBackgroundColor(Color.BLACK);
            WebSettings settings = webview.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            String url = extras.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    ? "url" : "url-v19");
            webview.loadUrl(url);
        }
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
