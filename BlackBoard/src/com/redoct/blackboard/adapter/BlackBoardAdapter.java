package com.redoct.blackboard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.redoct.blackboard.R;

public class BlackBoardAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	private int imgMaxNum;
	
	public BlackBoardAdapter(Context mContext) {
		super();

		this.imgMaxNum=imgMaxNum;
		
		inflater=LayoutInflater.from(mContext);
		
		/*mImageLoader=ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moment_pic_loading)
		.showImageForEmptyUri(R.drawable.moment_pic_loading)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();*/
	}

	@Override
	public int getCount() {
		
		/*if(imgUrList==null){
			return 0;
		}else {
			
			return imgUrList.size();
		}*/
		
		return 8;
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
            
            holder.mBlackBoardBtn= (Button) convertView.findViewById(R.id.mBlackBoardBtn);
            holder.mDeleteView=(ImageView)convertView.findViewById(R.id.mDeleteView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
    	 
    	holder.mBlackBoardBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				gotoDetails(pos);
			}
		});
    	
    	holder.mBlackBoardBtn.setBackgroundColor(Color.YELLOW);
    	holder.mBlackBoardBtn.setText(""+pos);
        
		return convertView;
	}
	
	public void gotoDetails(int position){
		
	}
	
	public void delete(int position){
		
	}
	
	class ViewHolder {

        Button mBlackBoardBtn;
        ImageView mDeleteView;
    }

}


