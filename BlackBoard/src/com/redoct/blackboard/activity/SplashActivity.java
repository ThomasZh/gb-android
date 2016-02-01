package com.redoct.blackboard.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redoct.blackboard.R;
import com.redoct.blackboard.network.HttpNetworkNormalManager;
import com.redoct.blackboard.network.IHttpTask;
import com.redoct.blackboard.task.ServerConnectTask;
import com.redoct.blackboard.util.DeviceUtil;
import com.redoct.iclub.config.AppConfig;
import com.redoct.iclub.config.ServiceConfig;

public class SplashActivity extends Activity {
	
	private TextView mShowInfoTv;
	private ProgressBar mProgressBar;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			updateShowInfo();
		}
	};
	
	private void updateShowInfo() {
		mProgressBar.setVisibility(View.GONE);
		mShowInfoTv.setText("服务不可用，请稍后重试......");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		mShowInfoTv = (TextView) findViewById(R.id.mShowInfoTv);
		mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
		
		AppConfig.DEVICE_ID=DeviceUtil.getRawDeviceId(this);
		AppConfig.OS_VERSION=DeviceUtil.getDeviceOS();
		
		connectGateKeeper();
	}
	
	
	
	private void connectGateKeeper(){
    	
    	ServerConnectTask connetTask = new ServerConnectTask() {
			@Override
			public void before() {
				super.before();
			}

			@Override
			public void callback(String responseContent, Header[] headers) {
				super.callback(responseContent);
				
				try {
					JSONObject object = new JSONObject(responseContent);
				
					String ip = object.optString("stpIp");
					String port = object.optString("stpPort");
					ServiceConfig.token = object.optString("gateToken");

					IHttpTask.HTTP_URL = "http://" + ip + ":" + port;
					
					Log.e("zyf", "stp url: "+IHttpTask.HTTP_URL);
					
					Intent intent=new Intent(SplashActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void complete() {
				super.complete();
			}

			@Override
			public void failure(int errorCode, String responseContent,
					Header[] headers) {
				super.failure(errorCode, responseContent);
				Log.e("zyf", "get gate keeper failed......");
				
				mHandler.sendEmptyMessage(0);
			}
		};
		HttpNetworkNormalManager.getInstance().addRunningtask(connetTask);
    }
}
