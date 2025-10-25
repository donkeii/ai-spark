package com.example.secret;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads bitmap images from the app's assets directory with a small in-memory cache.
 */
public final class AssetImageLoader {
    private static final int CACHE_SIZE_BYTES = 4 * 1024 * 1024; // 4MB
    private static final LruCache<String, Bitmap> bitmapCache = new LruCache<>(CACHE_SIZE_BYTES);

    private AssetImageLoader() {}

    public static void loadInto(ImageView target, Context ctx, String assetPath) {
        if (target == null || ctx == null || assetPath == null) return;

        Bitmap cached = bitmapCache.get(assetPath);
        if (cached != null && !cached.isRecycled()) {
            target.setImageBitmap(cached);
            return;
        }

        try (InputStream is = ctx.getAssets().open(assetPath)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565; // memory-friendly for card art
            Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
            if (bmp != null) {
                bitmapCache.put(assetPath, bmp);
                target.setImageBitmap(bmp);
            }
        } catch (IOException ignored) {
            // keep whatever image was previously set
        }
    }
}


