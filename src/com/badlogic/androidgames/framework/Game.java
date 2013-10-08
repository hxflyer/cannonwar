package com.badlogic.androidgames.framework;

import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.badlogic.androidgames.framework.Graphics.PixmapFormat;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.AndroidFastRenderView;
import com.badlogic.androidgames.framework.impl.AndroidFileIO;
import com.badlogic.androidgames.framework.impl.AndroidGraphics;
import com.badlogic.androidgames.framework.impl.AndroidInput;
import com.badlogic.androidgames.framework.impl.AndroidPixmap;
import com.hx.cannonwar.TankGame;
import com.qihoo.gamecenter.sdk.buildin.Matrix;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.demo.appserver.AddFriendListener;
import com.qihoo.gamecenter.sdk.demo.appserver.AddFriendTask;
import com.qihoo.gamecenter.sdk.demo.appserver.QihooUserInfo;
import com.qihoo.gamecenter.sdk.demo.appserver.QihooUserInfoListener;
import com.qihoo.gamecenter.sdk.demo.appserver.QihooUserInfoTask;
import com.qihoo.gamecenter.sdk.demo.appserver.TokenInfo;
import com.qihoo.gamecenter.sdk.demo.appserver.TokenInfoListener;
import com.qihoo.gamecenter.sdk.demo.appserver.TokenInfoTask;
import com.qihoo.gamecenter.sdk.demo.common.SdkUserBaseActivity;
import com.qihoo.gamecenter.sdk.demo.utils.ProgressUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhou.tank.Assets;
import com.zhou.tank.GameScreen;

public abstract class Game extends SdkUserBaseActivity  implements
	OnClickListener, TokenInfoListener, QihooUserInfoListener, AddFriendListener {
	
	public AndroidFastRenderView renderView;
	public Graphics graphics;
    public Audio audio;
    public Input input;
    public FileIO fileIO;
    public Screen screen;
    public WakeLock wakeLock;
    public SharedPreferences sp;
    
    public static Game instance;
    LinearLayout basicView;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        basicView = new LinearLayout(this);
        setContentView(basicView);
        boolean isLandscape = 
        		getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 800 : 480;
        int frameBufferHeight = isLandscape ? 480 : 800;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();
        
        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(getAssets());
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        
        basicView.addView(renderView);
        
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
        
    	Context ctx = Game.this;       
    	sp = ctx.getSharedPreferences("score", MODE_PRIVATE);
    	
    	init360(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();
        if(mProgress!=null){
            ProgressUtil.dismiss(mProgress);
            mProgress =null;
            }
        if (isFinishing()) {
            screen.dispose();
        }
        MobclickAgent.onPause(this);
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setScreen(Screen screen) {
        if (screen == null){
        	//throw new IllegalArgumentException("Screen must not be null");
        	this.screen.pause();
            this.screen.dispose();
        	finish();
        	return;
        }
        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }
    
    public Screen getCurrentScreen() {
        return screen;
    }
    
    public int getHighestScore(){
		return sp.getInt("HIGHESTSCORE", 0);
	}
	
	public void saveHighestScore(int score){
		doSdkUploadScore(mQihooUserInfo, mTokenInfo, String.valueOf(score));
		if(score<this.getHighestScore()){
			return;
		}
		Editor editor = sp.edit();
        editor.putInt("HIGHESTSCORE", score);
        editor.commit();
	}
	
	public Screen getStartScreen() {
		return null;
	}
	
	/***************
	 * 360 api
	 * *************/
	public final static String APPKEY = "0062fd5e49f631fcf02c3f660ac87e86";
    public final static String APPID = "200786751";
    public final static String APPSEC = "5659112b11d501fb431dba3cc8082b40";
    public static String ACCESSTOKEN = "";
    public static String REFRESHTOKEN = "";
    public ProgressDialog mProgress;
    private PopupWindow pw = null;

	public String loginNextIntent;
	
	public void init360(Bundle savedInstanceState){
		if (savedInstanceState == null) {
            // 传入横屏动画参数
            Matrix.init(this, true, new IDispatcherCallback() {
                @Override
                public void onFinished(String data) {
                    Log.d("login", "matrix startup callback,result is " + data);
                }
            });
        }
		this.doSdkCheckAutoLogin();
	}
	
	@Override
    public void onGotAuthorizationCode(String authorizationCode) {
		if (TextUtils.isEmpty(authorizationCode)) {
            Toast.makeText(this, "登录失败，请再试一次", Toast.LENGTH_LONG).show();
        } else {
            // 请求应用服务器，用AuthorizationCode换取AccessToken
        	mProgress = ProgressUtil.show(this,"","获取用户数据");
            TokenInfoTask.doRequest(this, authorizationCode, Matrix.getAppKey(this), this);
        }
    }

	@Override
	public void onGotTokenInfo(TokenInfo tokenInfo) {
		if (tokenInfo == null || TextUtils.isEmpty(tokenInfo.getAccessToken())) {
            Toast.makeText(this, "登录失败，请再试一次", Toast.LENGTH_LONG).show();
            if(mProgress!=null){
                ProgressUtil.dismiss(mProgress);
                mProgress =null;
            }
        } else {
            // 保存TokenInfo
            mTokenInfo = tokenInfo;
            // 请求应用服务器，用AccessToken换取UserInfo
            QihooUserInfoTask.doRequest(this, tokenInfo.getAccessToken(), Matrix.getAppKey(this),
                    this);
        }
	}
	
	@Override
	public void onGotUserInfo(QihooUserInfo userInfo) {
		if(mProgress!=null){
	        ProgressUtil.dismiss(mProgress);
	    }
	    mProgress =null;
        
	    if (userInfo != null && userInfo.isValid()) {
            // 保存QihooUserInfo
            mQihooUserInfo = userInfo;
            this.isLoggedIn = true;
            if(loginNextIntent!=null){
            	Log.d("tank", loginNextIntent);
            }
            AddFriendTask.doAddFriendTask(this, mTokenInfo.getAccessToken(), true,Matrix.getAppKey(this), this);
            if(loginNextIntent=="shareScore"){
            	showShareScorePopup();
            }else if(loginNextIntent=="inviteFriend"){
            	showInviteFriendPopup();
            }else if(loginNextIntent=="showList"){
            	showFriendsRank();
            }else if(loginNextIntent=="startGame"){
            	Screen gameScreen = ((TankGame)this).getGameScreen();
				((GameScreen)gameScreen).reset();
				setScreen(gameScreen);
            }
            loginNextIntent = null;
        } else {
            Toast.makeText(this, "登录失败，请再试一次", Toast.LENGTH_LONG).show();
        }
	}
	
    @Override
    public void onAddFriendTaskResult(String strResult) {
    	Log.d("tank", strResult);
        //Toast.makeText(Game.this, strResult, Toast.LENGTH_LONG).show();
    }
    
	public void showShareScorePopup() {
		
		this.runOnUiThread(new Runnable() {
			public void run() {
					generateScoreShareImg();
					doSdkSinaWeiboShare(mQihooUserInfo, mTokenInfo, true);
					mProgress = ProgressUtil.show(Game.instance, "", "连接新浪微博");
			    }
			});
		
		//doSdkGetMessageList(mQihooUserInfo, mTokenInfo);
	}
	
	public void generateScoreShareImg() {
		if(Assets.shareScoreImg==null){
			Assets.shareScoreImg = (AndroidPixmap)Game.instance.getGraphics().newPixmap("shareWeibo.jpg", PixmapFormat.RGB565);
        }
        int w = Assets.shareScoreImg.getWidth();  
        int h = Assets.shareScoreImg.getHeight();
        Log.d("tank","share img size"+ w+"/"+h);
        //create the new blank bitmap  
        Bitmap genbitmap = Bitmap.createBitmap( w, h, Config.RGB_565 );//创建一个新的和SRC长度宽度一样的位图  
        Canvas cv = new Canvas( genbitmap );  
        //draw src into  
        cv.drawBitmap( Assets.shareScoreImg.bitmap, 0, 0, null );//在 0，0坐标开始画入src 
        String score = String.valueOf(((TankGame)this).getGameScreen().world.totalScore);
        Paint paint = new Paint();

        paint.setTextSize(36);//设置字体大小
        paint.setColor(0xffffffff);
        cv.drawText(score, 250f-score.length()*7, 270f,paint);
        //draw watermark into  
        cv.save( Canvas.ALL_SAVE_FLAG );//保存  
        //store  
        cv.restore();//存储  
        
        FileOutputStream fOut;
		try {
			fOut = openFileOutput("shareWeiboGen.jpg",Context.MODE_WORLD_WRITEABLE);
			genbitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        Log.d("tank", "file generate done");
    }
	
	public void showFriendsRank(){
		doSdkDisplayGameFriendRank(mQihooUserInfo, mTokenInfo, true);
	}
	
	public void showInviteFriendPopup(){
		this.runOnUiThread(new Runnable() {
			public void run() {
				doSdkInviteFriendBySdk(mQihooUserInfo,mTokenInfo,true);
		    }
		});
	}
	
}