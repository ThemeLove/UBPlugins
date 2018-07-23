package com.ubsdk.ad.yyb.plugin;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.nativ.MediaListener;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeMediaADData;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.ResUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 这里接入的应用宝（广点通）激励视频广告是广点通的原生视频（自渲染）视频广告
 * @author qingshanliao
 */
public class ADYYBRewardVideoActivity extends Activity{
	private final String TAG=ADYYBRewardVideoActivity.class.getSimpleName();
	private UBADCallback mUBADCallback;
	private FrameLayout mVideoContainner;
	private MediaView mMediaView;
	private TextView mCountDown;
	private Button mDetailBtn;
	private ImageView mVideoHolder;
	
	private NativeMediaADData mVideoAD;                          // 原生视频广告对象
	private AQuery mAQuery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this,"activity_ad_yyb_rewardvideo"));
		loadADParams();
		initViewAndListener();
		showVideoAD();
	}

	/**
	 * 加载广告参数
	 */
	@SuppressWarnings("unchecked")
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mVideoAD = ADYYBSDK.getVideoAD();
		mUBADCallback = UBAD.getInstance().getUBADCallback();
	}
	
	/**
	 * 初始化View和Listener
	 */
	private void initViewAndListener() {
		UBLogUtil.logI(TAG+"----->initViewAndListener");
		mVideoContainner = (FrameLayout) findViewById(ResUtil.getViewID(this,"ad_yyb_video_container"));
		mMediaView = (MediaView) findViewById(ResUtil.getViewID(this,"ad_yyb_video_media_view"));
		mCountDown = (TextView) findViewById(ResUtil.getViewID(this,"ad_yyb_video_count_down"));
		mDetailBtn = (Button) findViewById(ResUtil.getViewID(this,"ad_yyb_video_detail"));
		mVideoHolder = (ImageView) findViewById(ResUtil.getViewID(this,"ad_yyb_video_holder"));

		mAQuery = new AQuery(mVideoContainner);
	    mDetailBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        if (mVideoAD != null) {
	          mVideoAD.onClicked(v);
	        }
	      }
	    });
	    
//	    MediaListener
	    mMediaListener = new MediaListener() {

	        @Override
	        public void onVideoReady(long videoDuration) {
	          UBLogUtil.logI(TAG+"----->MediaView AD onVideoReady");
	          mDuration = videoDuration;//视频的总时长
	        }

	        @Override
	        public void onVideoStart() {
	          UBLogUtil.logI(TAG+"----->MediaView AD onVideoStart");
	          mTickHandler.post(mCountDownRunnable);
	          mCountDown.setVisibility(View.VISIBLE);
	          mDetailBtn.setVisibility(View.VISIBLE);
	        }

	        @Override
	        public void onVideoPause() {
	          UBLogUtil.logI(TAG+"----->MediaView AD onVideoPause");
	        }

	        @Override
	        public void onVideoComplete() {
	          UBLogUtil.logI(TAG+"----->MediaView AD onVideoComplete");
	          UBLogUtil.logI(TAG+"----->RewardVideo AD complete!");
	          if (mUBADCallback!=null) {
				mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD complete!");
	          }
	          finish();
	        }

	        @Override
	        public void onVideoError(AdError adError) {
	        	UBLogUtil.logI(TAG+"----->mediaVideo AD onVideoError!");
	        	UBLogUtil.logI(TAG+"----->RewardVideo AD onVideoError!msg="+adError.getErrorMsg());
	        	
	        	if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO,adError.getErrorMsg());
				}
	        	
//	        	清空倒计时
	        	releaseCountDown();
//	        	加载失败的时候，可以播放一段默认视频
	        }

	        @Override
	        public void onADButtonClicked() {
	          // 当广点通默认视频控制界面中的“查看详情/免费下载”按钮被点击时，会回调此接口，如果没有使用广点通的控制条此接口不会被回调
	          UBLogUtil.logI(TAG+"----->mediaView AD onADButtonClicked");
	        }

	        @Override
	        public void onReplayButtonClicked() {
	          // 当广点通默认视频控制界面中的“重新播放”按钮被点击时，会回调此接口
	          UBLogUtil.logI(TAG+"----->mediaView AD onReplayButtonClicked");
	        }

	        @Override
	        public void onFullScreenChanged(boolean inFullScreen) {
	        	UBLogUtil.logI(TAG+"----->mediaVideo AD onFullScreenChanged:isFullScreen="+inFullScreen);
	        }
	      };
	}

	/**
	 * 加载广告
	 */
	private void showVideoAD() {
		if (mVideoAD!=null) {
			mVideoAD.onExposured(mMediaView);
			// 广告加载成功 渲染UI
	        mAQuery.id(mVideoHolder).image(mVideoAD.getImgUrl(), false, true, 0, 0, new BitmapAjaxCallback() {
	            @Override
	            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
	              // AQuery框架有一个问题，就是即使在图片加载完成之前将ImageView设置为了View.GONE，在图片加载完成后，这个ImageView会被重新设置为VIEW.VISIBLE。
	              // 所以在这里需要判断一下，如果已经把ImageView设置为隐藏，开始播放视频了，就不要再显示广告的大图。开发者在用其他的图片加载框架时，也应该注意检查下是否有这个问题。
	              if (iv.getVisibility() == View.VISIBLE) {
	                iv.setImageBitmap(bm);
	              }
	            }
	        });
	        
	        loadMediaView();
		}
      }
	
	  /**
	  * 播放广告
	  */
	  private void loadMediaView() {
		 if (mVideoAD.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
		      // 首先把预设的大图隐藏，显示出MediaView。一定要保证MediaView可见，才能播放视频，否则SDK将无法上报曝光效果并计费。
		    mVideoHolder.setVisibility(View.INVISIBLE);
		    mMediaView.setVisibility(View.VISIBLE);
		      // bindView时指定第二个参数为false，则不会调用广点通的默认视频控制条。贴片场景下可能不太需要用到SDK默认的控制条。
		    mVideoAD.bindView(mMediaView,false);  
		    mVideoAD.play();
		    mVideoAD.setVolumeOn(true);
			mVideoAD.setMediaListener(mMediaListener);
		 }
	  }
     
	 /************************************************************广告倒计时按钮*************************************************************/		 
	  /**
	   * 刷新广告倒计时
	   */
	  private static final String TEXT_COUNTDOWN = "广告倒计时：%s ";
	  private long mCurrentPosition, mOldPosition, mDuration;
	  
	  private void releaseCountDown() {
	    if (mTickHandler != null && mCountDownRunnable != null) {
	    	mTickHandler.removeCallbacks(mCountDownRunnable);
	    }
	  }
	  
	  //主线程倒计时Handler
	  private Handler mTickHandler=new Handler(Looper.getMainLooper());
	  private Runnable mCountDownRunnable = new Runnable() {
	    public void run() {
	      if (mVideoAD != null) {
	        mCurrentPosition = mVideoAD.getCurrentPosition();
	        long position = mCurrentPosition;
	        if (mOldPosition == position && mVideoAD.isPlaying()) {
	          Log.i(TAG, "玩命加载中...");
	          mCountDown.setTextColor(Color.WHITE);
	          mCountDown.setText("玩命加载中...");
	        } else {
	          mCountDown.setTextColor(Color.WHITE);
	          mCountDown.setText(String.format(TEXT_COUNTDOWN, Math.round((mDuration - position) / 1000.0) + ""));
	        }
	        mOldPosition = position;
	        if (mVideoAD.isPlaying()) {
	        	mTickHandler.postDelayed(mCountDownRunnable, 500);
	        }
	      }
	    }
	  };



/************************************************************生命周期相关*************************************************************/	
	  @Override
	  protected void onResume() {
	    if (mVideoAD != null) {
	    	mVideoAD.resume();
	    }
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    if (mVideoAD != null) {
	    	mVideoAD.stop();
	    }
	    super.onPause();
	  }

	  @Override
	  protected void onDestroy() {
		UBLogUtil.logI(TAG+"----->onDestroy");
	    if (mVideoAD != null) {
	    	mVideoAD.destroy();
	    	mVideoAD=null;
	    }
	    releaseCountDown();
	    if (mCountDownRunnable!=null) {
			mCountDownRunnable=null;
		}
	    super.onDestroy();
	  }

	  
	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
		  //屏蔽物理返回键和home键
		  if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			return true;
		  }
		  return super.onKeyDown(keyCode, event);
	  }

	/**
	   * 如果要使得MediaView也能跟随屏幕旋转而全屏播放，请处理好运行时变更
	   */
	  @Override
	  public void onConfigurationChanged(Configuration newConfig) {
	    UBLogUtil.logI(TAG+"----->onConfigurationChanged");
	    // 1.开发者请把新的横屏LayoutParams或者竖屏LayoutParams设置给MediaView
	    setMediaParams(newConfig);
	    // 2.通知SDK，发生了运行时变更。这个接口也是增强型接口，开发者可以自己实现同样的功能。
	    if (mVideoAD != null) {
	    	mVideoAD.onConfigurationChanged(newConfig);
	    }
	    super.onConfigurationChanged(newConfig);
	  }
	  
	  private FrameLayout.LayoutParams landScapeParams, portraitParams;
	  private MediaListener mMediaListener;
	  /** 设置好横屏、竖屏的LayoutParams */
	  private void setMediaParams(Configuration newConfig) {
		UBLogUtil.logI(TAG+"----->setMediaParams");
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    if (portraitParams == null) { // 先保存竖屏的params
		    	portraitParams = (FrameLayout.LayoutParams) mMediaView.getLayoutParams();
		    }
		    if (landScapeParams == null) { // 进入横屏，新建一个MATCH_PARENT的LayoutParams
		        landScapeParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		        landScapeParams.gravity = Gravity.CENTER;
		    }
		    mMediaView.setLayoutParams(landScapeParams);
	    } else {
	       if (landScapeParams == null) { // 先保存横屏的params
	           landScapeParams = (FrameLayout.LayoutParams) mMediaView.getLayoutParams();
	       }
	      if (portraitParams == null) { // 进入竖屏，新建一个LayoutParams
	          portraitParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
	          portraitParams.gravity = Gravity.TOP;
	      }
	      mMediaView.setLayoutParams(portraitParams);
	    }
	  }
}
