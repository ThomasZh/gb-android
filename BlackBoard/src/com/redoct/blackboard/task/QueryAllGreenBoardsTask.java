package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class QueryAllGreenBoardsTask extends IHttpTask {
	
	private long time;
	private int mode;

	public QueryAllGreenBoardsTask(long time, int mode) {
		super();
		this.time = time;
		this.mode = mode;
	}

	@Override
	public int getMethod() {

		return GET;
	}

	@Override
	public String getUrl() {

		if(mode==MODE_FIRST){
			
			return HTTP_URL+"/greenboards?before="+time+"&limit=20";
			
		}else if(mode==MODE_BEFORE){
			
			return HTTP_URL+"/greenboards?before="+time+"&limit=20";
		}
		
		return "";

	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
