package com.transvect.orbitalexplorer;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private MyView mMyView;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_main);

        mMyView = (MyView) findViewById(R.id.orbitalview);
        MyRenderer renderer = new MyRenderer();
        mMyView.setRenderer(renderer);
    }
}
