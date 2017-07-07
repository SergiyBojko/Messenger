package com.rammstein.messenger.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.StringSignature;
import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.AppUser;
import com.rammstein.messenger.model.local.UserDetails;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 19.06.2017.
 */

public class GlideHelper {
    private static String url = "http://andriidemkiv-001-site1.dtempurl.com/api/images/";
    public static void loadAvatar(Context context, ImageView target, UserDetails userDetails) {
        String imageUrl = url + userDetails.getId();
        AppUser appUser = RealmHelper.getCurrentUser();
        GlideUrl glideUrl = new GlideUrl(
                imageUrl,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", appUser.getAccessToken())
                        .build());
        Drawable placeholder = context.getResources().getDrawable(R.mipmap.ic_default_profile);
        Glide
                .with(context)
                .load(glideUrl)
                .crossFade()
                .placeholder(placeholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .signature(new StringSignature(Long.toString(userDetails.getLastModif())))
                .into(target);
    }

    public static void loadPlceholder(Context context, ImageView target) {
        Drawable placeholder = context.getResources().getDrawable(R.mipmap.ic_default_profile);
        Glide
                .with(context)
                .load(R.mipmap.ic_default_profile)
                .crossFade()
                .placeholder(placeholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(target);
    }
}
