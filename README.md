# Gloading

Show global loading status view in a low coupling way for Android


## Usage

    JCenter is coming soon.

#### 1. Provide global loading status views

For global usage, create an Adapter to provide views for all status via getView(...) method 

(If some page reused into 2 or more apps, each app should create its own Adapter)

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

        public GlobalLoadingStatusView(Context context) {
            super(context);
            //init view ...
        }
        
        public void setStatus(int status) {
            //change ui by different status...
        }
    }
}
```

#### 2. Init Gloading by Adapter before use it
```java
Gloading.initDefault(adapter);
```

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


## Debug mode

```java
//debug mode. if set true, logs will print into logcat
Gloading.debug(trueOrFalse);
```


## More details is coming soon...
