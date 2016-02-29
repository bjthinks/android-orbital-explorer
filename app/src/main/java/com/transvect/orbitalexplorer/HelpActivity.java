package com.transvect.orbitalexplorer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(extras.getString("title"));
        setSupportActionBar(toolbar);

        WebView webview = (WebView) findViewById(R.id.helpview);
        webview.loadUrl(extras.getString("url"));
    }
}
