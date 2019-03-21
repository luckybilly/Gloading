package com.billy.android.loadingstatusview.wrapview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.util.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import static com.billy.android.loadingstatusview.util.Util.getErrorImage;
import static com.billy.android.loadingstatusview.util.Util.getRandomImage;

/**
 * demo:
 *      bind status view to all item views in RecyclerView
 * @author billy.qi
 * @since 19/3/19 23:20
 */
public class WrapRecyclerViewItemActivity extends Activity {


    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        setContentView(recyclerView);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = dm.widthPixels >> 1;

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerAdapter adapter = new RecyclerAdapter(initData());
        recyclerView.setAdapter(adapter);
    }

    private List<String> initData() {
        int size = 20;
        List<String> list = new ArrayList<>(size + 4);
        list.add("");//empty
        list.add(getRandomImage());
        list.add(getRandomImage());
        list.add(getErrorImage());//failed
        for (int i = 0; i < size; i++) {
            list.add(getRandomImage());
        }
        return list;
    }

    class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<String> list;

        RecyclerAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            Gloading.Holder holder = Gloading.getDefault().wrap(imageView);
            holder.withData(Constants.HIDE_LOADING_STATUS_MSG);
            return new ViewHolder(holder, imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.showImage(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements Runnable {
        private Gloading.Holder holder;
        ImageView imageView;
        private String curUrl;


        ViewHolder(Gloading.Holder holder, ImageView imageView) {
            super(holder.getWrapper());
            this.imageView = imageView;
            this.holder = holder;
            this.holder.withRetry(this);
        }

        void showImage(String url) {
            curUrl = url;
            holder.showLoading();
            Glide.with(WrapRecyclerViewItemActivity.this)
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

        @Override
        public void run() {
            showImage(curUrl);
        }
    }
}
