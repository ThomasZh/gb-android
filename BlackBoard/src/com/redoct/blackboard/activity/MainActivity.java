package com.redoct.blackboard.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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
import com.redoct.blackboard.task.DeleteGreenBoardTask;
import com.redoct.blackboard.task.ModifyGreenBoardTask;
import com.redoct.blackboard.task.QueryAllGreenBoardsTask;
import com.redoct.blackboard.util.MyProgressDialogUtils;
import com.redoct.blackboard.widget.GreenBoardCreateDialog;
import com.redoct.blackboard.widget.GreenBoardCreateDialog.MySubmmitListener;
import com.redoct.iclub.config.Constant;
public class MainActivity extends Activity {
	
	private PullToRefreshGridView mPullToRefreshGridView;
	private GreenBoardAdapter mGreenBoardAdapter;
	private ArrayList<GreenBoardInfo> mGreenBoardInfos=new ArrayList<GreenBoardInfo>();
	
	private int mode;
	private long time;
	private int optionIndex;
	
	private MyProgressDialogUtils mProgressDialogUtils;

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
					
					optionIndex=position;
					
					Intent intent=new Intent(MainActivity.this,GreenBoardDetailsActivity.class);
					intent.putExtra("GreenBoardInfo", mGreenBoardInfos.get(position));
					startActivityForResult(intent, Constant.RESULT_CODE_GREEN_BOARD_MODIFY);
				}
			}

			@Override
			public void doMoreFunction(int position) {
				super.doMoreFunction(position);
				
				if(position==0){
					return;
				}
				
				final int pos=position;
				
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_more_function);
				dialog.setTitle(null);
				dialog.show();
				dialog.findViewById(R.id.mEditTv).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						
						modifyGreenBoard(pos);
					}
				});
				dialog.findViewById(R.id.mDeleteTv).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						
						deleteGreenBoard(pos);
					}
				});
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
	
	private void deleteGreenBoard(final int pos){
		
		DeleteGreenBoardTask mDeleteGreenBoardTask=new DeleteGreenBoardTask(mGreenBoardInfos.get(pos).getId()){

			@Override
			public void before() {
				super.before();
				
				mProgressDialogUtils=new MyProgressDialogUtils("正在删除...", MainActivity.this);
				mProgressDialogUtils.showDialog();
			}

			@Override
			public void callback(String responseContent, Header[] headers) {
				super.callback(responseContent, headers);
				
				mGreenBoardInfos.remove(pos);
				mGreenBoardAdapter.notifyDataSetChanged();
				
				Toast.makeText(MainActivity.this, "删除成功.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void failure(int errorCode, String responseContent,
					Header[] headers) {
				super.failure(errorCode, responseContent, headers);
				
				Toast.makeText(MainActivity.this, "删除失败.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void complete() {
				super.complete();
				
				mProgressDialogUtils.dismissDialog();
			}
			
		};
		HttpNetworkNormalManager.getInstance().addRunningtask(mDeleteGreenBoardTask);
	}
	
	private void modifyGreenBoard(final int pos){
		
		final GreenBoardCreateDialog mDialog=new GreenBoardCreateDialog(MainActivity.this, R.style.MyCustomDialog);
		mDialog.setMySubmmitListener(new MySubmmitListener() {
			
			@Override
			public void summit(final String content) {
				
				if(TextUtils.isEmpty(content)){
					
					Toast.makeText(MainActivity.this, "输入的标题不可为空.", Toast.LENGTH_SHORT).show();
					return;
				}else {
					
					JSONObject postJsonObject=new JSONObject();
					try {
						postJsonObject.put("title", content);
						
					} catch (JSONException e) {
						
						Toast.makeText(MainActivity.this, "编辑失败.", Toast.LENGTH_SHORT).show();
						return;
					}
					ModifyGreenBoardTask mModifyGreenBoardTask=new ModifyGreenBoardTask(mGreenBoardInfos.get(pos).getId(),postJsonObject.toString()){

						@Override
						public void before() {
							super.before();
							
							mProgressDialogUtils=new MyProgressDialogUtils("正在编辑...", MainActivity.this);
							mProgressDialogUtils.showDialog();
						}

						@Override
						public void callback(String responseContent,
								Header[] headers) {
							super.callback(responseContent, headers);
							
							Log.e("zyf","green board modify success....");
							
							mGreenBoardInfos.get(pos).setTitle(content);
							mGreenBoardAdapter.notifyDataSetChanged();
							Toast.makeText(MainActivity.this, "编辑成功.", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void failure(int errorCode,
								String responseContent, Header[] headers) {
							super.failure(errorCode, responseContent, headers);
							
							Log.e("zyf","green board modify failed....");
							Toast.makeText(MainActivity.this, "编辑失败.", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void complete() {
							super.complete();
							
							mProgressDialogUtils.dismissDialog();
						}
						
					};
					HttpNetworkNormalManager.getInstance().addRunningtask(mModifyGreenBoardTask);
				}
				
				mDialog.dismiss();
			}

		});
		mDialog.show();
		mDialog.initContent(mGreenBoardInfos.get(pos).getTitle());
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
							
							mProgressDialogUtils=new MyProgressDialogUtils("正在创建...", MainActivity.this);
							mProgressDialogUtils.showDialog();
						}

						@Override
						public void callback(String responseContent,
								Header[] headers) {
							super.callback(responseContent, headers);
							
							Log.e("zyf","green board create success....responseContent: "+responseContent);
							
							try {
								JSONObject jsonObject=new JSONObject(responseContent);
								
								GreenBoardInfo greenBoardInfo=new GreenBoardInfo();
								greenBoardInfo.setId(jsonObject.optString("id"));
								greenBoardInfo.setTitle(jsonObject.optString("title"));
								greenBoardInfo.setTimestamp(jsonObject.optLong("timestamp"));
								
								mGreenBoardInfos.add(1, greenBoardInfo);
								
								mGreenBoardAdapter.notifyDataSetChanged();
								
								Toast.makeText(MainActivity.this, "创建成功.", Toast.LENGTH_SHORT).show();
								
							} catch (JSONException e) {
								
								Toast.makeText(MainActivity.this, "创建失败.", Toast.LENGTH_SHORT).show();
							}
							
						}

						@Override
						public void failure(int errorCode,
								String responseContent, Header[] headers) {
							super.failure(errorCode, responseContent, headers);
							
							Log.e("zyf","green board create failed....");
							Toast.makeText(MainActivity.this, "创建失败.", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void complete() {
							super.complete();
							
							mProgressDialogUtils.dismissDialog();
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null) {
			Log.e("zyf", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			return;
		}else {
			Log.e("zyf", "********************************************************");
		}

		if (requestCode == Constant.RESULT_CODE_GREEN_BOARD_MODIFY) {

			Log.e("zyf", "Constant.RESULT_CODE_GREEN_BOARD_MODIFY......");
			
			GreenBoardInfo greenBoardInfo=(GreenBoardInfo) data.getSerializableExtra("GreenBoardInfo");
			boolean deleted=data.getBooleanExtra("deleted", false);
			
			if(deleted){ //已经删除
				
				mGreenBoardInfos.remove(optionIndex);
			}else {  //更新
				mGreenBoardInfos.set(optionIndex, greenBoardInfo);
			}
			mGreenBoardAdapter.notifyDataSetChanged();
		}
	}
	
}
