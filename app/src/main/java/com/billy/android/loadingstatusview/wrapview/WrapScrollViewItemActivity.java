package com.billy.android.loadingstatusview.wrapview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.billy.android.loadingstatusview.util.Util.getRandomImage;

/**
 * demo:
 *      bind status view to multiple specific views
 * @author billy.qi
 * @since 19/3/19 23:20
 */
public class WrapScrollViewItemActivity extends Activity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_scroll_view_item);
        container = findViewById(R.id.container);
        addImages();
    }


    private void addImages() {
        addImageView("");
        addImageView(getRandomImage());
        addImageView("http://www.no.such.website.com/no_such_pic.png");
        for (int i = 0; i < 20; i++) {
            addImageView(getRandomImage());
        }
    }

    private void addImageView(final String url) {
        final ImageView imageView = new ImageView(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
        lp.bottomMargin = 20;
        container.addView(imageView, lp);
        final Gloading.Holder holder = Gloading.getDefault().wrap(imageView);
        holder.withRetry(new Runnable() {
            @Override
            public void run() {
                loadImage(holder, imageView, url);
            }
        });
        loadImage(holder, imageView, url);
    }

    private void loadImage(final Gloading.Holder holder, final ImageView imageView, String url) {
        holder.showLoading();
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.showLoadFailed();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.showLoadSuccess();
                        return false;
                    }
                })
                .into(imageView);
    }

}
