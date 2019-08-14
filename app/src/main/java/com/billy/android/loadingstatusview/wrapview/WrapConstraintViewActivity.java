package com.billy.android.loadingstatusview.wrapview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.BaseActivity;
import com.billy.android.loadingstatusview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.billy.android.loadingstatusview.util.Util.getErrorImage;
import static com.billy.android.loadingstatusview.util.Util.getRandomImage;

/**
 * <pre>
 *     author : JX
 *     e-mail : liyayu@ffde.com
 *     time   : 2019/08/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WrapConstraintViewActivity extends BaseActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_constraint);
        imageView = findViewById(R.id.iv_awc_show);
        loadData(getErrorImage());
    }

    @Override
    protected void initLoadingStatusViewIfNeed() {
        //override this method in subclass to do special initialization
        if (mHolder == null) {
            //wrap view with default loading status view
            mHolder = Gloading.getDefault().wrap(imageView).withRetry(new Runnable() {
                @Override
                public void run() {
                    onLoadRetry();
                }
            });
        }
    }

    private void loadData(String picUrl) {
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
        //change picture url to a correct one
        loadData(getRandomImage());
    }

}
