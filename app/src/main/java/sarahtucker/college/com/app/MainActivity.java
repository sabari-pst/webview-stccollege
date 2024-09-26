package sarahtucker.college.com.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Bundle;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
//import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements  View.OnClickListener{

    WebView wv_main;
    ProgressDialog dialog = null;

    Button btn_retry;

 //   String sMainUrl="https://portal.sarahtuckercollege.edu.in/mob/";
 String sMainUrl="https://portal.sarahtuckercollege.edu.in/mob/";

    Stack stack_url=new Stack();

    //File myDir = new File("/data/data/web.faith.vedicadmin/files/"+"/myPDF");

    File myDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).toString()+"/myPDF");
    private static final int REQUEST = 112;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public Uri imageUri;

    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(                "sdsd","MainActivity1111111111111111111"        );

        try
        {
            if (isNetworkAvailable())
            {
                //withInternet();
                try {
                    setContentView(R.layout.activity_main);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    wv_main = (WebView) findViewById(R.id.webView_main);

                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setMessage("Please Wait...");
                    dialog.setCanceledOnTouchOutside(false);

                    wv_main.getSettings().setJavaScriptEnabled(true);
                    //                Using setJavaScriptEnabled can introduce XSS vulnerabilities into you application, review carefully

                    wv_main.getSettings().setAllowFileAccess(true);
                    wv_main.getSettings().setDomStorageEnabled(true);
                    wv_main.getSettings().setAllowContentAccess(true);
                    wv_main.getSettings().setAllowFileAccessFromFileURLs(true);
                    wv_main.getSettings().setAllowFileAccess(true);
                    wv_main.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    wv_main.setWebViewClient(new myWebClient());


                    wv_main.setWebChromeClient(new WebChromeClient() {

                        // openFileChooser for Android 3.0+
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){

                            // Update message
                            mUploadMessage = uploadMsg;

                            try{

                                // Create AndroidExampleFolder at sdcard

                                File imageStorageDir = new File(
                                        Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES)
                                        , "AndroidExampleFolder");

                                if (!imageStorageDir.exists()) {
                                    // Create AndroidExampleFolder at sdcard
                                    imageStorageDir.mkdirs();
                                }

                                // Create camera captured image file path and name
                                File file = new File(
                                        imageStorageDir + File.separator + "IMG_"
                                                + String.valueOf(System.currentTimeMillis())
                                                + ".jpg");

                                mCapturedImageURI = Uri.fromFile(file);

                                // Camera capture image intent
                                final Intent captureIntent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);

                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                i.setType("image/*");

                                // Create file chooser intent
                                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                                // Set camera intent to file chooser
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                                        , new Parcelable[] { captureIntent });

                                // On select image call onActivityResult method of activity
                                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                            }
                            catch(Exception e){
                                Toast.makeText(getBaseContext(), "Exception:"+e,
                                        Toast.LENGTH_LONG).show();
                            }

                        }

                        // openFileChooser for Android < 3.0
                        public void openFileChooser(ValueCallback<Uri> uploadMsg){
                            openFileChooser(uploadMsg, "");
                        }

                        //openFileChooser for other Android versions
                        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                                    String acceptType,
                                                    String capture) {

                            openFileChooser(uploadMsg, acceptType);
                        }
                        public boolean onConsoleMessage(ConsoleMessage cm) {

                            onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                            return true;
                        }

                        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                            Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

                        }
                    });   // End setWebChromeClient



                    wv_main.setDownloadListener(new DownloadListener() {
                        @Override
                        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                            //start download
                            DownloadPDF downloadPDF = new DownloadPDF();
                            downloadPDF.execute(url,userAgent,contentDisposition);
                        }
                    });

                   // wv_main.loadUrl(sMainUrl);

                    if (!hasPermissions(MainActivity.this, PERMISSIONS))
                    {
                        Log.d("hhh ","false");
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST );
                    }
                    else
                        Log.d("hhh ","true");

                }
                catch (Exception er)
                {
                    Log.e("eee",""+er);
                }
            }

            else
            {
                //Toast.makeText(this, "Internet Not Available...", Toast.LENGTH_LONG).show();
                InternetErrorPage();
            }
        }
        catch (Exception er)
        {
            Log.e("eee",""+er);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        wv_main.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        wv_main.restoreState(savedInstanceState);
    }


    void withInternet()
    {
        try {
            setContentView(R.layout.activity_main);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            wv_main = (WebView) findViewById(R.id.webView_main);

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);


            // Using setJavaScriptEnabled can introduce XSS vulnerabilities into you application, review carefully

            wv_main.getSettings().setAllowFileAccess(true);
            wv_main.getSettings().setDomStorageEnabled(true);
            wv_main.getSettings().setAllowContentAccess(true);
            wv_main.getSettings().setAllowFileAccessFromFileURLs(true);
            wv_main.getSettings().setAllowFileAccess(true);
            wv_main.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv_main.getSettings().setJavaScriptEnabled(true);
            wv_main.setWebViewClient(new myWebClient());

            wv_main.setWebChromeClient(new WebChromeClient() {

                // openFileChooser for Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){

                    // Update message
                    mUploadMessage = uploadMsg;

                    try{

                        // Create AndroidExampleFolder at sdcard

                        File imageStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                , "AndroidExampleFolder");

                        if (!imageStorageDir.exists()) {
                            // Create AndroidExampleFolder at sdcard
                            imageStorageDir.mkdirs();
                        }

                        // Create camera captured image file path and name
                        File file = new File(
                                imageStorageDir + File.separator + "IMG_"
                                        + String.valueOf(System.currentTimeMillis())
                                        + ".jpg");

                        mCapturedImageURI = Uri.fromFile(file);

                        // Camera capture image intent
                        final Intent captureIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);

                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");

                        // Create file chooser intent
                        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                        // Set camera intent to file chooser
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                                , new Parcelable[] { captureIntent });

                        // On select image call onActivityResult method of activity
                        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                    }
                    catch(Exception e){
                        Toast.makeText(getBaseContext(), "Exception:"+e,
                                Toast.LENGTH_LONG).show();
                    }

                }

                // openFileChooser for Android < 3.0
                public void openFileChooser(ValueCallback<Uri> uploadMsg){
                    openFileChooser(uploadMsg, "");
                }

                //openFileChooser for other Android versions
                public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                            String acceptType,
                                            String capture) {

                    openFileChooser(uploadMsg, acceptType);
                }
                public boolean onConsoleMessage(ConsoleMessage cm) {

                    onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                    return true;
                }

                public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                    Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

                }
            });   // End setWebChromeClient

            if (Build.VERSION.SDK_INT > 8)
                wv_main.getSettings().setPluginState(WebSettings.PluginState.ON);
            wv_main.loadUrl(sMainUrl);
        }
        catch (Exception er)
        {
            Log.e("eee",""+er);
        }
    }

    void InternetErrorPage()
    {
        try
        {
            setContentView(R.layout.internetconnection);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please Wait...");

            btn_retry=(Button) findViewById(R.id.btn_retry);

            btn_retry.setOnClickListener(this);
        }
        catch (Exception er)
        {
            Log.e("eee",""+er);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_retry)
        {
            if(isNetworkAvailable())
            {
                withInternet();
            }
            else
                InternetErrorPage();
        }
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            //Log.d("uuup"," "+url);
            dialog.show();
            super.onPageStarted(view, url, favicon);
        }

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
        public void onPageFinished(WebView view, String url)
        {
            stack_url.push(url);
            super.onPageFinished(view, url);
            //progressBar.setVisibility(View.GONE);
            dialog.hide();
        }
    }

    private class DownloadPDF extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... sUrl) {
            try {
                sUrl[0]="https://portal.sarahtuckercollege.edu.in/mob/";
                //sUrl[0]="http://ministore.maduramindustries.com/admin/sales/invoice/1/90/2018-07-31/pdf";
                //Log.d("eee","uuu0 "+sUrl[0]);
                URL url = new URL(sUrl[0]);
                //Log.d("eee","uuu "+url);
                //static  final String DB_Path="/data/data/web.faith.vedicadmin/databases/";

                // create the directory if it does not exist
                if (!myDir.exists()) myDir.mkdirs();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.connect();

                //get filename from the contentDisposition
                String filename = null;
                Pattern p = Pattern.compile("\"([^\"]*)\"");
                Matcher m = p.matcher(sUrl[2]);
                while (m.find()) {
                    filename = m.group(1);
                }

                //File outputFile = new File(myDir, filename);
                File outputFile = new File(myDir, "fffrr.pdf");
                //Log.d("eee","uuu out "+outputFile.getAbsolutePath());

                InputStream input   = new BufferedInputStream(connection.getInputStream());
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                connection.disconnect();
                output.flush();
                output.close();
                input.close();


                 displayPdf("fffrr.pdf");  //a function to open the PDF file automatically

            } catch (MalformedURLException e)
            {
                //Log.d("eee","fff "+e);
                e.printStackTrace();
            } catch (IOException e) {
                //Log.d("eee","fffe "+e);
                e.printStackTrace();
            }
            return null;
        }
    }

    public void displayPdf(String filename)
    {
        try
        {
            File file = new File(myDir, filename);
            //Log.d("eee","uuu out "+file.getAbsolutePath());

           // File file = new file(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ) + "/myPDF/" + filename);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);
        }
        catch (Exception e)
        {
            Log.i("eee",e.getMessage());
        }
    }

    @Override
    public void onBackPressed()
    {
        try {
            if (sMainUrl.equalsIgnoreCase(stack_url.top()) == true)
                alertDialogOption();
            else {
                //wv_main.loadUrl(prefiousURL);
                //Log.d("-------",stack_url.top());
                stack_url.pop();
                wv_main.loadUrl(stack_url.top());
                stack_url.pop();
            }
        }
        catch (Exception er)
        {
            Log.e("ee",""+er);
        }
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void alertDialogOption()
    {
        AlertDialog.Builder adb;
        adb = new AlertDialog.Builder(this,R.style.myBackgroundStyle);
        adb.setTitle("Alert");
        adb.setMessage("Do you want to close the application?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            } });
        adb.show();
    }

    private static boolean hasPermissions(Context context, String... permissions)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
