/****************************************************************************
 Copyright (c) 2015-2016 Chukong Technologies Inc.
 Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.cpp;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import org.cocos2dx.lib.Cocos2dxActivity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.annotation.Keep;
import androidx.core.content.FileProvider;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


import com.yourcompany.missingword.R;           //add your package name after write .R

public class AppActivity extends Cocos2dxActivity {

    private static AppActivity _this;
    public static boolean admobfullpageavailable =  false;

    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final String PREF_FILE  = "PREF_FILE";
    private static final String IS_PRODUCT_PURCHASE = "IS_PRODUCT_PURCHASE";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences prefs = null;

    public static final int WRITE_STORAGE_REQUEST_ID = 97483;
    String PhotoName = "ss.png";

    public static native void  coinReward();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
        // askForPermission();
        if (!isTaskRoot()) {
            return;
        }
        // Make sure we're running on Pie or higher to change cutout mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Enable rendering into the cutout area
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        AdManager.init(this);
        _this = this;

        prefs=  _this.getSharedPreferences("adpref", _this.MODE_PRIVATE);
        editor =  prefs.edit();
    }

    public static void vibrate() {
        Vibrator v = (Vibrator) _this.getSystemService(Context.VIBRATOR_SERVICE);
        Log.w("PTag", "check vibrate");
        //if (vibrator != null) {
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.w("PTag", "check vibrate 1");
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Log.w("PTag", "check vibrate 2");
            //deprecated in API 26
            v.vibrate(200);
        }
        //}
    }
    public static void VibrationLoad() {

        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                vibrate();
            }
        });

    }

     public static boolean isInterstitialAvailable(){

        return admobfullpageavailable;
    }

    public static void showInterstitial(){

        _this. runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AdManager.showInterstitialAd();
            }
        });

    }

    public static void ShowReward(){

        _this. runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AdManager.ShowRewardAds();
            }
        });

    }
    public static void RewardedAdditing()
    {
        Log.d("TAG", "aaa call");
        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                editor.putInt("ecoin", 100);
                editor.commit();
                editor.apply();

                coinReward();
                Toast.makeText(_this,"You Rewarded 100 Coins!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void OpenMoreGame()
    {
        String url="";
        Intent storeintent=null;

        String moreURL = getContext().getApplicationContext().getString(R.string.more_game_url);
        url = moreURL;
        storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        _this.startActivity(storeintent);

    }
    public static void openRateGame()
    {
        String url="";
        Intent storeintent=null;

        String rateURL = getContext().getApplicationContext().getString(R.string.rate_game_url);
        url = rateURL;
        storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        _this.startActivity(storeintent);
    }


    public static AppActivity getInstance() {
        Log.i("Call", "getInstance");
        return (AppActivity) _this;
    }

    public static void BackButtonClicked(){

        _this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(_this,
                        R.style.MyAlertDialogStyle);
                builder.setTitle(_this.getResources().getString(R.string.app_name));
                builder.setCancelable(false);
                builder.setMessage("Do you want to EXIT");
                builder  .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _this.finish();
                            }

                        })
                        .setNegativeButton("No", null);

                builder.show();
            }
        });

    }

}
