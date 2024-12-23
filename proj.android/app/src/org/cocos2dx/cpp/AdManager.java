package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.yourcompany.missingword.R;
import android.widget.Toast;

public class AdManager {

    private static final boolean IS_TESTING = false;
    private static AppActivity activity;
    private static AdView bannerAdView;
    private static InterstitialAd mInterstitialAd;
    private static RewardedAd mRewardedAd;
    public static native void  coinReward();
    private static final String TAG = "MainActivity";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences prefs = null;

    static void init(Context context) {
        activity = (AppActivity) context;
        initializeAds();
        prefs=  activity.getSharedPreferences("adpref", activity.MODE_PRIVATE);
        editor =  prefs.edit();
    }

    private static void initializeAds() {

        bannerAdView = new AdView(activity);
        bannerAdView.setAdSize(getBannerAdSize());
        bannerAdView.setAdUnitId(activity.getString(R.string.admob_banner_id));
        loadBannerAd();

        //m_interstitialAd = new InterstitialAd(activity);
        //m_interstitialAd.setAdUnitId(activity.getString(R.string.admob_interstitial_id));
        loadInterstitialAd();

        LoadRewardedAds();

    }

    static void  LoadRewardedAds() {
        final AdRequest.Builder adRequest = new AdRequest.Builder();

        RewardedAd.load(activity, activity.getString(R.string.admob_rewarded_id), adRequest.build(), new RewardedAdLoadCallback() {

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mRewardedAd = null;
                AppActivity.admobfullpageavailable = false;
                LoadRewardedAds();
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                mRewardedAd = ad;
                AppActivity.admobfullpageavailable = true;
                //Log.d(TAG, "Ad was loaded.");
            }
        });


    }
    static void ShowRewardAds() {

        if (mRewardedAd != null) {
            Activity activityContext = activity;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                   // RewardGet(true);
//                    editor.putInt("ecoin", 100);
//                    editor.commit();
//                    editor.apply();
//
//                    coinReward();
                   // Toast.makeText(activity,"You Rewarded 3 Hints!",Toast.LENGTH_LONG).show();
                    RewardedAdditing();
                 //   Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });

            LoadRewardedAds();
        } else {
            //Log.d(TAG, "The rewarded ad wasn't ready yet.");
            LoadRewardedAds();
        }
    }

    public static void RewardedAdditing()
    {

        AppActivity.RewardedAdditing();


    }


    private static AdSize getBannerAdSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        int adWidth = (int) (outMetrics.widthPixels / outMetrics.density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    private static void loadBannerAd() {
        final AdRequest.Builder builder = new AdRequest.Builder();
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (bannerAdView.getParent() == null) {
                    LinearLayout layout = new LinearLayout(activity);
                    layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                    activity.addContentView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.addView(bannerAdView, adLayoutParams);
                }

                showBannerAd();

            }
        });
        bannerAdView.loadAd(builder.build());
    }

    static void showBannerAd() {
        bannerAdView.setVisibility(View.VISIBLE);
    }

    static void hideBannerAd() {
        bannerAdView.setVisibility(View.GONE);
    }


    private static void  loadInterstitialAd() {
        final AdRequest.Builder builder = new AdRequest.Builder();
        InterstitialAd.load(activity, activity.getString(R.string.admob_interstitial_id), builder.build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                AppActivity.admobfullpageavailable = true;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
                AppActivity.admobfullpageavailable = false;
                loadInterstitialAd();
            }
        });
    }

    static void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    AppActivity.admobfullpageavailable = false;
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                }
            });

            mInterstitialAd.show(activity);
        } else {
            loadInterstitialAd();
        }
    }



}
