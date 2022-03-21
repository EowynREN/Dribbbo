package com.yuanren.dribbbo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.yuanren.dribbbo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageUtils {

    public static void loadUserPicture(@NonNull final Context context, String url, final ImageView imageView){
        Picasso.with(context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_picture_placeholder))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        imageView.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        // set default user image if user didn't upload profile
                        imageView.setImageResource(R.drawable.user_picture_placeholder);
                    }
                });
    }

    public static void loadShotImage(@NonNull Context context, String url, ImageView imageView){
        Picasso.with(context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.user_picture_placeholder))
                .into(imageView);
    }
}
