package com.gputreats.orbitalexplorer;

import static java.lang.Math.max;
import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
public class InsetsAux implements View.OnApplyWindowInsetsListener {

    private final Activity activity;

    InsetsAux(Activity activity_) {
        activity = activity_;
    }

    @NonNull
    @Override
    public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            Insets cutouts = insets.getInsets(WindowInsets.Type.displayCutout());

            // Add side margins for cutouts
            View sidebar_background = activity.findViewById(R.id.sidebar_background);
            setSideMargins(sidebar_background, cutouts.left, cutouts.right);

            // Left & right inset the app bar, background, content, and top & bottom insets
            int leftInset = max(bars.left, cutouts.left);
            int rightInset = max(bars.right, cutouts.right);
            View appbar = activity.findViewById(R.id.appbar);
            View content_background = activity.findViewById(R.id.content_background);
            View content = activity.findViewById(R.id.content);
            View topbottom_insets = activity.findViewById(R.id.topbottom_insets);
            setSideMargins(appbar, leftInset, rightInset);
            setSideMargins(content_background, leftInset, rightInset);
            setSideMargins(content, leftInset, rightInset);
            setSideMargins(topbottom_insets, leftInset, rightInset);

            // Give the toolbar a top margin
            int topInset = max(bars.top, cutouts.top);
            View toolbar = activity.findViewById(R.id.toolbar);
            setTopMargin(toolbar, topInset);

            // Size the top and bottom transparencies to cover the system bars
            View top_transparency = activity.findViewById(R.id.top_transparency);
            View bottom_transparency = activity.findViewById(R.id.bottom_transparency);
            setHeight(top_transparency, bars.top);
            setHeight(bottom_transparency, bars.bottom);

            //v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsets.CONSUMED;
        }
        return insets;
    }

    private void setSideMargins(View v, int left, int right) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        mlp.leftMargin = left;
        mlp.rightMargin = right;
        v.setLayoutParams(mlp);
    }

    private void setTopMargin(View v, int top) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        mlp.topMargin = top;
        v.setLayoutParams(mlp);
    }

    private void setHeight(View v, int height) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = height;
        v.setLayoutParams(layoutParams);
    }
}