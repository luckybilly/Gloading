# Gloading

[中文文档](README-zh-CN.md)

Show global loading status view in a low coupling way for Android App.

[JavaDocs](https://luckybilly.github.io/Gloading/) | [Download Demo](https://github.com/luckybilly/Gloading/raw/master/demo.apk)

Latest Version: [![Download](https://api.bintray.com/packages/hellobilly/android/gloading/images/download.svg)](https://bintray.com/hellobilly/android/gloading/_latestVersion)

Lightweight: aar is less than 6KB, just 170 code lines and 104 comment lines within only 1 java file.

Design as Adapter pattern,with good compatibility: most third-party LoadingViews can be used as Gloading views in the Adapter

## Demo

Wrap activity page

Load success|Load failed and click retry|Load success with empty data|This loading status UI is special
:---:|:---:|:---:|:---:
<img src="image/en_load_success.gif" width="200" />|<img src="image/en_load_failed.gif" width="200" />|<img src="image/en_load_empty_data.gif" width="200" />|<img src="image/en_load_special_loading.gif" width="200" />

Wrap view(s)

Wrap single view|Wrap views|Wrap in GridView|Wrap in RecyclerView<br>No words below
:---:|:---:|:---:|:---:
<img src="image/en_wrap_single_view.gif" width="200" />|<img src="image/en_wrap_scroll_view.gif" width="200" />|<img src="image/en_wrap_grid_view.gif" width="200" />|<img src="image/en_wrap_recycler_view.gif" width="200" />


## Usage

```groovy
compile 'com.billy.android:gloading:1.0.0'
```

#### 1. Provide global loading status views

For global usage, create an Adapter to provide views for all status via getView(...) method 

Note: Activity/Fragment/View reused in 2 or more apps with different loading status views? 

~~~
Just provide a different Adapter for each app.
No need to change any usage code
~~~ 

demo

```java
public class GlobalAdapter implements Gloading.Adapter {
    @Override
    public View getView(Gloading.Holder holder, View convertView, int status) {
        GlobalLoadingStatusView loadingStatusView = null;
        //reuse the old view, if possible
        if (convertView != null && convertView instanceof GlobalLoadingStatusView) {
            loadingStatusView = (GlobalLoadingStatusView) convertView;
        }
        if (loadingStatusView == null) {
            loadingStatusView = new GlobalLoadingStatusView(holder.getContext(), holder.getRetryTask());
        }
        loadingStatusView.setStatus(status);
        return loadingStatusView;
    }
    
    class GlobalLoadingStatusView extends RelativeLayout {

        public GlobalLoadingStatusView(Context context, Runnable retryTask) {
            super(context);
            //init view ...
        }
        
        public void setStatus(int status) {
            //change ui by different status...
        }
    }
}
```
See [demo code](app/src/main/java/com/billy/android/loadingstatusview/wrapactivity/adapter/GlobalAdapter.java)

#### 2. Init Gloading by Adapter before use it
```java
Gloading.initDefault(new GlobalAdapter());
```

Note: Use [AutoRegister](https://github.com/luckybilly/AutoRegister) to decoupling this step.

#### 3. Show global loading status views in all pages

3.1 Wrap something and return a Gloading.Holder object

```java
//Gloading wrapped whole activity, wrapper view: android.R.id.content
Gloading.Holder holder = Gloading.getDefault().wrap(activity);

//with load failed retry task
Gloading.Holder holder = Gloading.getDefault().wrap(activity).withRetry(retryTask);
```

or

```java
//Gloading will create a FrameLayout to wrap it
Gloading.Holder holder = Gloading.getDefault().wrap(view);

//with load failed retry task
Gloading.Holder holder = Gloading.getDefault().wrap(view).withRetry(retryTask);
```

3.2 Show status views for loading/loadFailed/empty/... by Gloading.Holder

```java
//show loading status view by holder
holder.showLoading() 

//show load success status view by holder (frequently, hide gloading)
holder.showLoadSuccess()

//show load failed status view by holder (frequently, needs retry task)
holder.showFailed()

//show empty status view by holder. (load completed, but data is empty)
holder.showEmpty()
```

More [Gloading.Holder APIs](https://luckybilly.github.io/Gloading/com/billy/android/loading/Gloading.Holder.html)

## Practice

### 1. Wrap into BaseActivity/BaseFragment

```java
public abstract class BaseActivity extends Activity {

    protected Gloading.Holder mHolder;

    /**
     * make a Gloading.Holder wrap with current activity by default
     * override this method in subclass to do special initialization
     */
    protected void initLoadingStatusViewIfNeed() {
        if (mHolder == null) {
            //bind status view to activity root view by default
            mHolder = Gloading.getDefault().wrap(this).withRetry(new Runnable() {
                @Override
                public void run() {
                    onLoadRetry();
                }
            });
        }
    }

    protected void onLoadRetry() {
        // override this method in subclass to do retry task
    }

    public void showLoading() {
        initLoadingStatusViewIfNeed();
        mHolder.showLoading();
    }

    public void showLoadSuccess() {
        initLoadingStatusViewIfNeed();
        mHolder.showLoadSuccess();
    }

    public void showLoadFailed() {
        initLoadingStatusViewIfNeed();
        mHolder.showLoadFailed();
    }

    public void showEmpty() {
        initLoadingStatusViewIfNeed();
        mHolder.showEmpty();
    }

}
```

### 2. Call super methods inside subclasses

```java

public class GlobalFailedActivity extends BaseActivity {
    private ImageView imageView;
    private String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do sth init...
        loadData();
    }

    private void loadData() {
        showLoading();
        loadDataAndCallback(new Callback() {
        	public void success(Data data) {
        		if (isEmpty(data)) {
        			showEmpty();
        		} else {
        			//do sth with data...
        			showLoadSuccess();
        		}
        	}
        	public void failed() {
        		//do sth...
        		showLoadFailed();
        	}
        });
    }

    @Override
    protected void onLoadRetry() {
        loadData();
    }
    
    //other codes...
}

```


## Debug mode

```java
//debug mode. if set true, logs will print into logcat
Gloading.debug(trueOrFalse);
```

## Thanks

Pictures in demo app all from: https://www.thiswaifudoesnotexist.net/