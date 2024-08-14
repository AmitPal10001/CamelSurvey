package com.SecretOdds;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.annotation.RequiresApi;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.webkit.GeolocationPermissions;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.camelsurvey.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import im.delight.android.webview.AdvancedWebView;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements AdvancedWebView.Listener {

    private String lastUrl="";
    public static final long MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L;
    private AdvancedWebView mWebView;

    private String baseUrl = "https://camelsurvey.com/";
    // public ProgressBar progressBar;
    String loadUrl= baseUrl+"?utm_campagin=camel_app";

    String notificationLoadUrl = baseUrl+"?from_notification=1";


    //View ll_pView, pView;
    //SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    private CardView headerLayout;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private static final String ONESIGNAL_APP_ID = "bedb2017-ad3a-4670-69-3a76647455";

    //private static final String URL = "https://com.camelsurvey/";

    //ProgressDialog pd;
    View ivSplash, ivError;
    // ImageView ivSharing;


    // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setContentView(R.layout.activity_main);
        //pd = new ProgressDialog(this, R.style.pdtheme);
//        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        pd.setCancelable(false);
//        pd.show();

        setContentView(R.layout.activity_main);


        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    updateToken(token,prefs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                    }
                });
        alarmMethod();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                request_notification_api13_permission();
            }
        }, 4000);
        // OneSignal Initialization
        if(!isScheduleExactAlarmPermissionEnabled(this)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
        }else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                Date today = new Date();
                String dateStr = prefs.getString("myDate", "");
                if(dateStr.isEmpty()){
                    prefs .edit().putString("mydate", sdf.format(today)).apply();
                    showUpdateAlert();
                }else {
                    try{
                        Date mydate = sdf.parse(dateStr);
                        long diff = mydate.getTime() - today.getTime();
                        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        if(days >= 3){
                            prefs .edit().putString("mydate", sdf.format(today)).apply();
                            showUpdateAlert();
                        }
                    } catch(Exception e){

                    }
                }
            }
        });
        //OneSignal.startInit(this).init();
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

        String deviceLanguage = Locale.getDefault().getLanguage();

        if (deviceLanguage.equals("en")) {
            loadUrl = baseUrl+"?utm_campaign=camel_app";
        } else if (deviceLanguage.equals("fr")) {
            loadUrl = baseUrl+"fr/?utm_campaign=camel_app";
        } else if (deviceLanguage.equals("it")) {
            loadUrl = baseUrl+"it/?utm_campaign=camel_app";
        } else if (deviceLanguage.equals("pt")) {
            loadUrl = baseUrl+"pt/?utm_campaign=camel_app";
        } else if (deviceLanguage.equals("es")) {
            loadUrl = baseUrl+"es/?utm_campaign=camel_app";
        } else if (deviceLanguage.equals("in")) {
            loadUrl = baseUrl+"id/?utm_campaign=camel_app";
        }


        Intent intent = this.getIntent();

        if (intent != null && intent.getExtras() != null && (intent.getExtras().containsKey("JOBID")
                || intent.getExtras().containsKey("google.delivered_priority")
                || intent.getExtras().containsKey("google.sent_time")
                || intent.getExtras().containsKey("google.ttl")
                || intent.getExtras().containsKey("google.original_priority"))) {
            Bundle bundle = intent.getExtras();
             loadUrl = notificationLoadUrl;
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//               Toast.makeText(this, "[" + key + "=" + bundle.get(key)+"]",Toast.LENGTH_LONG).show();
//            }
        }
        //Toast.makeText(this, loadUrl, Toast.LENGTH_LONG).show();
        progressBar = (ProgressBar) findViewById(R.id.prg);
        headerLayout=findViewById(R.id.headerLayout);

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.getInstance().setAcceptCookie(true);
        cookieManager.getCookie(loadUrl);

        //ll_pView = (View) findViewById(R.id.ll_pView);

        // pView = (View) findViewById(R.id.pView);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        // mSwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeToRefresh);

        ivSplash = findViewById(R.id.ivSplash);
        ivError = findViewById(R.id.ivError);
//        btnBack = (ImageView) findViewById(R.id.btnBack);
//        btnForward = (ImageView) findViewById(R.id.Forward);


        //findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
        //     mWebView.loadUrl("https://www.mobochanic.com/work-order.html");
        //  }
        //});
//        btnhome1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWebView.goBack();
//            }
//        });

        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goBack();
            }
        });

//        findViewById(R.id.btnMarket).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWebView.loadUrl("https://www.tchatchoulondon.com/pages/faqs");
//            }
//        });


        findViewById(R.id.btnhome1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("https://www.e-flexer.nl/hanflex/index.ewb");
            }
        });

//        findViewById(R.id.btnNEWS).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWebView.loadUrl("https://www.tchatchoulondon.com/pages/contact-us");
//            }
//        });

        findViewById(R.id.Forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goForward();
            }
        });


//        btnForward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWebView.goForward();
//            }
//        });
        //ivSharing = (ImageView) findViewById(R.id.ivSharing);
        //ivSharing.setOnClickListener(new View.OnClickListener() {

        //  @Override
        //  public void onClick(View arg0) {
        //    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        //  sharingIntent.setType("text/plain");
        //  sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mWebView.getTitle());
        //  sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mWebView.getUrl());
        //  startActivity(Intent.createChooser(sharingIntent, "Share URL via"));
        // }
        //  });


        mWebView = (AdvancedWebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        //mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //  mWebView.getSettings(). setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        //mWebView.getSettings().setUserAgentString("Web App");
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // mWebView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        //mWebView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        mWebView.getSettings().setDatabaseEnabled(true);
        //mWebView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
        mWebView.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        mWebView.setClickable(true);
        //  mWebView.getSettings().setUseWideViewPort(true);
        // webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        // mWebView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 );
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        if (!isNetworkAvailable()) { // loading offline
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        mWebView.setWebChromeClient(new WebChromeClient());
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);


        mWebView.setWebChromeClient(new WebChromeClient());
        //mWebView.setWebChromeClient(new MyChrome());
//        mWebView.loadUrl("https://coolpeoplenetwork.us/?p=videos");

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        mWebView.setListener(this, this);
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.i("onDownloadStart", "Requesturl::" +url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);


            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            @RequiresApi(api = Build.VERSION_CODES.N)

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("setWebChroverride", "Requesturl::" +view.getUrl());
                if ((String.valueOf(request.getUrl())).contains("external=true")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    view.getContext().startActivity(intent);
                    return true;
                } else {
                    view.loadUrl(String.valueOf(request.getUrl()));
                }

                return true;
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;


            public Bitmap getDefaultVideoPoster() {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView() {
                ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }


            @Override
            public void onProgressChanged(WebView view, int progress) {
                // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pView.getLayoutParams());
                // lp.weight = progress;
                // pView.setLayoutParams(lp);

                // ll_pView.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);


//                if (progress == 100)
//                    progressBar.dismiss();
//                else
//                    progressBar.show();

                checkNavigations();

            }

            //

            //mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        this.mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("shoul", "Requesturl::" + request.getUrl());
                if (request.getUrl().toString().equalsIgnoreCase("https://www.amazon.com/")) {
                    //DO SOMETHING
                } else {
                    //DO SOMETHING
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(view.GONE);
                Log.i("onPageFinished", "Requesturl::" + view.getUrl());
                Log.i("onPageFinished url", "Requesturl::" +url);
//                view.loadUrl("javascript:(function() { " +
//                        "document.getElementsByClassName('flex-row container')[0].style.display='none'; })()");
//                super.onPageFinished(view, url);
//                view.loadUrl("javascript:(function() { " +
//                        "document.getElementsByClassName('menu-item menu-item-type-post_type menu-item-object-page menu-item-has-children menu-item-1840 has-child')[0].style.display='none'; })()");
//                super.onPageFinished(view, url);
//
//                view.loadUrl("javascript:(function() { " +
//                        "document.getElementsByClassName('menu-item menu-item-type-post_type menu-item-object-page menu-item-home current-menu-item page_item page-item-1722 current_page_item menu-item-1833')[0].style.display='none'; })()");
//                super.onPageFinished(view, url);
//
//                view.loadUrl("javascript:(function() { " +
//                        "document.getElementsByClassName('account-item has-icon menu-item')[0].style.display='none'; })()");
//                super.onPageFinished(view, url);

                view.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('cli-bar-popup cli-modal-content')[0].style.display='none'; })()");
                super.onPageFinished(view, url);


//                mSwipeRefreshLayout.setOnRefreshListener(
//                        new SwipeRefreshLayout.OnRefreshListener() {
//                            @Override
//                            public void onRefresh() {
//                                mWebView.reload();
//                            }
//                        }
//                );

                //ll_pView.setVisibility(View.GONE);
                // mSwipeRefreshLayout.setRefreshing(false);
                //progressBar.dismiss();
                Log.i("onPageFinished end", "Requesturl::" +url);
                lastUrl=url;
                checkNavigations();
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("onPageStarted", "Requesturl::" +view.getUrl());
                Log.i("onPageStarted url", "Requesturl::" +url);
                lastUrl=url;
                String strArr[]=lastUrl.split("\\?");
                Log.i("onPageStarted split", "Requesturl::" +strArr[0]);
                if(url.startsWith(baseUrl)){
                    headerLayout.setVisibility(View.GONE);
                }else {
                    headerLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(view.VISIBLE);


            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("shouldOver", "Requesturl::" +view.getUrl());
                Log.i("shouldOver url", "Requesturl::" +url);

                if (url.startsWith("tel:") || url.startsWith("geo:") || url.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    if (url != null && url.startsWith("tel:0293322011")) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;

                    }
                    if (url != null && url.startsWith("tel:0293322499")) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;

                    }


                    if (url != null && url.startsWith("mailto:info@kirketon.com.au")) {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://" + url;
                        }
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;

                    }

                    startActivity(intent);
                    view.reload();
                    return true;
                }

                view.loadUrl(url);
                return true;
            }

            //            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // TODO Auto-generated method stub
//
//
//
//                if(url != null && url.startsWith("sms://+4917637686367") )
//                {
//                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
//                    return true;
//
//                }else
//                if(url != null && url.startsWith("whatsapp://send/?phone=4917637686367") )
//                {
//                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
//                    return true;
//
//                }else
//                if(url != null && url.startsWith("mailto:info@taximuneeb.de") )
//                {
//                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
//                    return true;
//
//                }else
//                {
//                    return false;
//                }
//            }
//            ;
            //mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()*/ {
            //@Override
            public void onRefresh() {
                mWebView.reload();
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.loadUrl(loadUrl);
                    //progressBar = ProgressDialog.show(MainActivity.this, "", "Loading...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ivSplash.setVisibility(View.GONE);
            }
        }, 3000);


        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    OnEverySecond();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();


//        PeriodicWorkRequest saveRequest =
//                new PeriodicWorkRequest.Builder(PeriodicWorker.class, MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.HOURS)
//                        .build();
//        WorkManager.getInstance().enqueue(saveRequest);

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class JavaScriptInterface {

        /**
         * this should be triggered when user and pwd is correct, maybe after
         * successful login
         */
        public void saveValues(String usr, String pwd) {

            if (usr == null || pwd == null) {
                return;
            }

            //save the values in SharedPrefs
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putString("usr", usr);
            editor.putString("pwd", pwd);
            editor.apply();
        }
    }

    public static boolean isScheduleExactAlarmPermissionEnabled(final Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED);
    }
    private void request_notification_api13_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                        .putExtra(Settings.EXTRA_CHANNEL_ID, "notification_channel");
                startActivity(settingsIntent);
            }else if (this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS}, 22);
            }else{
//                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
//                        .putExtra(Settings.EXTRA_CHANNEL_ID, "notification_channel");
//                startActivity(settingsIntent);
            }
        }
    }
    public boolean IsNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            // boolean isWiFi = info.getType() == ConnectivityManager.TYPE_WIFI;
            return info != null && info.getState() == NetworkInfo.State.CONNECTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void OnEverySecond() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IsNetworkAvailable()) {
                    ivError.setVisibility(View.GONE);
                } else {
                    ivError.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void handleURL(String url) {
        if (url.startsWith("tel:") || url.startsWith("geo:") || url.startsWith("mailto:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

            return;
        } else
            mWebView.loadUrl(url);
        checkNavigations();
    }

    private void checkNavigations() {
        //btnBack.setVisibility(mWebView.canGoBack() ? View.VISIBLE : View.GONE);
        //btnForward.setVisibility(mWebView.canGoForward() ? View.VISIBLE : View.GONE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.i("onPageStarted", "advace::" + url);
    }

    @Override
    public void onPageFinished(String url) {
        Log.i("onPageStarted", "advace::" + url);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @Override
    public void onBackPressed() {
        if(!lastUrl.startsWith(baseUrl)){
           showExitDialog();
        }else {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void showExitDialog() {
        Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_back);
        dialog.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                headerLayout.setVisibility(View.GONE);
                mWebView.loadUrl(baseUrl+"surveys/");
            }
        });
        dialog.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void showUpdateAlert(){
        new AlertDialog.Builder(this)
                .setTitle("App Update")
                .setMessage("Please install new version from playstore")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private Notification getNotification(){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Reminder")
                .setContentText("Car service due in 2 weeks")
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(pIntent);
        return builder.build();
    }
    private void alarmMethod() {
        Intent myIntent = new Intent(this , MainActivity.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Notification notification = getNotification();
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.NOTIFICATION_ID, 0);
        intent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
       // PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        // get current time
        Calendar currentTime = Calendar.getInstance();
        // setup time for alarm
        Calendar mondayAlarmTime = Calendar.getInstance();
        // set time-part of alarm
        mondayAlarmTime.set(Calendar.SECOND, 0);
        mondayAlarmTime.set(Calendar.MINUTE, 30);
        mondayAlarmTime.set(Calendar.HOUR, 1);
        mondayAlarmTime.set(Calendar.AM_PM, Calendar.PM);
      //  mondayAlarmTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // check if it in the future
        if (currentTime.getTimeInMillis() <  mondayAlarmTime.getTimeInMillis()) {
            // nothing to do - time of alarm in the future
        } else {
            int dayDiffBetweenClosestMonday = (7 + mondayAlarmTime.get(Calendar.DAY_OF_WEEK) - mondayAlarmTime.get(Calendar.DAY_OF_WEEK)) % 7;

            if (dayDiffBetweenClosestMonday == 0) {
                // Today is Friday, but current time after 3pm, so schedule for the next Friday
                dayDiffBetweenClosestMonday = 7;
            }
           // mondayAlarmTime.add(Calendar.DAY_OF_MONTH, dayDiffBetweenClosestMonday);
            mondayAlarmTime.add(Calendar.DATE, 1); // add one day
        }
        // calculate interval (7 days) in ms
       // int interval = 1000 * 60 * 60 * 24* 7;
        int interval = 1000 * 60 * 60;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mondayAlarmTime.getTimeInMillis(), interval, pendingIntent);

        Calendar thursdayAlarmTime = Calendar.getInstance();
        // set time-part of alarm
        thursdayAlarmTime.set(Calendar.SECOND, 0);
        thursdayAlarmTime.set(Calendar.MINUTE, 30);
        thursdayAlarmTime.set(Calendar.HOUR, 6);
        thursdayAlarmTime.set(Calendar.AM_PM, Calendar.PM);
        thursdayAlarmTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        // check if it in the future
        if (currentTime.getTimeInMillis() <  thursdayAlarmTime.getTimeInMillis()) {
            // nothing to do - time of alarm in the future
        } else {
            int dayDiffBetweenClosestThursday = (7 + thursdayAlarmTime.get(Calendar.DAY_OF_WEEK) - thursdayAlarmTime.get(Calendar.DAY_OF_WEEK)) % 7;

            if (dayDiffBetweenClosestThursday == 0) {
                // Today is Friday, but current time after 3pm, so schedule for the next Friday
                dayDiffBetweenClosestThursday = 7;
            }
            thursdayAlarmTime.add(Calendar.DAY_OF_MONTH, dayDiffBetweenClosestThursday);
        }
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, thursdayAlarmTime.getTimeInMillis(), interval, pendingIntent);
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }

    private void updateToken(String token, SharedPreferences prefs){
        System.out.println(token);

        OkHttpClient client = new OkHttpClient();
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", token)
                .addFormDataPart("country", getUserCountry(this))
                .addFormDataPart("ip_address", "192.168.0.1")
                .build();
        Request request = new Request.Builder()
                .url("https://push-collect.com/api/addSubscriber")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            assert response.body() != null;
            String responsestr = response.body().string();
            System.out.println("response "+responsestr);
            prefs.edit().putBoolean("firstrun", false).commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
//https://stackoverflow.com/questions/39194405/displaying-multiple-notifications-at-user-defined-times
//https://stackoverflow.com/questions/34517520/how-to-give-notifications-on-android-on-specific-time
//https://stackoverflow.com/questions/33821322/how-to-show-multiple-notifications-on-a-particular-day-in-different-times


