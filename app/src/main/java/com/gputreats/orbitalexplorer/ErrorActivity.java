package com.gputreats.orbitalexplorer;

import android.app.Activity;
import android.os.Bundle;

public class ErrorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_error);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
