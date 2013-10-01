package com.qihoo.gamecenter.sdk.demo.appserver;

import android.content.Context;
import android.content.Intent;

import com.qihoo.gamecenter.sdk.buildin.Matrix;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

public class AddFriendTask {

    public static void doAddFriendTask(Context context, String strToken, boolean isLandScape, String appkey, final AddFriendListener listener){
        
        Intent intent = new Intent();
        // function code 必须参数，使用SDK接口添加好友
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_ADD_FRIENDS);
        // 必须参数，APP KEY
        intent.putExtra(ProtocolKeys.APP_KEY, appkey);
        // 必须参数，access_token
        intent.putExtra(ProtocolKeys.ACCESS_TOKEN, strToken);
        // 必须参数，IS_SCREEN_ORIENTATION_LANDSCAPE
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        Matrix.execute(context, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                if(listener != null)
                    listener.onAddFriendTaskResult(data);
            }
        });
    }
    
    
}
