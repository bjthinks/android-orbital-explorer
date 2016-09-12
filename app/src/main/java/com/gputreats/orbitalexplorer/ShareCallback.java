package com.gputreats.orbitalexplorer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class ShareCallback implements Handler.Callback {

    private final Activity activity;
    ShareCallback(Activity a) {
        activity = a;
    }

    @Override
    public boolean handleMessage(Message message) {
        int width = message.arg1;
        int height = message.arg2;
        int[] colors = new int[width * height];
        byte[] imageArray = ((ByteBuffer) message.obj).array();
        for (int row = 0; row < height; ++row) {
            for (int col = 0; col < width; ++col) {
                int cell = row * width + col;
                colors[(height - 1 - row) * width + col] = 0xff000000
                        | ((imageArray[4 * cell] & 0xff) << 16)
                        | ((imageArray[4 * cell + 1] & 0xff) << 8)
                        | (imageArray[4 * cell + 2] & 0xff);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextAlign(Paint.Align.CENTER);
        float density = activity.getResources().getDisplayMetrics().density;
        paint.setTextSize(20.0f * density); // ~20dp
        canvas.drawText("Orbital Explorer", width / 2.0f, height - 20.0f * density, paint);

        int name = (int) (System.currentTimeMillis() % 0x10000L);
        File file = new File(activity.getCacheDir(), "screens/" + name + ".jpg");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException ignored) {
            File parent = file.getParentFile();
            if (!parent.mkdirs()) {
                shareError(name);
                return true;
            }
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ignore) {
                shareError(name);
                return true;
            }
        }
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
            shareError(name);
            return true;
        }
        try {
            fileOutputStream.close();
        } catch (IOException ignored) {
            shareError(name);
            return true;
        }

        Uri shareUri = FileProvider
                .getUriForFile(activity, "com.gputreats.orbitalexplorer.provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, shareUri);
        intent.setType("image/jpeg");
        if (activity.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            shareError(name);
            return true;
        }
        Intent chooser = Intent.createChooser(intent, "Share image with");
        activity.startActivityForResult(chooser, name);

        return true;
    }

    private void shareError(int name) {
        Toast.makeText(activity, "Unable to share", Toast.LENGTH_SHORT).show();
        File file = new File(activity.getCacheDir(), "screens/" + name + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
