package com.redoct.blackboard.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.redoct.blackboard.R;

//添加备注时的提示框
public class GreenBoardCreateDialog extends Dialog implements android.view.View.OnClickListener{
	
	//上下文对象
	private Context mContext;
	
	private String title;
	
	//“确定”、“取消”按钮
	private Button mSummitBtn,mCancelBtn;
	
	private EditText mContentEt;
	
	//点击"确定"按钮时的监听器
	public interface MySubmmitListener{
		void summit(String content);
	}
	
	//点击"确定"按钮时的监听器
	private MySubmmitListener mSubmmitListener;
	
	//设置点击"确定"按钮时的监听器
	public void setMySubmmitListener(MySubmmitListener mSubmmitListener){
		this.mSubmmitListener=mSubmmitListener;
	}

	public GreenBoardCreateDialog(Context context,int theme) {
		super(context,theme);
		
		mContext=context;
	}
	
	public GreenBoardCreateDialog(Context context,int theme,String title) {
		super(context,theme);
		
		mContext=context;
		this.title=title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.custom_dialog_delete);
		
		mSummitBtn=(Button)findViewById(R.id.mSummitBtn);
		mCancelBtn=(Button)findViewById(R.id.mCancelBtn);
		
		mSummitBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		
		mContentEt=(EditText)findViewById(R.id.mContentEt);
		
		//控制整个Dialog显示的大小
		WindowManager.LayoutParams  lp=getWindow().getAttributes();
		WindowManager wm=(WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        int width=wm.getDefaultDisplay().getWidth();
        lp.width=width*5/6;
        getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		
		case R.id.mSummitBtn:    //“确定”
			
			if(mSubmmitListener!=null){    //回调“确定”按钮点击时的处理函数
				mSubmmitListener.summit(mContentEt.getText().toString());
			}
			
			break;
		case R.id.mCancelBtn:    //“取消”
			
			dismiss();
			
			break;

		default:
			break;
		}
	}
}
