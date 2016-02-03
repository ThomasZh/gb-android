package com.redoct.blackboard.fragment;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.redoct.blackboard.R;
import com.redoct.blackboard.activity.GreenBoardDetailsActivity;
import com.redoct.blackboard.activity.StickyNoteCreateActivity;
import com.redoct.blackboard.adapter.StickyNoteAdapter;
import com.redoct.blackboard.item.GreenBoardInfo;
import com.redoct.blackboard.item.StickyNoteInfo;
import com.redoct.blackboard.network.HttpNetworkNormalManager;
import com.redoct.blackboard.task.QueryAllGreenBoardsTask;
import com.redoct.blackboard.task.QueryAllStickyNotesTask;
import com.redoct.iclub.config.Constant;

public class UnCompletedStickyNoteFragment extends Fragment {
	
	private PullToRefreshGridView mPullToRefreshGridView;
	private StickyNoteAdapter mStickyNoteAdapter;
	private ArrayList<StickyNoteInfo> mStickyNoteInfos=new ArrayList<StickyNoteInfo>();
	
	private int mode;
	private long time;
	private GreenBoardInfo mGreenBoardInfo;
	private int optionIndex;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.fragment_uncompleted_stickynotes,
				container, false);
		
		mGreenBoardInfo=(GreenBoardInfo) getArguments().getSerializable("GreenBoardInfo");
		
		mPullToRefreshGridView=(PullToRefreshGridView)contentView.findViewById(R.id.mPullToRefreshGridView);
		mPullToRefreshGridView.setMode(Mode.BOTH);
		mStickyNoteAdapter=new StickyNoteAdapter(getActivity(),mStickyNoteInfos){

			@Override
			public void gotoDetails(int position) {
				super.gotoDetails(position);
				
				if(position==0){   //创建一个新stickynote
					
					optionIndex=-1;
					
					Intent intent=new Intent(getActivity(),StickyNoteCreateActivity.class);
					intent.putExtra("boardId", mGreenBoardInfo.getId());
					startActivityForResult(intent, Constant.RESULT_CODE_STICKY_NOTE_MODIFY);
					
				}else {   //查看stickynote详情
					
					optionIndex=position;
					
					Intent intent=new Intent(getActivity(),StickyNoteCreateActivity.class);
					intent.putExtra("boardId", mGreenBoardInfo.getId());
					intent.putExtra("StickyNoteInfo", mStickyNoteInfos.get(position));
					startActivityForResult(intent, Constant.RESULT_CODE_STICKY_NOTE_MODIFY);
				}
			}
			
		};
		mPullToRefreshGridView.setAdapter(mStickyNoteAdapter);
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

				if(mStickyNoteInfos==null||mStickyNoteInfos.size()==0){
					
					mode=QueryAllGreenBoardsTask.MODE_FIRST;
					time=System.currentTimeMillis();
				}else {
					mode=QueryAllGreenBoardsTask.MODE_BEFORE;
					time=mStickyNoteInfos.get(mStickyNoteInfos.size()-1).getTimestamp();
				}

				loadData();
			}
		});
		
		mode=QueryAllGreenBoardsTask.MODE_FIRST;
		time=System.currentTimeMillis();
		loadData();
		
		return contentView;
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

		if (requestCode == Constant.RESULT_CODE_STICKY_NOTE_MODIFY) {

			Log.e("zyf", "Constant.RESULT_CODE_STICKY_NOTE_MODIFY......from UnCompletedStickyNoteFragment.....");
			
			StickyNoteInfo stickyNoteInfo=(StickyNoteInfo) data.getSerializableExtra("StickyNoteInfo");
			boolean deleted=data.getBooleanExtra("deleted", false);
			
			if(optionIndex==-1){//创建
				
				mStickyNoteInfos.add(1, stickyNoteInfo);
			}else {
				
				if(deleted){ //已经删除
					
					mStickyNoteInfos.remove(optionIndex);
				}else {
					
					if(stickyNoteInfo.isCompleted()){   //已经“完成”，需要从列表中删除
						
						mStickyNoteInfos.remove(optionIndex);
					}else{
						
						mStickyNoteInfos.set(optionIndex, stickyNoteInfo);
					}
				}
			}
			
			mStickyNoteAdapter.notifyDataSetChanged();
		}
	}
	
	private void loadData(){
		
		QueryAllStickyNotesTask mQueryAllStickyNotesTask=new QueryAllStickyNotesTask(time,mode,mGreenBoardInfo.getId(),false){

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
					StickyNoteInfo stickyNoteInfo;
					
					if(mode==QueryAllGreenBoardsTask.MODE_FIRST){
						mStickyNoteInfos.clear();
					}
					
					for(int i=0;i<totalArray.length();i++){
						
						jsonObject=totalArray.getJSONObject(i);
						
						stickyNoteInfo=new StickyNoteInfo();
						stickyNoteInfo.setId(jsonObject.optString("id"));
						stickyNoteInfo.setTitle(jsonObject.optString("title"));
						stickyNoteInfo.setColor(jsonObject.optString("color"));
						stickyNoteInfo.setCompleted(jsonObject.optBoolean("completed"));
						stickyNoteInfo.setTimestamp(jsonObject.optLong("timestamp"));
						
						mStickyNoteInfos.add(stickyNoteInfo);
					}
					
					if(mode==QueryAllGreenBoardsTask.MODE_FIRST){
						mStickyNoteInfos.add(0,new StickyNoteInfo());
					}
					
					mStickyNoteAdapter.notifyDataSetChanged();
					
				} catch (Exception e) {
					
					Log.e("zyf", "QueryAllStickyNotesTask exception: "+e.toString());
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
		HttpNetworkNormalManager.getInstance().addRunningtask(mQueryAllStickyNotesTask);
	}
	
	
}
