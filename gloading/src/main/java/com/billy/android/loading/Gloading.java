package com.billy.android.loading;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 * manage loading status view<br>
 * usage:<br>
 *  //if set true, logs will print into logcat<br>
 *  Gloading.debug(trueOrFalse);<br>
 *  //init the default loading status view creator ({@link Adapter})<br>
 *  Gloading.initDefault(adapter);<br>
 *  //wrap an activity. return the holder<br>
 *  Holder holder = Gloading.getDefault().wrap(activity);<br>
 *  //wrap an activity and set retry task. return the holder<br>
 *  Holder holder = Gloading.getDefault().wrap(activity).withRetry(retryTask);<br>
 *  <br>
 *  holder.showLoading() //show loading status view by holder<br>
 *  holder.showLoadSuccess() //show load success status view by holder (frequently, hide gloading)<br>
 *  holder.showFailed() //show load failed status view by holder (frequently, needs retry task)<br>
 *  holder.showEmpty() //show empty status view by holder. (load completed, but data is empty)
 *
 * @author billy.qi
 * @since 19/3/18 17:49
 */
public class Gloading {
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_LOAD_SUCCESS = 2;
    public static final int STATUS_LOAD_FAILED = 3;
    public static final int STATUS_EMPTY_DATA = 4;
    
    private static volatile Gloading mDefault;
    private Adapter mAdapter;
    private static boolean DEBUG = false;

    /**
     * Provides view to show current loading status
     */
    public interface Adapter {
        /**
         * get view for current status
         * @param holder Holder
         * @param convertView The old view to reuse, if possible.
         * @param status current status
         * @return status view to show. Maybe convertView for reuse.
         * @see Holder
         */
        View getView(Holder holder, View convertView, int status);
    }

    /**
     * set debug mode or not
     * @param debug true:debug mode, false:not debug mode
     */
    public static void debug(boolean debug) {
        DEBUG = debug;
    }

    private Gloading() { }

    /**
     * Create a new Gloading different from the default one
     * @param adapter another adapter different from the default one
     * @return Gloading
     */
    public static Gloading from(Adapter adapter) {
        Gloading gloading = new Gloading();
        gloading.mAdapter = adapter;
        return gloading;
    }

    /**
     * get default Gloading object for global usage in whole app
     * @return default Gloading object
     */
    public static Gloading getDefault() {
        if (mDefault == null) {
            synchronized (Gloading.class) {
                if (mDefault == null) {
                    mDefault = new Gloading();
                }
            }
        }
        return mDefault;
    }

    /**
     * init the default loading status view creator ({@link Adapter})
     * @param adapter adapter to create all status views
     */
    public static void initDefault(Adapter adapter) {
        getDefault().mAdapter = adapter;
    }

    /**
     * Gloading(loading status view) wrap the whole activity
     * wrapper is android.R.id.content
     * @param activity current activity object
     * @return holder of Gloading
     */
    public Holder wrap(Activity activity) {
        ViewGroup wrapper = activity.findViewById(android.R.id.content);
        return new Holder(mAdapter, activity, wrapper);
    }

    /**
     * Gloading(loading status view) wrap the specific view.
     * @param view view to be wrapped
     * @return Holder
     */
    public Holder wrap(View view) {
        FrameLayout wrapper = new FrameLayout(view.getContext());
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            wrapper.setLayoutParams(lp);
        }
        if (view.getParent() != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(wrapper, index);
        }
        LayoutParams newLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        wrapper.addView(view, newLp);
        return new Holder(mAdapter, view.getContext(), wrapper);
    }

    /**
     * Gloading holder<br>
     * create by {@link Gloading#wrap(Activity)} or {@link Gloading#wrap(View)}<br>
     * the core API for showing all status view
     */
    public static class Holder {
        private Adapter mAdapter;
        private Context mContext;
        private Runnable mRetryTask;
        private View mCurStatusView;
        private ViewGroup mWrapper;
        private int curState;
        private SparseArray<View> mStatusViews = new SparseArray<>(4);
        private Object mData;

        private Holder(Adapter adapter, Context context, ViewGroup wrapper) {
            this.mAdapter = adapter;
            this.mContext = context;
            this.mWrapper = wrapper;
        }

        /**
         * set retry task when user click the retry button in load failed page
         * @param task when user click in load failed UI, run this task
         * @return this
         */
        public Holder withRetry(Runnable task) {
            mRetryTask = task;
            return this;
        }

        /**
         * set extension data
         * @param data extension data
         * @return this
         */
        public Holder withData(Object data) {
            this.mData = data;
            return this;
        }

        /** show UI for status: {@link #STATUS_LOADING} */
        public void showLoading() {
            showLoadingStatus(STATUS_LOADING);
        }
        /** show UI for status: {@link #STATUS_LOAD_SUCCESS} */
        public void showLoadSuccess() {
            showLoadingStatus(STATUS_LOAD_SUCCESS);
        }
        /** show UI for status: {@link #STATUS_LOAD_FAILED} */
        public void showLoadFailed() {
            showLoadingStatus(STATUS_LOAD_FAILED);
        }
        /** show UI for status: {@link #STATUS_EMPTY_DATA} */
        public void showEmpty() {
            showLoadingStatus(STATUS_EMPTY_DATA);
        }

        /**
         * Show specific status UI
         * @param status status
         * @see #showLoading()
         * @see #showLoadFailed()
         * @see #showLoadSuccess()
         * @see #showEmpty()
         */
        public void showLoadingStatus(int status) {
            if (curState == status || !validate()) {
                return;
            }
            curState = status;
            //first try to reuse status view
            View convertView = mStatusViews.get(status);
            if (convertView == null) {
                //secondly try to reuse current status view
                convertView = mCurStatusView;
            }
            try {
                //call customer adapter to get UI for specific status. convertView can be reused
                View view = mAdapter.getView(this, convertView, status);
                if (view == null) {
                    printLog(mAdapter.getClass().getName() + ".getView returns null");
                    return;
                }
                if (view != mCurStatusView || mWrapper.indexOfChild(view) < 0) {
                    if (mCurStatusView != null) {
                        mWrapper.removeView(mCurStatusView);
                    }
                    mWrapper.addView(view);
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (lp != null) {
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                } else if (mWrapper.indexOfChild(view) != mWrapper.getChildCount() - 1) {
                    // make sure loading status view at the front
                    view.bringToFront();
                }
                mCurStatusView = view;
                mStatusViews.put(status, view);
            } catch(Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }

        private boolean validate() {
            if (mAdapter == null) {
                printLog("Gloading.Adapter is not specified.");
            }
            if (mContext == null) {
                printLog("Context is null.");
            }
            if (mWrapper == null) {
                printLog("The mWrapper of loading status view is null.");
            }
            return mAdapter != null && mContext != null && mWrapper != null;
        }

        public Context getContext() {
            return mContext;
        }

        /**
         * get wrapper
         * @return container of gloading
         */
        public ViewGroup getWrapper() {
            return mWrapper;
        }

        /**
         * get retry task
         * @return retry task
         */
        public Runnable getRetryTask() {
            return mRetryTask;
        }

        /**
         *
         * get extension data
         * @param <T> return type
         * @return data
         */
        public <T> T getData() {
            try {
                return (T) mData;
            } catch(Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private static void printLog(String msg) {
        if (DEBUG) {
            Log.e("Gloading", msg);
        }
    }
}
