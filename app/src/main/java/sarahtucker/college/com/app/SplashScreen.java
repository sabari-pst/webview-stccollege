package sarahtucker.college.com.app;
/**
 * Created by android on 4/7/2018.
 */
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
/*import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;*/
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
/*import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;*/
import androidx.appcompat.app.AppCompatDelegate;

public class SplashScreen extends AppCompatActivity
{
    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    AlertDialog dialog;

    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            Log.d("ppp","1Download else true");
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_splash);
            Log.d("ppp","2Download else true");

            Thread background = new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        Log.d("ppp","3Download else true");
                        sleep(1000);
                        Log.d("ppp","4Download else true");
                        //checkAppPermissions();
                        go_next();
                        // After 5 seconds redirect to another intent
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error :" + e,
                                Toast.LENGTH_LONG).show();
                        //&//Log.d("ppp","splash "+e.toString());
                        //&//Log.d("ppp","splash "+e.toString());
                    }
                }
            };
            // start thread
            background.start();
        }
        catch (Exception er)
        {
            Toast.makeText(getApplicationContext(), "Error :" + er, Toast.LENGTH_LONG).show();
        }
    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    public  void go_next()
    {
        Log.d("ppp1","Download else true");
        Intent intent = new Intent(SplashScreen.this, MainActivityNew.class);
        startActivity(intent);
    }

/*    public  void go_next()
    {
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(hasPermissions(SplashScreen.this,PERMISSIONS))
        {
            Intent intent = new Intent(SplashScreen.this, MainActivityNew.class);
            startActivity(intent);
        }
        else
            getPermission();
    }*/


    void getPermission()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!hasPermissions(SplashScreen.this, PERMISSIONS))
            {
                ActivityCompat.requestPermissions(SplashScreen.this, PERMISSIONS, REQUEST );
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        if(requestCode == MY_PERMISSIONS_REQUEST_WRITE_FIELS)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("ppp","Download else true");
                Intent intent = new Intent(SplashScreen.this, MainActivityNew.class);
                startActivity(intent);
            }
            else
            {
                Log.d("ppp","Download else");
            }
            return;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        go_next();//
    }

    @Override
    protected void onDestroy() {
        if(dialog!=null)
        {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }
}
