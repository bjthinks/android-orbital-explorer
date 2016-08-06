package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;

public class CardboardActivity extends GvrActivity {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_cardboard);
        GvrView gvrView = (GvrView) findViewById(R.id.gvrView);
        gvrView.setRenderer(new CardboardRenderer());
    }

    @Override
    public void onCardboardTrigger() {
        Log.d("CardboardActivity", "Trigger");
    }
}
