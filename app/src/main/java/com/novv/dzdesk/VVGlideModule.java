package com.novv.dzdesk;

import android.content.Context;
import android.support.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;

@SuppressWarnings("unused")
@GlideModule
public class VVGlideModule extends AppGlideModule {
  @Override
  public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    MemorySizeCalculator mCal = new MemorySizeCalculator.Builder(context)
        .setMemoryCacheScreens(2)
        .build();
    builder.setMemoryCache(new LruResourceCache(mCal.getMemoryCacheSize()));

    MemorySizeCalculator bCal = new MemorySizeCalculator.Builder(context)
        .setBitmapPoolScreens(3)
        .build();
    builder.setBitmapPool(new LruBitmapPool(bCal.getBitmapPoolSize()));
  }
}