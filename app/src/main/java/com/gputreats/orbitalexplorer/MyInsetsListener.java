package com.gputreats.orbitalexplorer;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class MyInsetsListener implements View.OnApplyWindowInsetsListener {

    Context context;

    MyInsetsListener(Context c) {
        context = c;
    }

    @NonNull
    @Override
    public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() |
                            WindowInsets.Type.displayCutout());
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.dark));
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsets.CONSUMED;
        }
        return insets;
    }
}