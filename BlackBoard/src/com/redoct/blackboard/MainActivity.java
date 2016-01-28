package com.redoct.blackboard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.redoct.blackboard.R;
import com.redoct.blackboard.adapter.BlackBoardAdapter;

public class MainActivity extends Activity {
	
	private GridView mGridView;
	private BlackBoardAdapter mBlackBoardAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mGridView=(GridView)findViewById(R.id.mGridView);
		mBlackBoardAdapter=new BlackBoardAdapter(this);
		mGridView.setAdapter(mBlackBoardAdapter);
	}
	
}
