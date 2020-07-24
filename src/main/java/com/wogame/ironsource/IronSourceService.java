package com.wogame.ironsource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.wogame.cinterface.AdCallBack;
import com.wogame.common.AppMacros;
import com.wogame.permission.PermissionUtils;
import com.wogame.util.GMDebug;

public class IronSourceService {

    public static IronSourceService mInstance;
    private Activity mActivity;
    View mViewRoot;

    IronSourceBannerLayout mBannerTop;
    IronSourceBannerLayout mBannerBottom;

    private AdCallBack mAdCallback = null;
    InterstitialListener mInterstitialListener;
    boolean mIsRewarded = false;
    String mPlaceId;
    int isShowBanner[];


    public static IronSourceService getInstance() {
        if (mInstance == null) {
            mInstance = new IronSourceService();
        }
        return mInstance;
    }

    public void initActivity(Activity activity,final String appKey) {
        mActivity = activity;

        initSdk(appKey);
    }

    public void setCallBack(AdCallBack callBack){
        if(mAdCallback == null) mAdCallback = callBack;
    };

    private void initSdk(final String appKey){
        /**
         *Ad Units should be in the type of IronSource.Ad_Unit.AdUnitName, example
         * IronSource.AD_UNIT.OFFERWALL
         */
        IronSource.init(mActivity, appKey, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);
        //网络连接状态
        IronSource.shouldTrackNetworkState(mActivity, true);
        //成功验证集成后，请记住从代码中删除集成帮助器。
        //IntegrationHelper.validateIntegration(mActivity);

        initBanner();

        setRewardedVideoListener();
        setInterstitialListener();

        loadInterstitial();
    }

    private void initBanner(){
        isShowBanner = new int[2];

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        //@SuppressLint("WrongViewCast") LinearLayout fragment = (LinearLayout) mActivity.findViewById(R.id.banner_container);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        mViewRoot = inflater.inflate(R.layout.banner_ad, null, false);
        mActivity.addContentView(mViewRoot, params);
    }

    private void loadBottomAd(){
        isShowBanner[0] = 1;
        mBannerBottom = IronSource.createBanner(mActivity, ISBannerSize.BANNER);
        final FrameLayout bannerContainer = mViewRoot.findViewById(R.id.banner_container);
//        bannerContainer.addView(mBannerBottom);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

//        ImageView imageView = new ImageView(mActivity);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setImageResource(R.drawable.app_banner);

//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        bannerContainer.addView(mBannerBottom);
        mBannerBottom.setVisibility(View.GONE);
        mBannerBottom.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
                GMDebug.LogD("loadBottomAd onBannerAdLoaded");
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError error) {
                // Called after a banner has attempted to load an ad but failed.
                GMDebug.LogE("onBannerAdLoadFailed" + error.getErrorMessage());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isShowBanner[0] = 2;
//                        bannerContainer.removeAllViews();
                    }
                });
            }

            @Override
            public void onBannerAdClicked() {
                // Called after a banner has been clicked.
            }

            @Override
            public void onBannerAdScreenPresented() {
                // Called when a banner is about to present a full screen content.
            }

            @Override
            public void onBannerAdScreenDismissed() {
                // Called after a full screen content has been dismissed
            }

            @Override
            public void onBannerAdLeftApplication() {
                // Called when a user would be taken out of the application context.
            }
        });

        IronSource.loadBanner(mBannerBottom);
    }

    private void loadTopBanner(){
        isShowBanner[1] = 1;
        mBannerTop = IronSource.createBanner(mActivity, ISBannerSize.BANNER);
        final FrameLayout bannerTopContainer = mViewRoot.findViewById(R.id.banner_container);
        //FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
        //bannerContainer.addView(banner, 0, layoutParams);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        bannerTopContainer.addView(mBannerTop, 0, layoutParams);


        mBannerTop.setVisibility(View.GONE);

        mBannerTop.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
                GMDebug.LogD("onBannerAdLoaded");
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError error) {
                // Called after a banner has attempted to load an ad but failed.
                GMDebug.LogE("onBannerAdLoadFailed" + error.getErrorMessage());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isShowBanner[1] = 2;
                        bannerTopContainer.removeAllViews();
                    }
                });
            }

            @Override
            public void onBannerAdClicked() {
                // Called after a banner has been clicked.
            }

            @Override
            public void onBannerAdScreenPresented() {
                // Called when a banner is about to present a full screen content.
            }

            @Override
            public void onBannerAdScreenDismissed() {
                // Called after a full screen content has been dismissed
            }

            @Override
            public void onBannerAdLeftApplication() {
                // Called when a user would be taken out of the application context.
            }
        });

        IronSource.loadBanner(mBannerTop);
    }

    private void destroyBanner(int type){
        if(type == AppMacros.AT_Banner_Top){
            if(mBannerTop != null) IronSource.destroyBanner(mBannerTop);
        }
        else if(type == AppMacros.AT_Banner_Bottom){
            if(mBannerBottom != null) IronSource.destroyBanner(mBannerBottom);
        }
    }

    private void setRewardedVideoListener(){
        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            /**
             * Invoked when the RewardedVideo ad view has opened.
             * Your Activity will lose focus. Please avoid performing heavy
             * tasks till the video ad will be closed.
             */
            @Override
            public void onRewardedVideoAdOpened() {
            }
            /**Invoked when the RewardedVideo ad view is about to be closed.
            Your activity will now regain its focus.*/
            @Override
            public void onRewardedVideoAdClosed() {
                if(mIsRewarded){
                    if(mAdCallback != null){
                        mAdCallback.onCallBack(AppMacros.AT_RewardVideo,AppMacros.CALL_SUCCESS,mPlaceId,"","","");
                    }
                }
                else {
                    if(mAdCallback != null){
                        mAdCallback.onCallBack(AppMacros.AT_RewardVideo,AppMacros.CALL_CLOSE,mPlaceId,"","","");
                    }
                }
                mIsRewarded = false;
            }
            /**
             * Invoked when there is a change in the ad availability status.
             *
             * @param - available - value will change to true when rewarded videos are *available.
             *          You can then show the video by calling showRewardedVideo().
             *          Value will change to false when no videos are available.
             */
            @Override
            public void onRewardedVideoAvailabilityChanged(boolean available) {
                //Change the in-app 'Traffic Driver' state according to availability.
            }
            /**
             * Invoked when the video ad starts playing.
             */
            @Override
            public void onRewardedVideoAdStarted() {
            }
            /**Invoked when the video ad finishes playing.*/
            @Override
            public void onRewardedVideoAdEnded() {
            }
            /**
             * Invoked when the user completed the video and should be rewarded.
             * If using server-to-server callbacks you may ignore this events and wait *for the callback from the ironSource server.
             *
             * @param - placement - the Placement the user completed a video from.
             */
            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {
                /** here you can reward the user according to the given amount.
                 String rewardName = placement.getRewardName();
                 int rewardAmount = placement.getRewardAmount();
                 */

                //TODO - here you can reward the user according to the given amount
                String rewardName = placement.getRewardName();
                //int rewardAmount = placement.getRewardAmount();
                mIsRewarded = true;
            }
            /** Invoked when RewardedVideo call to show a rewarded video has failed
             * IronSourceError contains the reason for the failure.
             */
            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError error) {
                GMDebug.LogE(error.getErrorMessage());
            }

            @Override
            public void onRewardedVideoAdClicked(Placement placement) {

            }
        });
    }


    private void  setInterstitialListener(){
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
            }
            /**
             invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
            }
            /**
             Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
            }
            /*
             * Invoked when the ad was opened and shown successfully.
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
            }
            /**
             * Invoked when Interstitial ad failed to show.
             // @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
            }
            /*
             * Invoked when the end user clicked on the interstitial ad.
             */
            @Override
            public void onInterstitialAdClicked() {
            }
        });
    }

    private void loadInterstitial(){
        IronSource.loadInterstitial();
    }

    public void onResume() {
        IronSource.onResume(mActivity);
    }

    public void onPause() {
        IronSource.onPause(mActivity);
    }


    public boolean loadedAd(final int type){
        if(type == AppMacros.AT_RewardVideo){
            boolean available = IronSource.isRewardedVideoAvailable();
            return available;
        }
        else if(type == AppMacros.AT_Interstitial){
            return  IronSource.isInterstitialReady();
        }
        return false;
    }

    public void showAd(final int type, final String cpPlaceId){
        mPlaceId = cpPlaceId;
        if(type == AppMacros.AT_RewardVideo){
            boolean ss = IronSource.isRewardedVideoPlacementCapped(cpPlaceId);
            if(loadedAd(type)){
                IronSource.showRewardedVideo(cpPlaceId);
            }
            else {
                if(mAdCallback != null){
                    mAdCallback.onCallBack(AppMacros.AT_RewardVideo,AppMacros.CALL_FALIED,mPlaceId,"","","");
                }
            }
        }
        else if(type == AppMacros.AT_Interstitial){
            if(loadedAd(type)){
                IronSource.showInterstitial(cpPlaceId);
            }
            else {
                if(mAdCallback != null){
                    mAdCallback.onCallBack(AppMacros.AT_Interstitial,AppMacros.CALL_FALIED,mPlaceId,"","","");
                }
            }
        }
        else if(type == AppMacros.AT_Banner_Bottom) {
            if( isShowBanner[1] != 1){
                loadBottomAd();
            }
        }
    }
    /**
     * 关闭广告
     * @param type
     */
    public void closeAd(final int type) {
        GMDebug.LogD("closeAd" + type);
    }

}
