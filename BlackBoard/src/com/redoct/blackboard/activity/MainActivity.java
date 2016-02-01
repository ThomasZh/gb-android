package com.redoct.blackboard.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.redoct.blackboard.R;
import com.redoct.blackboard.adapter.GreenBoardAdapter;
import com.redoct.blackboard.item.GreenBoardInfo;
import com.redoct.blackboard.network.HttpNetworkNormalManager;
import com.redoct.blackboard.task.CreateGreenBoardTask;
import com.redoct.blackboard.task.QueryAllGreenBoardsTask;
import com.redoct.blackboard.widget.GreenBoardCreateDialog;
import com.redoct.blackboard.widget.GreenBoardCreateDialog.MySubmmitListener;
public class MainActivity extends Activity {
	
	private PullToRefreshGridView mPullToRefreshGridView;
	private GreenBoardAdapter mGreenBoardAdapter;
	private ArrayList<GreenBoardInfo> mGreenBoardInfos=new ArrayList<GreenBoardInfo>();
	
	private int mode;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mPullToRefreshGridView=(PullToRefreshGridView)findViewById(R.id.mPullToRefreshGridView);
		mPullToRefreshGridView.setMode(Mode.BOTH);
		mGreenBoardAdapter=new GreenBoardAdapter(this,mGreenBoardInfos){

			@Override
			public void gotoDetails(int position) {
				super.gotoDetails(position);
				
				if(position==0){   //创建一个新greenboard
					
					addNewGreenBoard();
					
				}else {   //查看greenboard详情
					
				}
			}
			
		};
		mPullToRefreshGridView.setAdapter(mGreenBoardAdapter);
		mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) { // 下拉刷新

				Log.e("zyf", "on pull down......");

				mode=QueryAllGreenBoardsTask.MODE_FIRST;
				time=System.currentTimeMillis();

				loadData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) { // 上拉加载更多

				Log.e("zyf", "on pull up......");

				if(mGreenBoardInfos==null||mGreenBoardInfos.size()==0){
					
					mode=QueryAllGreenBoardsTask.MODE_FIRST;
					time=System.currentTimeMillis();
				}else {
					mode=QueryAllGreenBoardsTask.MODE_BEFORE;
					time=mGreenBoardInfos.get(mGreenBoardInfos.size()-1).getTimestamp();
				}

				loadData();
			}
		});
		
		mode=QueryAllGreenBoardsTask.MODE_FIRST;
		time=System.currentTimeMillis();
		loadData();
	}
	
	private void addNewGreenBoard(){
		
		final GreenBoardCreateDialog mDialog=new GreenBoardCreateDialog(MainActivity.this, R.style.MyCustomDialog); 
		mDialog.setMySubmmitListener(new MySubmmitListener() {
			
			@Override
			public void summit(String content) {
				
				if(TextUtils.isEmpty(content)){
					
					Toast.makeText(MainActivity.this, "输入的标题不可为空.", Toast.LENGTH_SHORT).show();
					return;
				}else {
					
					JSONObject postJsonObject=new JSONObject();
					try {
						postJsonObject.put("title", content);
						
					} catch (JSONException e) {
						
						Toast.makeText(MainActivity.this, "创建失败.", Toast.LENGTH_SHORT).show();
						return;
					}
					CreateGreenBoardTask mCreateGreenBoardTask=new CreateGreenBoardTask(postJsonObject.toString()){

						@Override
						public void before() {
							super.before();
						}

						@Override
						public void callback(String responseContent,
								Header[] headers) {
							super.callback(responseContent, headers);
							
							Log.e("zyf","green board create success....");
						}

						@Override
						public void failure(int errorCode,
								String responseContent, Header[] headers) {
							super.failure(errorCode, responseContent, headers);
							
							Log.e("zyf","green board create failed....");
						}

						@Override
						public void complete() {
							super.complete();
						}
						
					};
					HttpNetworkNormalManager.getInstance().addRunningtask(mCreateGreenBoardTask);
				}
				
				mDialog.dismiss();
			}

		});
		mDialog.show();
	}
	
	private void loadData(){
		
		QueryAllGreenBoardsTask mQueryAllGreenBoardsTask=new QueryAllGreenBoardsTask(time,mode){

			@Override
			public void before() {
				super.before();
			}

			@Override
			public void callback(String responseContent, Header[] headers) {
				super.callback(responseContent, headers);
				
				try {
					
					JSONArray totalArray=new JSONArray(responseContent);
					JSONObject jsonObject;
					GreenBoardInfo greenBoardInfo;
					
					if(mode==QueryAllGreenBoardsTask.MODE_FIRST){
						mGreenBoardInfos.clear();
					}
					
					for(int i=0;i<totalArray.length();i++){
						
						jsonObject=totalArray.getJSONObject(i);
						
						greenBoardInfo=new GreenBoardInfo();
						greenBoardInfo.setId(jsonObject.optString("id"));
						greenBoardInfo.setTitle(jsonObject.optString("title"));
						greenBoardInfo.setTimestamp(jsonObject.optLong("timestamp"));
						
						mGreenBoardInfos.add(greenBoardInfo);
					}
					
					if(mode==QueryAllGreenBoardsTask.MODE_FIRST){
						mGreenBoardInfos.add(0,new GreenBoardInfo());
					}
					
					mGreenBoardAdapter.notifyDataSetChanged();
					
				} catch (Exception e) {
					
					Log.e("zyf", "QueryAllGreenBoardsTask exception: "+e.toString());
				}
			}

			@Override
			public void failure(int errorCode, String responseContent,
					Header[] headers) {
				super.failure(errorCode, responseContent, headers);
			}

			@Override
			public void complete() {
				super.complete();
				
				mPullToRefreshGridView.onRefreshComplete();
			}
			
		};
		HttpNetworkNormalManager.getInstance().addRunningtask(mQueryAllGreenBoardsTask);
	}
	
}
