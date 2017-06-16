package com.gputreats.orbitalexplorer;

import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;

public class CardboardActivity extends GvrActivity {

    private Orbital orbital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        orbital = new Orbital(extras.getInt("Z"), extras.getInt("N"), extras.getInt("L"),
                extras.getInt("M"), extras.getBoolean("real"), extras.getBoolean("color"));

        setContentView(R.layout.activity_cardboard);
        CardboardView cv = (CardboardView) findViewById(R.id.cardboardView);
        setGvrView(cv);
    }

    @Override
    public void onCardboardTrigger() {
        if (BuildConfig.DEBUG)
            Log.i("CardboardActivity", "Trigger");
    }

    Orbital getOrbital() {
        return orbital;
    }
}
