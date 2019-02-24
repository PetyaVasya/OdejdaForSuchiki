package com.example.mac.suchik.UI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mac.suchik.InternetDialogFragment;
import com.example.mac.suchik.R;
import com.example.mac.suchik.Storage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivityUI extends AppCompatActivity implements InternetDialogFragment.InternetDialogListener {
    public android.support.v7.app.ActionBar actionbar;

    public static final int MAIN_WINDOW_FRAGMENT = 1;
    public static final int SCHEDULE_WINDOW_FRAGMENT = 2;
    public static final int MY_LIST = 3;
    public static final int SETTINGS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#84B3D5")));//change color of action bar
        Storage.getOrCreate(getApplicationContext());
        actionbar.setTitle("WAW");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.build();
        ImageLoader.getInstance().init(config);
//        actionbar.setBackgroundDrawable(getDrawable(R.drawable.backgtoundmusttop));
//        actionbar.setDisplayShowTitleEnabled (false);
//        if (!hasConnection(this)) {
//                (new InternetDialogFragment()).show(getSupportFragmentManager(), "WAW");
//        }
//        else if (savedInstanceState == null) {
            openFragment(MAIN_WINDOW_FRAGMENT);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_window_btn:
                openFragment(MAIN_WINDOW_FRAGMENT);
                return true;
            case R.id.schedule_btn:
                openFragment(SCHEDULE_WINDOW_FRAGMENT);
                return true;
            case R.id.list_btn:
                openFragment(MY_LIST);
                return true;
            case R.id.settings_btn:
                openFragment(SETTINGS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFragment(int fragmentId) {
        Fragment fragment = null;
        switch (fragmentId) {
            case SCHEDULE_WINDOW_FRAGMENT:
                fragment = new Schedule();
                break;
            case MY_LIST:
                fragment = new TimeTable();
                break;
            case SETTINGS:
                fragment = new Settings();
                break;
            case MAIN_WINDOW_FRAGMENT:
            default:
                fragment = new MainWindowFragment();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        if (!hasConnection(this)) {
            (new InternetDialogFragment()).show(getSupportFragmentManager(), "WAW");
        }
        else{
            openFragment(MAIN_WINDOW_FRAGMENT);
        }
    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        System.exit(0);
    }
}