package com.redoct.blackboard.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redoct.blackboard.R;
import com.redoct.blackboard.item.StickyNoteInfo;
import com.redoct.blackboard.network.HttpNetworkNormalManager;
import com.redoct.blackboard.task.CreateStickyNoteTask;
import com.redoct.blackboard.task.DeleteStickyNoteTask;
import com.redoct.blackboard.task.ModifyStickyNoteTask;
import com.redoct.blackboard.util.MyProgressDialogUtils;
import com.redoct.iclub.config.Constant;

public class StickyNoteCreateActivity extends Activity implements OnClickListener{
	
	private TextView mTitleTv;
	
	private LinearLayout mStickyNoteContainer,mCompletedContainer;
	private Button mYellowBtn,mGreenBtn,mBlueBtn,mPurpleBtn,mRedBtn,mSummitBtn,mDeleteBtn;
	private EditText mContentEt;
	private CheckBox mCompletedCb;
	
	private String color;
	private String[] colorArray={"yellow","green","blue","purple","red"};
	
	private String boardId;
	private StickyNoteInfo mStickyNoteInfo;
	
	private MyProgressDialogUtils mProgressDialogUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sticky_note_create);
		
		boardId=getIntent().getStringExtra("boardId");
		Log.e("zyf", "received boardId: "+boardId);
		
		mStickyNoteInfo=(StickyNoteInfo) getIntent().getSerializableExtra("StickyNoteInfo");
		
		initViews();
		color=colorArray[0];
		
		if(mStickyNoteInfo!=null){   //编辑模式
			
			updateViews();
		}
	}
	
	private void initViews(){
		
		mTitleTv=(TextView)findViewById(R.id.mTitleTv);
		mTitleTv.setText("编辑");
		
		findViewById(R.id.mLeftExitConatiner).setOnClickListener(this);
		findViewById(R.id.mRightContainer).setVisibility(View.GONE);
		
		mStickyNoteContainer=(LinearLayout)findViewById(R.id.mStickyNoteContainer);
		mCompletedContainer=(LinearLayout)findViewById(R.id.mCompletedContainer);
		
		mYellowBtn=(Button)findViewById(R.id.mYellowBtn);
		mGreenBtn=(Button)findViewById(R.id.mGreenBtn);
		mBlueBtn=(Button)findViewById(R.id.mBlueBtn);
		mPurpleBtn=(Button)findViewById(R.id.mPurpleBtn);
		mRedBtn=(Button)findViewById(R.id.mRedBtn);
		mSummitBtn=(Button)findViewById(R.id.mSummitBtn);
		mDeleteBtn=(Button)findViewById(R.id.mDeleteBtn);
		
		mYellowBtn.setOnClickListener(this);
		mGreenBtn.setOnClickListener(this);
		mBlueBtn.setOnClickListener(this);
		mPurpleBtn.setOnClickListener(this);
		mRedBtn.setOnClickListener(this);
		mSummitBtn.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);
		
		mCompletedCb=(CheckBox)findViewById(R.id.mCompletedCb);
		
		mContentEt=(EditText)findViewById(R.id.mContentEt);
	}
	
	private void updateViews(){
		
		mContentEt.setText(mStickyNoteInfo.getTitle());
		
		color=mStickyNoteInfo.getColor();
		if(color.equals("yellow")){
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.yellow));
			
		}else if(color.equals("green")){
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.green));
			
		}else if(color.equals("blue")){
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.blue));
			
		}else if(color.equals("purple")){
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.purple));
			
		}else if(color.equals("red")){
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.red));
		}
		
		mDeleteBtn.setVisibility(View.VISIBLE);
		mCompletedContainer.setVisibility(View.VISIBLE);
		mCompletedCb.setChecked(mStickyNoteInfo.isCompleted());
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.mYellowBtn:
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.yellow));
			color=colorArray[0];
			break;
		case R.id.mGreenBtn:
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.green));
			color=colorArray[1];
			break;
		case R.id.mBlueBtn:
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.blue));
			color=colorArray[2];
			break;
		case R.id.mPurpleBtn:
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.purple));
			color=colorArray[3];
			break;
		case R.id.mRedBtn:
			
			mStickyNoteContainer.setBackgroundColor(getResources().getColor(R.color.red));
			color=colorArray[4];
			break;
		case R.id.mSummitBtn:
			
			if(TextUtils.isEmpty(mContentEt.getText().toString())){
				
				Toast.makeText(StickyNoteCreateActivity.this, "标签内容不可为空.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(mStickyNoteInfo==null){   //创建
				
				JSONObject postJsonObject=new JSONObject();
				try {
					postJsonObject.put("title", mContentEt.getText().toString());
					postJsonObject.put("color", color);
				} catch (JSONException e) {
					
					Toast.makeText(StickyNoteCreateActivity.this, "标签创建失败.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				CreateStickyNoteTask mCreateStickyNoteTask=new CreateStickyNoteTask(boardId, postJsonObject.toString()){

					@Override
					public void before() {
						super.before();
						
						mProgressDialogUtils=new MyProgressDialogUtils("正在创建...", StickyNoteCreateActivity.this);
						mProgressDialogUtils.showDialog();
					}

					@Override
					public void callback(String responseContent, Header[] headers) {
						super.callback(responseContent, headers);
						
						Log.e("zyf", "sticky note create success,responseContent: "+responseContent);
						
						try {
							JSONObject jsonObject=new JSONObject(responseContent);
							
							StickyNoteInfo stickyNoteInfo=new StickyNoteInfo();
							stickyNoteInfo.setId(jsonObject.optString("id"));
							stickyNoteInfo.setTitle(jsonObject.optString("title"));
							stickyNoteInfo.setColor(jsonObject.optString("color"));
							stickyNoteInfo.setCompleted(jsonObject.optBoolean("completed"));
							stickyNoteInfo.setTimestamp(jsonObject.optLong("timestamp"));
							
							Intent intent=new Intent();
							intent.putExtra("StickyNoteInfo", stickyNoteInfo);
							setResult(Constant.RESULT_CODE_STICKY_NOTE_MODIFY, intent);
							finish();
							
							Toast.makeText(StickyNoteCreateActivity.this, "创建成功.", Toast.LENGTH_SHORT).show();
							
						} catch (JSONException e) {
							
							Toast.makeText(StickyNoteCreateActivity.this, "创建失败.", Toast.LENGTH_SHORT).show();
						}
						
					}

					@Override
					public void failure(int errorCode, String responseContent,
							Header[] headers) {
						super.failure(errorCode, responseContent, headers);
						
						Toast.makeText(StickyNoteCreateActivity.this, "创建失败.", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void complete() {
						super.complete();
						
						mProgressDialogUtils.dismissDialog();
					}
					
				};
				HttpNetworkNormalManager.getInstance().addRunningtask(mCreateStickyNoteTask);
			}else {
				
				JSONObject postJsonObject=new JSONObject();
				try {
					postJsonObject.put("id", mStickyNoteInfo.getId());
					postJsonObject.put("title", mContentEt.getText().toString());
					postJsonObject.put("color", color);
					postJsonObject.put("completed", mCompletedCb.isChecked());
				} catch (JSONException e) {
					
					Toast.makeText(StickyNoteCreateActivity.this, "标签编辑失败.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ModifyStickyNoteTask mModifyStickyNoteTask=new ModifyStickyNoteTask(boardId, mStickyNoteInfo.getId(), postJsonObject.toString()){

					@Override
					public void before() {
						super.before();
						
						mProgressDialogUtils=new MyProgressDialogUtils("正在编辑...", StickyNoteCreateActivity.this);
						mProgressDialogUtils.showDialog();
					}

					@Override
					public void callback(String responseContent,
							Header[] headers) {
						super.callback(responseContent, headers);
						
						StickyNoteInfo stickyNoteInfo=new StickyNoteInfo();
						stickyNoteInfo.setId(mStickyNoteInfo.getId());
						stickyNoteInfo.setTitle(mContentEt.getText().toString());
						stickyNoteInfo.setColor(color);
						stickyNoteInfo.setCompleted(mCompletedCb.isChecked());
						stickyNoteInfo.setTimestamp(mStickyNoteInfo.getTimestamp());
						
						Intent intent=new Intent();
						intent.putExtra("StickyNoteInfo", stickyNoteInfo);
						setResult(Constant.RESULT_CODE_STICKY_NOTE_MODIFY, intent);
						finish();
						
						Toast.makeText(StickyNoteCreateActivity.this, "编辑成功.", Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void failure(int errorCode, String responseContent,
							Header[] headers) {
						super.failure(errorCode, responseContent, headers);
						
						Toast.makeText(StickyNoteCreateActivity.this, "编辑失败.", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void complete() {
						super.complete();
						
						mProgressDialogUtils.dismissDialog();
					}
					
				};
				HttpNetworkNormalManager.getInstance().addRunningtask(mModifyStickyNoteTask);
			}
			
			break;
		case R.id.mDeleteBtn:
			
			DeleteStickyNoteTask mDeleteStickyNoteTask=new DeleteStickyNoteTask(boardId, mStickyNoteInfo.getId()){

				@Override
				public void before() {
					super.before();
					
					mProgressDialogUtils=new MyProgressDialogUtils("正在删除...", StickyNoteCreateActivity.this);
					mProgressDialogUtils.showDialog();
				}

				@Override
				public void callback(String responseContent, Header[] headers) {
					super.callback(responseContent, headers);
					
					Toast.makeText(StickyNoteCreateActivity.this, "删除成功.", Toast.LENGTH_SHORT).show();
					
					StickyNoteInfo stickyNoteInfo=new StickyNoteInfo();
					stickyNoteInfo.setId(mStickyNoteInfo.getId());
					stickyNoteInfo.setTitle(mContentEt.getText().toString());
					stickyNoteInfo.setColor(color);
					stickyNoteInfo.setCompleted(mCompletedCb.isChecked());
					stickyNoteInfo.setTimestamp(mStickyNoteInfo.getTimestamp());
					
					Intent intent=new Intent();
					intent.putExtra("StickyNoteInfo", stickyNoteInfo);
					intent.putExtra("deleted", true);
					setResult(Constant.RESULT_CODE_STICKY_NOTE_MODIFY, intent);
					finish();
				}

				@Override
				public void failure(int errorCode, String responseContent,
						Header[] headers) {
					super.failure(errorCode, responseContent, headers);
					
					Toast.makeText(StickyNoteCreateActivity.this, "删除失败.", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void complete() {
					super.complete();
					
					mProgressDialogUtils.dismissDialog();
				}
				
			};
			HttpNetworkNormalManager.getInstance().addRunningtask(mDeleteStickyNoteTask);
			
			break;
		case R.id.mLeftExitConatiner:
			
			finish();
			
			break;
		default:
			break;
		}
	}
}
