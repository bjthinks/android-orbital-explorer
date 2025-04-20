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
            Insets statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars());
            Insets navBarInsets = insets.getInsets(WindowInsets.Type.navigationBars());
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.dark));
            v.setPadding(navBarInsets.left, statusBarInsets.top, navBarInsets.right, 0);
            return insets;
        }
        return insets;
    }
}