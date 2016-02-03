package com.redoct.blackboard.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.redoct.blackboard.R;
import com.redoct.blackboard.fragment.CompletedStickyNoteFragment;
import com.redoct.blackboard.fragment.UnCompletedStickyNoteFragment;
import com.redoct.blackboard.item.GreenBoardInfo;
import com.redoct.blackboard.item.StickyNoteInfo;
import com.redoct.blackboard.network.HttpNetworkNormalManager;
import com.redoct.blackboard.task.DeleteGreenBoardTask;
import com.redoct.blackboard.task.ModifyGreenBoardTask;
import com.redoct.blackboard.util.MyProgressDialogUtils;
import com.redoct.blackboard.widget.GreenBoardCreateDialog;
import com.redoct.blackboard.widget.GreenBoardCreateDialog.MySubmmitListener;
import com.redoct.iclub.config.Constant;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GreenBoardDetailsActivity extends FragmentActivity implements OnClickListener{
	
	private TextView mTitleTv,mStatusTv;
	private ImageView mLeftView,mSwitchView;
	
	private GreenBoardInfo mGreenBoardInfo;
	
	private UnCompletedStickyNoteFragment mUnCompletedStickyNoteFragment;
	private CompletedStickyNoteFragment mCompletedStickyNoteFragment;
	
	private final int SHOW_UNCOMPLETED_STICKYNOTE=0;
	private final int SHOW_COMPLETED_STICKYNOTE=1;
	private int showMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_greenboard_details);
		
		mGreenBoardInfo=(GreenBoardInfo) getIntent().getSerializableExtra("GreenBoardInfo");
		
		initViews();
		
		showMode=SHOW_UNCOMPLETED_STICKYNOTE;
		updateUI();
	}
	
	private void initViews(){
		
		mTitleTv=(TextView)findViewById(R.id.mTitleTv);
		mTitleTv.setText(mGreenBoardInfo.getTitle());
		
		mStatusTv=(TextView)findViewById(R.id.mStatusTv);
		
		mLeftView=(ImageView)findViewById(R.id.mLeftView);
		
		findViewById(R.id.mLeftExitConatiner).setOnClickListener(this);
		findViewById(R.id.mEditContainer).setOnClickListener(this);
		findViewById(R.id.mSwitchContainer).setOnClickListener(this);
	}
	
	private void updateUI() {

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		/*transaction.setCustomAnimations(
			    R.anim.fragment_slide_right_in, R.anim.fragment_slide_left_out,
			    R.anim.fragment_slide_left_in, R.anim.fragment_slide_right_out);*/

		hideFragments(transaction);

		if (showMode == SHOW_UNCOMPLETED_STICKYNOTE) {   
			
			if (mUnCompletedStickyNoteFragment == null) {
				mUnCompletedStickyNoteFragment = new UnCompletedStickyNoteFragment();
				Bundle bundle=new Bundle();
				bundle.putSerializable("GreenBoardInfo", mGreenBoardInfo);
				mUnCompletedStickyNoteFragment.setArguments(bundle);

				transaction.add(R.id.contentLayout, mUnCompletedStickyNoteFragment);
			} else {
				transaction.show(mUnCompletedStickyNoteFragment);
			}
		}else{
			
			if (mCompletedStickyNoteFragment == null) {
				mCompletedStickyNoteFragment = new CompletedStickyNoteFragment();
				Bundle bundle=new Bundle();
				bundle.putSerializable("GreenBoardInfo", mGreenBoardInfo);
				mCompletedStickyNoteFragment.setArguments(bundle);

				transaction.add(R.id.contentLayout, mCompletedStickyNoteFragment);
			} else {
				transaction.show(mCompletedStickyNoteFragment);
			}
		}
		
		transaction.commit();
	}
	
	private void hideFragments(FragmentTransaction transaction) {

		if (mUnCompletedStickyNoteFragment != null) {
			transaction.hide(mUnCompletedStickyNoteFragment);
		}
		
		if (mCompletedStickyNoteFragment != null) {
			transaction.hide(mCompletedStickyNoteFragment);
		}
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.mLeftExitConatiner:
			
			Intent intent=new Intent();
			intent.putExtra("GreenBoardInfo", mGreenBoardInfo);
			intent.putExtra("deleted", false);
			setResult(Constant.RESULT_CODE_GREEN_BOARD_MODIFY, intent);
			
			finish();
			
			break;
		case R.id.mEditContainer:
			
			final Dialog dialog = new Dialog(GreenBoardDetailsActivity.this);
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog_more_function);
			dialog.setTitle(null);
			dialog.show();
			dialog.findViewById(R.id.mEditTv).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					
					modifyGreenBoard();
				}
			});
			dialog.findViewById(R.id.mDeleteTv).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					
					deleteGreenBoard();
				}
			});
			
			break;
		case R.id.mSwitchContainer:
			
			if(showMode==SHOW_UNCOMPLETED_STICKYNOTE){
				
				showMode=SHOW_COMPLETED_STICKYNOTE;
				mStatusTv.setText("未完成");
			}else {
				
				showMode=SHOW_UNCOMPLETED_STICKYNOTE;
				mStatusTv.setText("已完成");
			}
			
			updateUI();
			
			break;

		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent=new Intent();
			intent.putExtra("GreenBoardInfo", mGreenBoardInfo);
			intent.putExtra("deleted", false);
			setResult(Constant.RESULT_CODE_GREEN_BOARD_MODIFY, intent);
			
			finish();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	private void modifyGreenBoard(){
		
		final GreenBoardCreateDialog mDialog=new GreenBoardCreateDialog(GreenBoardDetailsActivity.this, R.style.MyCustomDialog);
		mDialog.setMySubmmitListener(new MySubmmitListener() {
			
			@Override
			public void summit(final String content) {
				
				if(TextUtils.isEmpty(content)){
					
					Toast.makeText(GreenBoardDetailsActivity.this, "输入的标题不可为空.", Toast.LENGTH_SHORT).show();
					return;
				}else {
					
					JSONObject postJsonObject=new JSONObject();
					try {
						postJsonObject.put("title", content);
						
					} catch (JSONException e) {
						
						Toast.makeText(GreenBoardDetailsActivity.this, "编辑失败.", Toast.LENGTH_SHORT).show();
						return;
					}
					ModifyGreenBoardTask mModifyGreenBoardTask=new ModifyGreenBoardTask(mGreenBoardInfo.getId(),postJsonObject.toString()){

						@Override
						public void before() {
							super.before();
							
							mProgressDialogUtils=new MyProgressDialogUtils("正在编辑...", GreenBoardDetailsActivity.this);
							mProgressDialogUtils.showDialog();
						}

						@Override
						public void callback(String responseContent,
								Header[] headers) {
							super.callback(responseContent, headers);
							
							Log.e("zyf","green board modify success....");
							
							Toast.makeText(GreenBoardDetailsActivity.this, "编辑成功.", Toast.LENGTH_SHORT).show();
							
							mTitleTv.setText(content);
							
							mGreenBoardInfo.setTitle(content);
						}

						@Override
						public void failure(int errorCode,
								String responseContent, Header[] headers) {
							super.failure(errorCode, responseContent, headers);
							
							Log.e("zyf","green board modify failed....");
							Toast.makeText(GreenBoardDetailsActivity.this, "编辑失败.", Toast.LENGTH_SHORT).show();
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
		mDialog.initContent(mGreenBoardInfo.getTitle());
	}
	
	private MyProgressDialogUtils mProgressDialogUtils;
	private void deleteGreenBoard(){
		
		DeleteGreenBoardTask mDeleteGreenBoardTask=new DeleteGreenBoardTask(mGreenBoardInfo.getId()){

			@Override
			public void before() {
				super.before();
				
				mProgressDialogUtils=new MyProgressDialogUtils("正在删除...", GreenBoardDetailsActivity.this);
				mProgressDialogUtils.showDialog();
			}

			@Override
			public void callback(String responseContent, Header[] headers) {
				super.callback(responseContent, headers);
				
				Toast.makeText(GreenBoardDetailsActivity.this, "删除成功.", Toast.LENGTH_SHORT).show();
				
				Intent intent=new Intent();
				intent.putExtra("GreenBoardInfo", new GreenBoardInfo());
				intent.putExtra("deleted", true);
				setResult(Constant.RESULT_CODE_GREEN_BOARD_MODIFY, intent);
				
				finish();
			}

			@Override
			public void failure(int errorCode, String responseContent,
					Header[] headers) {
				super.failure(errorCode, responseContent, headers);
				
				Toast.makeText(GreenBoardDetailsActivity.this, "删除失败.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void complete() {
				super.complete();
				
				mProgressDialogUtils.dismissDialog();
			}
			
		};
		HttpNetworkNormalManager.getInstance().addRunningtask(mDeleteGreenBoardTask);
	}
}
