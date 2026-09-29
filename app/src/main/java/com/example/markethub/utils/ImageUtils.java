package com.example.markethub.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.markethub.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageUtils {

    /**
     * Parse comma-separated image URIs or URLs string into a List of String URIs.
     */
    public static List<String> parseImageUris(String imageUrisString) {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(imageUrisString)) {
            String[] parts = imageUrisString.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    list.add(trimmed);
                }
            }
        }
        return list;
    }

    /**
     * Joins a list of image URI strings into a comma-separated string.
     */
    public static String joinImageUris(List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return "";
        }
        return TextUtils.join(",", uris);
    }

    /**
     * Loads image from local content URI or HTTP URL into ImageView using Glide.
     */
    public static void loadImage(Context context, String imageUriOrUrl, ImageView imageView) {
        if (context == null || imageView == null) return;

        if (TextUtils.isEmpty(imageUriOrUrl)) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        try {
            Glide.with(context)
                    .load(imageUriOrUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
