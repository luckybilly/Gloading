package com.billy.android.loadingstatusview.wrapactivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.billy.android.loadingstatusview.BaseActivity;
import com.billy.android.loadingstatusview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.billy.android.loadingstatusview.util.Util.getRandomImage;

/**
 * demo: loading and load success (hide
 * @author billy.qi
 * @since 19/3/19 21:09
 */
public class GlobalSuccessActivity extends BaseActivity {

    private ImageView imageView;
    private String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        setContentView(imageView);
        picUrl = getRandomImage(); //a correct picture url
        loadData();
    }

    private void loadData() {
        showLoading();
        Glide.with(this)
                .load(picUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        showLoadFailed();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        showLoadSuccess();
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    protected void onLoadRetry() {
        loadData();
    }
}
