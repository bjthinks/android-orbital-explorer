package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;

public class CardboardActivity extends GvrActivity {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_cardboard);
        CardboardView cv = (CardboardView) findViewById(R.id.cardboardView);
        setGvrView(cv);
    }

    @Override
    public void onCardboardTrigger() {
        Log.d("CardboardActivity", "Trigger");
    }
}
