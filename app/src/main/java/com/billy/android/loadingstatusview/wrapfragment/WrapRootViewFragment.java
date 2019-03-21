package com.billy.android.loadingstatusview.wrapfragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.BaseActivity;
import com.billy.android.loadingstatusview.util.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.billy.android.loadingstatusview.util.Util.getRandomImage;

/**
 * demo for wrap fragment
 *
 * You can wrap a BaseFragment like {@link BaseActivity}
 *
 * @author billy.qi
 * @since 19/3/21 17:23
 */
public class WrapRootViewFragment extends Fragment {
    private Gloading.Holder holder;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        imageView = new ImageView(inflater.getContext());
        holder = Gloading.getDefault().wrap(imageView).withRetry(new Runnable() {
            @Override
            public void run() {
                //change picture url to a correct one
                loadImage(getRandomImage());
            }
        });
        //demo load failed with an error image url
        loadImage(Util.getErrorImage());
        return holder.getWrapper();
    }

    private void loadImage(String picUrl) {
        holder.showLoading();
        Glide.with(this)
                .load(picUrl)
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
