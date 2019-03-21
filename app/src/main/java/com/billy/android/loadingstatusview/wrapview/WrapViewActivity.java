package com.billy.android.loadingstatusview.wrapview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.billy.android.loadingstatusview.R;

public class WrapViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_view);
    }

    public void toLoadFailed(View view) {
        startActivity(new Intent(this, WrapSingleViewActivity.class));
    }

    public void toMultiLoadingView(View view) {
        startActivity(new Intent(this, WrapScrollViewItemActivity.class));
    }

    public void toGridViewLoadingView(View view) {
        startActivity(new Intent(this, WrapGridViewItemActivity.class));
    }

    public void toRecyclerViewLoadingView(View view) {
        startActivity(new Intent(this, WrapRecyclerViewItemActivity.class));
    }
}
