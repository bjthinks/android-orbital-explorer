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
public class MyInsetsListener implements View.OnApplyWindowInsetsListener {

    private final boolean doBottomInsets;
    private final View bottomTransparency;

    MyInsetsListener(boolean doBottomInsets_, View bottomTransparency_) {
        doBottomInsets = doBottomInsets_;
        bottomTransparency = bottomTransparency_;
    }

    @NonNull
    @Override
    public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            Insets cutouts = insets.getInsets(WindowInsets.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, doBottomInsets ? bars.bottom : 0);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = max(cutouts.top - bars.top, 0);
            mlp.leftMargin = max(cutouts.left - bars.left, 0);
            mlp.rightMargin = max(cutouts.right - bars.right, 0);
            mlp.bottomMargin = doBottomInsets ? max(cutouts.bottom - bars.bottom, 0) : 0;
            v.setLayoutParams(mlp);
            if (bottomTransparency != null) {
                ViewGroup.LayoutParams layoutParams = bottomTransparency.getLayoutParams();
                layoutParams.height = bars.bottom;
                bottomTransparency.setLayoutParams(layoutParams);
            }
            return WindowInsets.CONSUMED;
        }
        return insets;
    }
}