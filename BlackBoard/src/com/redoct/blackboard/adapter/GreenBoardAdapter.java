package com.redoct.blackboard.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.redoct.blackboard.R;
import com.redoct.blackboard.item.GreenBoardInfo;

@SuppressLint("NewApi") 
public class GreenBoardAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private ArrayList<GreenBoardInfo> mGreenBoardInfos;
	
	public GreenBoardAdapter(Context mContext,ArrayList<GreenBoardInfo> mGreenBoardInfos) {
		super();
		
		this.mGreenBoardInfos=mGreenBoardInfos;
		
		inflater=LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		
		if(mGreenBoardInfos==null){
			return 0;
		}else {
			
			return mGreenBoardInfos.size();
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
		
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_blackboard, parent, false);
            holder = new ViewHolder();
            
            holder.mGreenBoardBtn= (Button) convertView.findViewById(R.id.mGreenBoardBtn);
            holder.mDeleteView=(ImageView)convertView.findViewById(R.id.mDeleteView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
    	 
    	holder.mGreenBoardBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				gotoDetails(pos);
			}
		});
    	
    	if(pos==0){
    		holder.mGreenBoardBtn.setTextSize(30);
    		holder.mGreenBoardBtn.setText("+");
    	}else {
    		holder.mGreenBoardBtn.setTextSize(16);
    		holder.mGreenBoardBtn.setText(""+mGreenBoardInfos.get(pos).getTitle());
		}
    	
    	/*if(pos%2!=0){
    		holder.mGreenBoardBtn.setRotation(8);
    	}else {
    		holder.mGreenBoardBtn.setRotation(-8);
		}*/
        
		return convertView;
	}
	
	public void gotoDetails(int position){
		
	}
	
	public void delete(int position){
		
	}
	
	class ViewHolder {

        Button mGreenBoardBtn;
        ImageView mDeleteView;
    }

}


