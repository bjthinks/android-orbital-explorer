package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;

public class CardboardActivity extends GvrActivity {

    Orbital orbital;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle extras = getIntent().getExtras();
        orbital = new Orbital(extras.getInt("Z"), extras.getInt("N"), extras.getInt("L"),
                extras.getInt("M"), extras.getBoolean("real"), extras.getBoolean("color"));

        setContentView(R.layout.activity_cardboard);
        CardboardView cv = (CardboardView) findViewById(R.id.cardboardView);
        setGvrView(cv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.setScreenName("Cardboard");
    }

    @Override
    public void onCardboardTrigger() {
        Log.d("CardboardActivity", "Trigger");
    }

    public Orbital getOrbital() {
        return orbital;
    }
}
