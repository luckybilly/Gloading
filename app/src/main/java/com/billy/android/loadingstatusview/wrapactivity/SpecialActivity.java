package com.billy.android.loadingstatusview.wrapactivity;

import com.billy.android.loading.Gloading;
import com.billy.android.loadingstatusview.wrapactivity.adapter.SpecialAdapter;

/**
 * demo:
 *      use special loading UI
 *      load failed and empty status use global UI
 * @author billy.qi
 * @since 19/3/19 23:20
 */
public class SpecialActivity extends GlobalFailedActivity {

    @Override
    protected void initLoadingStatusViewIfNeed() {
        //override this method in subclass to do special initialization
        if (mHolder == null) {
            //create a special Gloading object by SpecialAdapter
            Gloading specialGloading = Gloading.from(new SpecialAdapter());
            mHolder = specialGloading.wrap(this).withRetry(new Runnable() {
                @Override
                public void run() {
                    onLoadRetry();
                }
            });
        }
    }
}
