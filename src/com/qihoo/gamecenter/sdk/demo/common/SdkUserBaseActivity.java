
package com.qihoo.gamecenter.sdk.demo.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.hx.cannonwar.TankGame;
import com.qihoo.gamecenter.sdk.buildin.Matrix;
import com.qihoo.gamecenter.sdk.buildin.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.demo.appserver.AddFriendListener;
import com.qihoo.gamecenter.sdk.demo.appserver.AddFriendTask;
import com.qihoo.gamecenter.sdk.demo.appserver.QihooUserInfo;
import com.qihoo.gamecenter.sdk.demo.appserver.TokenInfo;
import com.qihoo.gamecenter.sdk.demo.utils.ProgressUtil;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.zhou.tank.GameScreen;

/**
 * SdkUserBaseActivity这个基类，处理请求360SDK的登录和支付接口。
 * 使用方的Activity继承SdkUserBaseActivity，调用doSdkLogin接口发起登录请求；调用doSdkPay接口发起支付请求。
 * 父类通过onGotAuthorizationCode通知子类登录获取的授权码
 * ，子类实现onGotAuthorizationCode接口接收授权码，做后续处理。
 */
public abstract class SdkUserBaseActivity extends Activity implements SdkLoginListener {

    // 登录响应模式：CODE模式。
    protected static final String RESPONSE_TYPE_CODE = "code";
    public static final String APPNAME = "坦克大战";
    private static final String TAG = "SdkUserBaseActivity";
    private static final String TOPNID = "HIGHTESTSCORE";
	// 360SDK登录返回的Json字符串中的Json name，代表CODE模式登录返回的Authorization Code（授权码，60秒有效）。
	private static final String JSON_NAME_CODE = "code";

	
	public TokenInfo mTokenInfo;
    
	public QihooUserInfo mQihooUserInfo;
	
	private boolean mIsInOffline = false;
	public boolean isLoggedIn = false;

    // ---------------------------------调用360SDK接口------------------------------------
    /**
     * 使用360SDK的登录接口
     * 
     * @param isLandScape 是否横屏显示登录界面
     * @param isBgTransparent 是否以透明背景显示登录界面
     * @param clientId 即AppKey
     */
    public void doSdkLogin(boolean isLandScape, boolean isBgTransparent, String clientId) {
        if(isConnect(this)) {
        	doSdkLogin(isLandScape, isBgTransparent, clientId, true);
        } else {
        	doSdkLoginSupportOffline(isLandScape, isBgTransparent, clientId, true);
        }
    }
    

    /**
     * 使用360SDK的登录接口
     * 
     * @param isLandScape 是否横屏显示登录界面
     * @param isBgTransparent 是否以透明背景显示登录界面
     * @param clientId 即AppKey
     * @param isShowCloseIcon 是否显示关闭按钮 默认true
     * @param isForce 是否强制登陆 默认false
     */
    public void doSdkLogin(boolean isLandScape, boolean isBgTransparent, String clientId,boolean isShowCloseIcon) {
        mIsInOffline = false;
        Intent intent = getLoginIntent(isLandScape, isBgTransparent, clientId, isShowCloseIcon, false);
        Matrix.invokeActivity(this, intent, mLoginCallback);
    }
    
    /**
    * 使用 360SDK 的登录接口
    * @param isLandScape 是否横屏显示登录界面
    * @param isBgTransparent 是否以透明背景显示登录界面 
    * @param clientId 即 AppKey
    * @param isShowCloseIcon 是否显示关闭按钮 默认 true 
    */
    public void doSdkLoginSupportOffline(boolean isLandScape, boolean isBgTransparent, String clientId,boolean isShowCloseIcon) {
	    mIsInOffline = false;
	    Intent intent = getLoginIntent(isLandScape, isBgTransparent, clientId,isShowCloseIcon, true);
	    Matrix.invokeActivity(this, intent, mLoginCallbackSupportOffline);
    }
    

    // -----------------------------------参数Intent-------------------------------------

    /***
     * 生成调用360SDK登录接口的Intent
     * 
     * @param isLandScape 是否横屏
     * @param isBgTransparent 是否背景透明
     * @param clientId 即AppKey
     * @param isShowCloseIcon 是否显示关闭按钮 默认true
     * @param isSupportOffline 是否支持离线模式
     * @return Intent
     */
    private Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent, 
    		String clientId, boolean isShowCloseIcon, boolean isSupportOffline) {

        Intent intent = new Intent(this, ContainerActivity.class);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 界面相关参数，360SDK登录界面背景是否透明。
        intent.putExtra(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);


        // *** 以下非界面相关参数 ***

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, isShowCloseIcon);

        // Client Id 即 App Key。
        if (!TextUtils.isEmpty(clientId))
            intent.putExtra(ProtocolKeys.CLIENT_ID, clientId);

        // 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
        intent.putExtra(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

        // 必需参数，手机的IMEI。
        intent.putExtra(ProtocolKeys.APP_IMEI, getImei());

        // 必需参数，360SDK版本号。
        intent.putExtra(ProtocolKeys.INSDK_VERSION, Matrix.getVersion(this));

        // 必需参数，App 版本号。
        intent.putExtra(ProtocolKeys.APP_VERSION, Matrix.getAppVersionName(this));

        // 必需参数，App Key。
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));

        // App 渠道号。
        intent.putExtra(ProtocolKeys.APP_CHANNEL, Matrix.getChannel());

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
        
        // 必须参数，登录的渠道号，每个游戏会分配不同的字串，用于区分用户从哪里登录
        intent.putExtra(ProtocolKeys.LOGIN_FROM, "360");
        
        // 可选参数，是否支持离线模式，默认值为false
        intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, isSupportOffline);

        return intent;
    }


    // ---------------------------------360SDK接口的回调-----------------------------------

    /**
     *  登录、注册的回调
     */
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            // press back 
            if (data == null) {
                return;
            }
            Log.d(TAG, "mLoginCallback, data is " + data);
            String authorizationCode = parseAuthorizationCode(data);
            onGotAuthorizationCode(authorizationCode);
        }
    };
    
	/**
	 * 离线模式登录、注册的回调
	 */
	private IDispatcherCallback mLoginCallbackSupportOffline = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			// press back
			if (data == null) {
				return;
			}
			Log.d(TAG, "mLoginCallbackSupportOffline, data is " + data);
			try {
				JSONObject joRes = new JSONObject(data);
				JSONObject joData = joRes.getJSONObject("data");
				String mode = joData.optString("mode", "");
				if (!TextUtils.isEmpty(mode) && mode.equals("offline")) {
					Toast.makeText(SdkUserBaseActivity.this,
						"login success in offline mode", Toast.LENGTH_SHORT).show();
					mIsInOffline = true;
					mTokenInfo = new TokenInfo();
					mQihooUserInfo = new QihooUserInfo();
					/*
					 * 离线模式下,直接调用 addfriend 接口, 
					 * 可以从该接口返回的数据中尝试获取离线登录用户的昵称和 QID
					 */
					AddFriendTask.doAddFriendTask(SdkUserBaseActivity.this,
							null, false, Game.APPKEY, new AddFriendListener() {
						@Override
						public void onAddFriendTaskResult(
								String strResult) {
							Toast.makeText(SdkUserBaseActivity.this,
									strResult, Toast.LENGTH_LONG)
									.show();
							// 从数据中拿到 nick 和 qid
							try {
								JSONObject joRes = new JSONObject(strResult);
								JSONObject jodata = joRes.getJSONObject("data");
								String qid = jodata.optString("qid", "");
								String nick = jodata.optString("nick", "");
								mQihooUserInfo.setId(qid);
								mQihooUserInfo.setNick(nick);
								Log.i(TAG, "qid:"+qid+",nick:"+nick);
								Toast.makeText(SdkUserBaseActivity.this, 
										"qid:"+qid+",nick:"+nick, Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					mLoginCallback.onFinished(data);
				}
			} catch (Exception e) {
				Log.e(TAG, "mLoginCallbackSupportOffline exception", e);
			}
		}
	};
    
    /**
     * 从Json字符中获取授权码
     * 
     * @param intent Intent数据
     * @return 授权码
     */
    private String parseAuthorizationCode(String data) {
        String authorizationCode = null;
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject json = new JSONObject(data);
                int errCode = json.optInt("errno");
                if (errCode == 0) {
                    // 只支持code登陆模式
                    JSONObject content = json.optJSONObject("data");
                    authorizationCode = content.optString(JSON_NAME_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "parseAuthorizationCode=" + authorizationCode);
        return authorizationCode;
    }

    private String getImei() {
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    
    // -----------------------------添加好友接口---------------------------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkAddFriend(boolean isLandScape, QihooUserInfo usrInfo, TokenInfo tokeninfo){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo))
            return;
        Intent intent = this.getAddFriendIntent(isLandScape, tokeninfo);
        Matrix.execute(this, intent, new IDispatcherCallback() {

            @Override
            public void onFinished(String data) {
                //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
                //System.out.println(data);
            }
        });
    }
    
    private Intent getAddFriendIntent(boolean isLandScape, TokenInfo token){
        Intent intent = new Intent();
        // function code 必须参数，使用SDK接口添加好友
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_ADD_FRIENDS);
        // 必须参数，APP KEY
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        // 必须参数，access_token
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        return intent;
    }
    
    // ---------------------------------------------获取游戏好友接口-------------------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkGetGameFriend(QihooUserInfo usrInfo, TokenInfo tokeninfo){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = this.getGetGameFriendIntent(tokeninfo);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // token : 必须参数，用户token信息
    private Intent getGetGameFriendIntent(TokenInfo token){
        Intent intent = new Intent();
        int strStart = 1;
        int strCount = 20;
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能为获取游戏好友。
         *  app_id : 必须参数，APP KEY 
         *  start : 可选参数，表示从第几个好友开始获取。
         *  coutn : 可选参数，表示要获取的好友数量。
         *          start和count参数如果不传的话，会返回整个好友列表。
         *  access_token : 必须参数，用户的token
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_GET_GAME_FRIENDS);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.START, strStart/*"1"*/);
        intent.putExtra(ProtocolKeys.COUNT, strCount/*"20"*/);
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        return intent;
    }
    
    // ------------------------上传积分接口--------------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkUploadScore(QihooUserInfo usrInfo, TokenInfo tokeninfo,String score){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo))
            return;
        Intent intent = this.getUploadScoreIntent(tokeninfo,score);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                
            }
        });
    }
    // token : 必须参数，用户token信息
    private Intent getUploadScoreIntent(TokenInfo token,String score){
        Intent intent = new Intent();
        
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能为上传积分
         *  app_id : 必须参数，APP_KEY
         *  score : 用户积分。
         *  topnid : 排行榜标识
         *  access_token : 必须参数用户的token
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_UPLOAD_SCORE);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.SCORE, score);
        intent.putExtra(ProtocolKeys.TOPNID,TOPNID);
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        return intent;
    }
    
    // -----------------------------获取好友接口---------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkGetFriend(QihooUserInfo usrInfo, TokenInfo tokeninfo){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo))
            return;
        Intent intent = this.getGetFriendIntent(tokeninfo);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
            }
        });
    }
    // token : 必须参数，用户token信息
    public Intent getGetFriendIntent(TokenInfo token){
        Intent intent = new Intent();
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能为获取其他好友列表
         *  app_id : 必须参数，APP_KEY
         *  access_token : 必须参数，用户token
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_GET_FRIENDS);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        return intent;
    }
  
    // -------------------检查、并绑定微博----------------------
    public void doSdkCheckBindSinaWeibo(QihooUserInfo usrInfo, TokenInfo tokeninfo, boolean bLandScape){
        
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        
        Intent intent = getCheckBindSinaWeiboIntent(usrInfo, tokeninfo, bLandScape);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                if (null == data) {
                    return;
                }
                //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
                //System.out.println(data);
            }
        });
    }
    private Intent getCheckBindSinaWeiboIntent(QihooUserInfo usrInfo, TokenInfo tokeninfo, boolean bLandScape){
        Intent intent = new Intent();
        /*
         * 必须参数：
         *  function_code : 必须参数，表示要执行的操作
         *  app_id : 必须参数，APP_KEY
         *  access_token : 必须参数，用户token
         *  bLandScape : 屏幕方向，bool值 true为横屏，flase为竖屏
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_CHECK_BIND_SINAWEIBO);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, tokeninfo.getAccessToken());
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, bLandScape);
        return intent;
    }
   
    //--------------发送微博--------------
    public void doSdkSinaWeiboShare(QihooUserInfo usrInfo, final TokenInfo tokeninfo, boolean bLandScape){
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        // 判断微博绑定关系
        Intent intent = getCheckBindSinaWeiboIntent(usrInfo, tokeninfo, bLandScape);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
            	if (data == null) {
            		return;
            	}
            	try {
            		JSONObject result = new JSONObject(data);
            		if ("0".equals(result.optString("errno"))) {
            			JSONObject content = result.optJSONObject("data");
            			if (content != null && "1".equals(content.optString("status"))) {
            				String score = String.valueOf(((TankGame)Game.instance).getGameScreen().world.totalScore);
            				String shareImgPath = Game.instance.getFileStreamPath("shareWeiboGen.jpg").getAbsolutePath(); 
            				Log.d("tank", shareImgPath);
            				
            				Intent intent = getSinaWeiboShareIntent(tokeninfo, "玩坦克大战得了"+score+"分，你们也来试试吧 http://map.m.360.cn/t/6Qg", shareImgPath);
    		                Matrix.execute(SdkUserBaseActivity.this, intent, new IDispatcherCallback() {
    		                    @Override
    		                    public void onFinished(String data) {
    		                    	ProgressUtil.dismiss(Game.instance.mProgress);
    		                    	Toast.makeText(SdkUserBaseActivity.this, "微博发送成功", Toast.LENGTH_SHORT).show();
    		                    }
    		                });
            			} else {
            				Toast.makeText(SdkUserBaseActivity.this, "连接微博失败，请再次尝试", Toast.LENGTH_SHORT).show();
            				ProgressUtil.dismiss(Game.instance.mProgress);
            			}
            		} else {
            			Toast.makeText(SdkUserBaseActivity.this, "连接微博失败，请再次尝试", Toast.LENGTH_SHORT).show();
            			ProgressUtil.dismiss(Game.instance.mProgress);
            		}
				} catch (Exception e) {
				}
            }
        });
    }
    
    
    private Intent getSinaWeiboShareIntent(TokenInfo tokeninfo, String strContent, String filePath){
        
        Intent intent = new Intent();
        
        /*
         * 必须参数：
         *  function_code : 必须参数，标识发送微博分享
         *  app_id : 必须参数，APP_KEY
         *  access_token : 必须参数，用户token
         *  weibo_conten: 必须参数，微博分享的内容
         *  weibo_conten_pic: 可选参数，微博分享的图片的绝对路径
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SINAWEIBO_SHARE);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, tokeninfo.getAccessToken());
        intent.putExtra(ProtocolKeys.WEIBO_CONTENT, strContent);
        intent.putExtra(ProtocolKeys.WEIBO_CONTENT_PIC, filePath);
        return intent;
    }
    
    //--------------获取新浪微博好友接口--------------
    public void doSdkGetSinaWeiboFriendList(QihooUserInfo userInfo, final TokenInfo tokenInfo,
    		boolean bLannScape) {
    	if(!checkLoginInfo(userInfo)) {
            return;
        }
    	// 判断微博绑定关系
    	Intent intent = getCheckBindSinaWeiboIntent(userInfo, tokenInfo, bLannScape);
    	Matrix.execute(this, intent, new IDispatcherCallback() {
			
			@Override
			public void onFinished(String data) {
				if (data == null) {
					return ;
				}
				try {
					JSONObject result = new JSONObject(data);
					if ("0".equals(result.optString("errno"))) {
						JSONObject content = result.optJSONObject("data");
						if (content != null && "1".equals(content.optString("status"))) {
							// 获取微博好友列表
							Intent intentWeibo = getGetSinaWeiboFriendListIntent(tokenInfo);
							Matrix.execute(SdkUserBaseActivity.this, intentWeibo, new IDispatcherCallback() {
								
								@Override
								public void onFinished(String data) {
									Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
					}
				} catch(Exception e) {
					
				}
			}
		});
    }
    
    private Intent getGetSinaWeiboFriendListIntent(TokenInfo tokenInfo) {
    	Intent intent = new Intent();
    	/*
         * 必须参数：
         *  function_code : 必须参数，表示要执行的操作
         *  app_id : 必须参数，APP_KEY
         *  access_token : 必须参数，用户的token
        */
    	intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_GET_SINAWEIBO_FRIEND_LIST);
    	intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
    	intent.putExtra(ProtocolKeys.ACCESS_TOKEN, tokenInfo.getAccessToken());
    	return intent;
    }
    
    // ------------------注销登录----------------
    public void doSdkLogout(QihooUserInfo usrInfo){
    	isLoggedIn = false;
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = getLogoutIntent();
        Matrix.execute(this, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                Toast.makeText(SdkUserBaseActivity.this, "账户已退出", Toast.LENGTH_SHORT).show();
                Log.i(TAG, data);
            }
        });
    }
    
    private Intent getLogoutIntent(){
        
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能
        */
        Intent intent = new Intent();
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGOUT);
        return intent;
    }
    
    public boolean checkLoginInfo(QihooUserInfo info){
        if (mIsInOffline) {
            return true;
        }
        if(null == info || !info.isValid()){
            //Toast.makeText(SdkUserBaseActivity.this, "需要登录才能执行此操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // -----------------------------获取好友排行榜接口---------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkGetRankFriend(QihooUserInfo usrInfo, TokenInfo tokeninfo){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = this.getGetGameTopFriendIntent(tokeninfo);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
                //System.out.println(data);
            }
        });
    }
    
    // -----------------------------获取全球排行榜接口---------------
    // usrInfo : 必须参数，用户信息
    // tokeninfo : 必须参数，用户token信息
    public void doSdkGetRankAll(QihooUserInfo usrInfo, TokenInfo tokeninfo){
        // 检查用户是否登录
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = this.getGetGameTop100Intent(tokeninfo);
        Matrix.execute(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_SHORT).show();
                //System.out.println(data);
            }
        });
    }
    
    // token : 必须参数，用户token信息
    private Intent getGetGameTop100Intent(TokenInfo token){
        Intent intent = new Intent();
        //String strTopNID;
        //String StrOrderby;
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能为游戏的排行榜。
         *  app_id : 必须参数，APP KEY 
         *  access_token : 必须参数，用户的token
        */
        /*
         * 可选参数：
         *  topnid:可选参数，排行榜类型（不填写，则为默认的排行榜）
         *  orderby:可选参数，排序类型（填写为0则按正序排序，其他情况按倒序排序）
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_GET_RANK);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.TOPNID, TOPNID);
        if (token != null) {
            intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        }
        //intent.putExtra(ProtocolKeys.ORDERBY, StrOrderby);
        return intent;
    }
    
    // token : 必须参数，用户token信息
    private Intent getGetGameTopFriendIntent(TokenInfo token){
        Intent intent = new Intent();
        
        String strStart;
        String strCount;
        String strTopNID;
        String StrOrderby;
        /*
         * 必须参数：
         *  function_code : 必须参数，表示调用SDK接口执行的功能为游戏的排行榜。
         *  app_id : 必须参数，APP KEY 
         *  access_token : 必须参数，用户的token
        */
        /*
         * 可选参数：
         *  start : 可选参数，排名榜的第几位开始获取(索引从1开始)
         *  count : 可选参数，排名榜上从start开始，获取多少位,最小值为20。
         *          范围：20～100。start和count参数如果不传的话，会返回整个排行榜内容。
         *  topnid:可选参数，排行榜类型（不填写，则为默认的排行榜）
         *  orderby:可选参数，排序类型（填写为0则按正序排序，其他情况按倒序排序）
        */
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_GET_RANK_FRIENDS);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        //intent.putExtra(ProtocolKeys.START, strStart/*"1"*/);
        //intent.putExtra(ProtocolKeys.COUNT, strCount/*"20"*/);
        intent.putExtra(ProtocolKeys.TOPNID, TOPNID);
        //intent.putExtra(ProtocolKeys.ORDERBY, StrOrderby);
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        return intent;
    }
    
    public void doShowUserProfile(QihooUserInfo usrInfo, boolean  isLandScape,TokenInfo token){
        if(!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = new Intent();
        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SHOW_USER_PROFILE);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        Matrix.invokeActivity(this, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                if(data!=null){
                    //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void doQuerySdkInfo() {
        Intent intent = new Intent();
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUERY_SDK_INFO);
        Matrix.execute(SdkUserBaseActivity.this, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    public void doSdkCheckAutoLogin() {
    	if (isConnect(this)) {
			Intent intent = getCheckAutoLoginIntent();
			Matrix.execute(this, intent, new IDispatcherCallback() {
				
				@Override
				public void onFinished(String data) {
					try {
						JSONObject jsonObj = new JSONObject(data);
						Boolean isAutoLoginOk = jsonObj.getBoolean("autologin");
						if(isAutoLoginOk){
							doSdkLogin(true, false, Game.APPKEY);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			});
		} else {
			doSdkLoginSupportOffline(true, false, Game.APPKEY, true);
		}
    }
    
    private Intent getCheckAutoLoginIntent() {
        Intent intent = new Intent();
        // 必须参数，function code
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_CHECK_AUTOLOGIN);
        // 必须参数，appkey
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        return intent;
    }
    
    public void doSdkInviteFriendBySdk(QihooUserInfo usrinfo, TokenInfo token, boolean bLandScape) {
        if (!checkLoginInfo(usrinfo)) {
            return;
        }
        Intent intent = getInviteFriendBySdkIntent(token, bLandScape);
        Matrix.invokeActivity(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                //System.out.println("result: " + data);
                //Toast.makeText(SdkUserBaseActivity.this, "result: " + data, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private Intent getInviteFriendBySdkIntent(TokenInfo token, boolean bLandScape) {
        
        
        /*
         * function_code : 必须参数，表示调用SDK接口执行的功能
         * app_key : 必须参数，appkey
         * screen_orientation : 360SDK界面是否以横屏显示。
         * insdk_version : 必需参数，360SDK版本号。
         * access_token : 必须参数，用户的token
         */
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_INVITE_FRIEND_BY_SDK);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, bLandScape);
        intent.putExtra(ProtocolKeys.INSDK_VERSION, Matrix.getVersion(this));
        return intent;
        
    }
    
    public void doSdkDisplayGameFriendRank(QihooUserInfo usrInfo, TokenInfo token, boolean bLandScape) {
        if (!checkLoginInfo(usrInfo)) {
            return;
        }
        Intent intent = getDisplayGameFriendRankIntent(token, bLandScape);
        Matrix.invokeActivity(this, intent, new IDispatcherCallback() {
            
            @Override
            public void onFinished(String data) {
                //System.out.println("result: " + data);
                //Toast.makeText(SdkUserBaseActivity.this, "result: " + data, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private Intent getDisplayGameFriendRankIntent(TokenInfo token, boolean bLandScape) {
               
        /*
         * function_code : 必须参数，表示调用SDK接口执行的功能
         * app_key : 必须参数，appkey
         * screen_orientation : 360SDK界面是否以横屏显示。
         * insdk_version : 必需参数，360SDK版本号。
         * app_name : 必须参数，应用的名称
         * access_token : 必须参数，用户的token
         */
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_DISPLAY_GAME_FRIEND_RANK);
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, token.getAccessToken());
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, bLandScape);
        intent.putExtra(ProtocolKeys.INSDK_VERSION, Matrix.getVersion(this));
        intent.putExtra(ProtocolKeys.APP_NAME, APPNAME);
        return intent;
    }
    /**
     * 使用 360SDK 的切换账号接口
     * @param isLandScape 是否横屏显示登录界面
     * @param isBgTransparent 是否以透明背景显示登录界面
     * @param clientId 即 AppKey 
     */
    public void doSdkSwitchAccount(boolean isLandScape, boolean isBgTransparent, String clientId) { 
    	Intent intent = getSwitchAccountIntent(isLandScape, isBgTransparent, clientId, true, false);
    	Matrix.invokeActivity(this, intent, mLoginCallback);
    }
    
    private Intent getSwitchAccountIntent(boolean isLandScape,
			boolean isBgTransparent, String clientId, boolean isShowCloseIcon, boolean isSupportOffline) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 界面相关参数，360SDK登录界面背景是否透明。
        intent.putExtra(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);


        // *** 以下非界面相关参数 ***

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, isShowCloseIcon);

        // Client Id 即 App Key。
        if (!TextUtils.isEmpty(clientId))
            intent.putExtra(ProtocolKeys.CLIENT_ID, clientId);

        // 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
        intent.putExtra(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

        // 必需参数，手机的IMEI。
        intent.putExtra(ProtocolKeys.APP_IMEI, getImei());

        // 必需参数，360SDK版本号。
        intent.putExtra(ProtocolKeys.INSDK_VERSION, Matrix.getVersion(this));

        // 必需参数，App 版本号。
        intent.putExtra(ProtocolKeys.APP_VERSION, Matrix.getAppVersionName(this));

        // 必需参数，App Key。
        intent.putExtra(ProtocolKeys.APP_KEY, Matrix.getAppKey(this));

        // App 渠道号。
        intent.putExtra(ProtocolKeys.APP_CHANNEL, Matrix.getChannel());

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
        
        // 必须参数，登录的渠道号，每个游戏会分配不同的字串，用于区分用户从哪里登录
        intent.putExtra(ProtocolKeys.LOGIN_FROM, "360");
        
        // 可选参数，是否支持离线模式，默认值为false
        intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, isSupportOffline);

        return intent;
	}


	/**
    * 使用 360SDK 的切换账号接口
    * @param isLandScape 是否横屏显示登录界面
    * @param isBgTransparent 是否以透明背景显示登录界面
    * @param clientId 即 AppKey */
    protected void doSdkSwitchAccountSupportOffline(boolean isLandScape, boolean isBgTransparent, String clientId) {
	    Intent intent = getSwitchAccountIntent(isLandScape, isBgTransparent, clientId, true, true);
	    Matrix.invokeActivity(this, intent, mAccountSwitchSupportOfflineCB); 
    }
    
    private IDispatcherCallback mAccountSwitchSupportOfflineCB = new IDispatcherCallback() {
		
		@Override
		public void onFinished(String data) {
			if (data == null) {
				return;
			}
			Log.d(TAG, "mAccountSwitchSupportOfflineCB, data is " + data);
			String authorizationCode = parseAuthorizationCode(data);
			if (TextUtils.isEmpty(authorizationCode)) {
				Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_LONG).show();
			} else {
				// 能获取到 auth code
				onGotAuthorizationCode(authorizationCode);
			}
		}
	};
	
	public boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}
}
