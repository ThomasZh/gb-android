package com.redoct.blackboard.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import com.redoct.blackboard.R;
import com.redoct.blackboard.adapter.StickyNoteAdapter.ViewHolder;
import com.redoct.blackboard.item.StickyNoteInfo;

@SuppressLint("NewApi") 
public class CompletedStickyNoteAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private ArrayList<StickyNoteInfo> mStickyNoteInfos;
	
	private Context mContext;
	
	public CompletedStickyNoteAdapter(Context mContext,ArrayList<StickyNoteInfo> mStickyNoteInfos) {
		super();
		
		this.mContext=mContext;
		
		this.mStickyNoteInfos=mStickyNoteInfos;
		
		inflater=LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		
		if(mStickyNoteInfos==null){
			return 0;
		}else {
			
			return mStickyNoteInfos.size();
		}
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		final int pos=position;
		StickyNoteInfo item=mStickyNoteInfos.get(pos);
		
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_stickynote, parent, false);
            holder = new ViewHolder();
            
            holder.mStickyNoteBtn= (Button) convertView.findViewById(R.id.mStickyNoteBtn);
            holder.mDeleteView=(ImageView)convertView.findViewById(R.id.mDeleteView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
    	 
    	holder.mStickyNoteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				gotoDetails(pos);
			}
		});
    	
   /* 	if(pos==0){
    		holder.mStickyNoteBtn.setTextSize(30);
    		holder.mStickyNoteBtn.setText("+");
    		
    		holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
    	}else {*/
    		holder.mStickyNoteBtn.setTextSize(14);
    		holder.mStickyNoteBtn.setText(""+mStickyNoteInfos.get(pos).getTitle());
    		
    		if(item.getColor().equals("yellow")){
    			
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
    			
    		}else if(item.getColor().equals("green")){
    			
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.green));
    			
    		}else if(item.getColor().equals("blue")){
    			
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
    			
    		}else if(item.getColor().equals("purple")){
    			
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.purple));
    			
    		}else if(item.getColor().equals("red")){
    			
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.red));
    			
    		}else {
    			holder.mStickyNoteBtn.setBackgroundColor(mContext.getResources().getColor(R.color.black));
			}
		//}
    	
    	if(pos%2!=0){
    		holder.mStickyNoteBtn.setRotation(8);
    	}else {
    		holder.mStickyNoteBtn.setRotation(-8);
		}
        
		return convertView;
	}
	
	public void gotoDetails(int position){
		
	}
	
	public void delete(int position){
		
	}
	
	class ViewHolder {

        Button mStickyNoteBtn;
        ImageView mDeleteView;
    }

}

