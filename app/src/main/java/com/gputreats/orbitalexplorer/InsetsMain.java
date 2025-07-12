package com.gputreats.orbitalexplorer;

import static java.lang.Math.max;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class InsetsMain implements View.OnApplyWindowInsetsListener {

    InsetsMain() {}

    @NonNull
    @Override
    public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            Insets cutouts = insets.getInsets(WindowInsets.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = max(cutouts.top - bars.top, 0);
            mlp.leftMargin = max(cutouts.left - bars.left, 0);
            mlp.rightMargin = max(cutouts.right - bars.right, 0);
            mlp.bottomMargin = max(cutouts.bottom - bars.bottom, 0);
            v.setLayoutParams(mlp);
            return WindowInsets.CONSUMED;
        }
        return insets;
    }
}