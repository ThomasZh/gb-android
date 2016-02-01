package com.redoct.blackboard.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.redoct.blackboard.network.IHttpTask;
import com.redoct.iclub.config.AppConfig;
import com.redoct.iclub.config.ServiceConfig;

public class ServerConnectTask extends IHttpTask {
	

	@Override
	public String justTodo() {

		JSONObject object = new JSONObject();
		try {
			
			object.put("deviceId", AppConfig.DEVICE_ID);
			object.put("appId", AppConfig.APP_ID);
			object.put("clientVersion", AppConfig.APP_VERSION);
			
			Log.e("zyf", "AppConfig.DEVICE_ID: "+AppConfig.DEVICE_ID);
			Log.e("zyf", "AppConfig.APP_ID: "+AppConfig.APP_ID);
			Log.e("zyf", "AppConfig.APP_VERSION: "+AppConfig.APP_VERSION);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return object.toString();
	}

	@Override
	public String getUrl() {

		return ServiceConfig.HTTP_SERVER_CONNECT_IP + "/gatekeeper/tokens";
	}

	@Override
	public int getMethod() {

		return POST;
	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

	

}
