package com.billy.android.loadingstatusview.wrapview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.billy.android.loading.Gloading;
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
 *      bind status view to all item views in GridView (All AdapterView are the same)
 * @author billy.qi
 * @since 19/3/19 23:20
 */
public class WrapGridViewItemActivity extends Activity {

    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GridView gridView = new GridView(this);
        gridView.setNumColumns(2);
        setContentView(gridView);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = dm.widthPixels >> 1;
        gridView.setAdapter(new MyAdapter(initData()));
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

    class MyAdapter extends BaseAdapter {
        List<String> list;

        MyAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                Gloading.Holder holder = Gloading.getDefault().wrap(imageView);
                viewHolder = new ViewHolder(holder, imageView);
                //use wrapper view instead of origin imageView
                convertView = holder.getWrapper();
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.showImage(list.get(position));
            return convertView;
        }
        class ViewHolder implements Runnable {
            private final Gloading.Holder holder;
            ImageView imageView;
            private String curUrl;

            ViewHolder(Gloading.Holder holder, ImageView imageView) {
                this.imageView = imageView;
                this.holder = holder;
                this.holder.withRetry(this);
            }

            void showImage(String url) {
                curUrl = url;
                holder.showLoading();
                Glide.with(WrapGridViewItemActivity.this)
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

}
