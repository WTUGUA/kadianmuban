package com.novv.dzdesk.loader;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import java.io.File;

public class GlideBannerLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path instanceof String) {
            Glide.with(context).load((String) path).into(imageView);
        } else if (path instanceof File) {
            Glide.with(context).load((File) path).into(imageView);
        }
    }
}
