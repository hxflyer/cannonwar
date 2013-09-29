
package com.qihoo.gamecenter.sdk.demo.appserver;

import android.content.Context;
import android.util.Log;

import com.qihoo.gamecenter.sdk.demo.Constants;
import com.qihoo.gamecenter.sdk.demo.common.SdkHttpListener;
import com.qihoo.gamecenter.sdk.demo.common.SdkHttpTask;

/***
 * 此类使用Access Token，请求您的应用服务器，获取QihooUserInfo。
 * （注：应用服务器由360SDK使用方自行搭建，用于和360服务器进行安全交互，具体协议请查看文档中，服务器端接口）。
 */
public class QihooUserInfoTask {

    private static final String TAG = "QihooUserInfoTask";

    private static SdkHttpTask sSdkHttpTask;

    public synchronized static void doRequest(Context context, String accessToken, String appKey,
            final QihooUserInfoListener listener) {

        // DEMO使用的应用服务器url仅限DEMO示范使用，禁止正式上线游戏把DEMO应用服务器当做正式应用服务器使用，请使用方自己搭建自己的应用服务器。
        String url = Constants.APP_SERVER_URL_GET_USER_BY_TOKEN + accessToken + "&appkey="
                + appKey;
        //https://openapi.360.cn/user/me.json?access_token=12345678983b38aabcdef387453ac8133ac3263987654321&fields=id,name,avatar,sex,area
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
                QihooUserInfo userInfo = QihooUserInfo.parseJson(response);
                listener.onGotUserInfo(userInfo);
                sSdkHttpTask = null;
            }

            @Override
            public void onCancelled() {
                listener.onGotUserInfo(null);
                sSdkHttpTask = null;
            }

        }, url);
    }

    public synchronized static boolean doCancel() {
        return (sSdkHttpTask != null) ? sSdkHttpTask.cancel(true) : false;
    }

}
