package sarahtucker.college.com.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


/*import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;*/
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import android.support.v4.widget.SwipeRefreshLayout;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;
import android.webkit.JavascriptInterface;

public class MainActivityNew extends Activity {
    Handler mHandler = new Handler();//In UI Thread
    SwipeRefreshLayout swipeToRefresh;
    CustomWebView mWebView;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    //TransparentProgressDialog dialog ;
    AlertDialog alertDialog=null;
    View dialogView;
    AlertDialog.Builder builder;
    Button btn_no,btn_yes;
    Boolean isRefreshing=false;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeToRefresh.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged()
                    {
                        Log.d("yyyyyy","yyyyy- "+mWebView.getScrollY());
                        Log.d("yyyyyy","xxxxx- "+mWebView.getScrollX());
                        if (mWebView.getScrollY() == 0) {
                            Log.d("yyyyyy","yyyyy "+mWebView.getScrollY());
                            Log.d("yyyyyy","xxxxx "+mWebView.getScrollX());
                            swipeToRefresh.setEnabled(true);
                        }
                        else
                            swipeToRefresh.setEnabled(false);
                    }
                });
    }

    @Override
    public void onStop() {
        swipeToRefresh.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(                "sdsd","MainActivityNew22222222222222222222222"        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE)
            {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
/*            dialog = new TransparentProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);*/
            swipeToRefresh=(SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
            swipeToRefresh.setColorSchemeResources(R.color.colorAccent);

            swipeToRefresh.setProgressViewOffset(false,
                    getResources().getDimensionPixelSize(R.dimen.refresher_offset),
                    getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));

            mWebView= (CustomWebView) findViewById(R.id.webView_main);
            //mWebView.setBackgroundColor(Color.parseColor("#0000ff"));
            mWebView.setBackgroundResource(R.drawable.img);

            swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    mWebView.clearCache(true);
                    mWebView.reload();
                }
            });

            WebSettings webSettings = mWebView.getSettings();
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);

            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            mWebView.addJavascriptInterface(new WebAppInterface(), "Android");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            mWebView.clearCache(false);

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("upi:")) {    //To allow link which starts with upi://
                        Intent intent = new Intent(Intent.ACTION_VIEW);  // To show app chooser
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    //dialog.dismiss();
                    swipeToRefresh.setRefreshing(false);
                    mWebView.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('androidhide')[0].style.display='none'; })()");
                    swipeToRefresh.setVisibility(View.VISIBLE);
                    super.onPageFinished(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    //dialog.show();
                    super.onPageStarted(view, url, favicon);
                }
/*                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed(); // Ignore SSL certificate errors
                }*/
            });
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl("https://portal.sarahtuckercollege.edu.in/mob/");
            //mWebView.loadUrl("https://www.amazon.in/");
            //mWebView.loadUrl("https://www.snapdeal.com/");
            //mWebView.loadUrl("https://sarahtuckercollege.edu.in/");

        }catch (Exception er)
        {
            Log.d("ffff ","Errro r "+er);
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event)
    {
        String webUrl = mWebView.getUrl();
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(mWebView.canGoBack()){
/*                            if((webUrl.contains("url")))
                            {
*//*                                new AlertDialog.Builder(this).setTitle("Alert!")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage("Are you sure you want to exit the app?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                intent.addCategory(Intent.CATEGORY_HOME);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                //finish();
                                            }
                                        }).setNegativeButton("no", null).show();*//*
                                //alertDialogOption();
                            }
                            else*/
                        if((webUrl.contains("url"))){
                            Toast.makeText(this, "Press the X button.",Toast.LENGTH_SHORT).show();
                        }
                        else if((webUrl.contains("url")||(webUrl.contains("file:///android_asset/error_page.html")||webUrl.contains("url"))))
                        {
/*                                new AlertDialog.Builder(this).setTitle("Alert!")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage("Are you sure you want to exit the app?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                                intent.addCategory(Intent.CATEGORY_HOME);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                //finish();
                                            }
                                        }).setNegativeButton("no", null).show();*/
                            //alertDialogOption();
                        }else {
                            mWebView.goBack();
                        }
                    }else {
/*                            new AlertDialog.Builder(this).setTitle("Alert!")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("Are you sure you want to exit the app?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            //finish();
                                        }
                                    }).setNegativeButton("no", null).show();*/
                        //alertDialogOption();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebAppInterface {
        @JavascriptInterface
        public String getInstalledUPIApps() {
            PackageManager pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("upi://pay"));
            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

            StringBuilder installedApps = new StringBuilder();
            for (ResolveInfo resolveInfo : resolveInfos) {
                installedApps.append(resolveInfo.activityInfo.packageName).append("\n");
            }
            return installedApps.toString();
        }
    }

/*    void alertDialogOption()
    {
        constantvalues.bForCloseApplication=false;
        CustomDialog cdd = new CustomDialog(this);
        cdd.show();

        cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                if(constantvalues.bForCloseApplication)
                {
                    //Close/Minimize application
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //finish();
                }
            }
        });
    }*/


/*    private void showCustomDialog()
    {


        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);
        //then we will inflate the custom alert dialog xml that we created
        //View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert, viewGroup, false);
        dialogView = LayoutInflater.from(this).inflate(R.layout.layout_alertnew, viewGroup, false);
        btn_no=(Button)dialogView.findViewById(R.id.btn_no);
        btn_yes=(Button)dialogView.findViewById(R.id.btn_yes);

        //Now we need an AlertDialog.Builder object
        builder = new AlertDialog.Builder(this);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();

        btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });
    }*/

/*    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

    }*/

}
