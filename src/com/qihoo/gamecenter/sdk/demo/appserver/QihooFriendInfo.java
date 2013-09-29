package com.qihoo.gamecenter.sdk.demo.appserver;

import org.json.JSONException;
import org.json.JSONObject;

public class QihooFriendInfo {
	
	public Boolean isFriend = false;
	public String phone;
	public String nick;
	public String qid;
	public Boolean isInvited;
	public String avatar;
	
	public static QihooFriendInfo parseJsonObj(JSONObject jsonObj) {
		QihooFriendInfo friendInfo = null;
				try {
					friendInfo.isFriend = jsonObj.getBoolean("is_friend");
					friendInfo.phone = jsonObj.getString("phone");
					friendInfo.nick = jsonObj.getString("nick");
					friendInfo.qid = jsonObj.getString("qid");
					friendInfo.isInvited = jsonObj.getBoolean("isInvited");
					friendInfo.avatar = jsonObj.getString("avatar");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
         

        return friendInfo;
    }
}
