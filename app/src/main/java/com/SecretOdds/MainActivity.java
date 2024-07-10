package com.SecretOdds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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


import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.camelsurvey.R;
import com.onesignal.OneSignal;

import im.delight.android.webview.AdvancedWebView;

import java.util.Locale;

public class MainActivity extends Activity implements AdvancedWebView.Listener {

    private String lastUrl="";
    public static final long MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L;
    private AdvancedWebView mWebView;
    // public ProgressBar progressBar;
    String loadUrl="https://camelsurvey.com/?utm_campagin=camel_app";


    //View ll_pView, pView;
    //SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    private CardView headerLayout;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private static final String ONESIGNAL_APP_ID = "bedb2017-ad3a-4670-69-3a76647455";

    private static final String URL = "https://camelsurvey.com/";
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

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setContentView(R.layout.activity_main);
        //pd = new ProgressDialog(this, R.style.pdtheme);
//        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        pd.setCancelable(false);
//        pd.show();

        setContentView(R.layout.activity_main);


        //OneSignal.startInit(this).init();
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();

        String deviceLanguage = Locale.getDefault().getLanguage();
        if (deviceLanguage.equals("en")) {
            loadUrl = "https://camelsurvey.com/?utm_campagin=camel_app";
        } else if (deviceLanguage.equals("fr")) {
            loadUrl = "https://camelsurvey.com/fr/";
        } else if (deviceLanguage.equals("it")) {
            loadUrl = "https://camelsurvey.com/it/";
        } else if (deviceLanguage.equals("pt")) {
            loadUrl = "https://camelsurvey.com/pt/";
        } else if (deviceLanguage.equals("es")) {
            loadUrl = "https://camelsurvey.com/es/";
        }else if(deviceLanguage.equals("in")){
            loadUrl = "https://camelsurvey.com/id/";
        }
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
                if(url.startsWith("https://camelsurvey.com/")){
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
        if(!lastUrl.startsWith("https://camelsurvey.com/")){
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
                mWebView.loadUrl("https://camelsurvey.com/surveys/");
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

}



