package com.gputreats.orbitalexplorer;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class HelpActivity extends AppCompatActivity {

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Handle insets and set the system bars colors on Android 15+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            View root = findViewById(R.id.coordinator_layout);
            root.setOnApplyWindowInsetsListener(new InsetsAux(this));
        }

        String title;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            title = extras.getString("title");
        else
            title = "Help";

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        webview = findViewById(R.id.help_webview);
        if (webview != null) {
            if (savedInstanceState != null) {
                webview.restoreState(savedInstanceState);
            } else {
                webview.setBackgroundColor(Color.BLACK);
                WebSettings settings = webview.getSettings();
                settings.setDefaultTextEncodingName("utf-8");
                String url;
                if (extras != null)
                    url = extras.getString(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                            ? "url" : "url-v19");
                else
                    url = "file:///android_asset/docs/help.html";
                webview.loadUrl(url);
            }
        }

        // Check if we want to scroll the toolbar
        if (extras != null) {
            boolean scroll = extras.getBoolean("scrollToolbar");
            if (scroll) {
                CollapsingToolbarLayout collapsingToolbarLayout =
                        findViewById(R.id.collapsing_toolbar_layout);
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                        collapsingToolbarLayout.getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                collapsingToolbarLayout.setLayoutParams(params);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
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
