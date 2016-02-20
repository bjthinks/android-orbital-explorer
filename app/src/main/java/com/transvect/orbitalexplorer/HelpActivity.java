package com.transvect.orbitalexplorer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    WebView webview;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_help);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        webview = (WebView) findViewById(R.id.helpview);
        webview.loadData("<html><body bgcolor=\"#000\" text=\"#fff\">Foo</body></html>", "text/html", null);
    }

    // This might happen before or after onPause(), but if it needs to be called,
    // it will always be called before onStop().
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
    }
}
