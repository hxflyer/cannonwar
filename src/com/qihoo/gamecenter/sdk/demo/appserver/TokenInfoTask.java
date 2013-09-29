
package com.qihoo.gamecenter.sdk.demo.appserver;

import com.badlogic.androidgames.framework.Game;
import com.qihoo.gamecenter.sdk.demo.Constants;
import com.qihoo.gamecenter.sdk.demo.common.SdkHttpListener;
import com.qihoo.gamecenter.sdk.demo.common.SdkHttpTask;

import android.content.Context;
import android.util.Log;

/***
 * 此类使用AuthorizationCode，请求您的应用服务器，以获取AccessToken。
 * （注：应用服务器由360SDK使用方自行搭建，用于和360服务器进行安全交互，具体协议请查看文档中，服务器端接口）。
 */
public class TokenInfoTask {

    private static final String TAG = "TokenInfoTask";

    private static SdkHttpTask sSdkHttpTask;

    public synchronized static void doRequest(Context context,
    		String authorizationCode,
            String appKey,
            final TokenInfoListener listener) {
        // DEMO使用的应用服务器url仅限DEMO示范使用，禁止正式上线游戏把DEMO应用服务器当做正式应用服务器使用，请使用方自己搭建自己的应用服务器。
        String url = Constants.APP_SERVER_URL_GET_TOKEN_BY_CODE + authorizationCode +
                "&client_id=" + Game.APPKEY+
                "&client_secret="+Game.APPSEC+
                "&redirect_uri=oob";
        
        //url = "http://tongban1.com/bitdrill/game/cannonwar.php?authorizecode="+authorizationCode;
        Log.d("tank", url);
        // 如果存在，取消上一次请求
        if (sSdkHttpTask != null) {
            sSdkHttpTask.cancel(true);
        }
        // 新请求
        sSdkHttpTask = new SdkHttpTask(context);
        sSdkHttpTask.doGet(new SdkHttpListener() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse=" + response);
                TokenInfo tokenInfo = TokenInfo.parseJson(response);
                listener.onGotTokenInfo(tokenInfo);
                sSdkHttpTask = null;
            }

            @Override
            public void onCancelled() {
                listener.onGotTokenInfo(null);
                sSdkHttpTask = null;
            }

        }, url);
    }

    public synchronized static boolean doCancel() {
        return (sSdkHttpTask != null) ? sSdkHttpTask.cancel(true) : false;
    }

}
