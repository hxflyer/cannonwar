
package com.qihoo.gamecenter.sdk.demo.common;

public interface SdkLoginListener {
    
	/**
     * 360SDK登录返回授权码（授权码生存期只有60秒，必需立即请求应用服务器，以得到AccessToken）。
     */
    public void onGotAuthorizationCode(String code);;

}
