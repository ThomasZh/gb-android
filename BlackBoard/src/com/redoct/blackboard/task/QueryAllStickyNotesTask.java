package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class QueryAllStickyNotesTask extends IHttpTask {

	private String boardId;
	private long time;
	private int mode;
	private boolean completed;

	public QueryAllStickyNotesTask(long time, int mode,String boardId,boolean completed) {
		super();
		this.time = time;
		this.mode = mode;
		this.boardId=boardId;
		this.completed=completed;
	}

	@Override
	public int getMethod() {

		return GET;
	}

	@Override
	public String getUrl() {

		if(mode==MODE_FIRST){
			
			return HTTP_URL+"/greenboards/"+boardId+"/stickynotes?completed="+completed+"&before="+time+"&limit=20";
			
		}else if(mode==MODE_BEFORE){
			
			return HTTP_URL+"/greenboards/"+boardId+"/stickynotes?completed="+completed+"&before="+time+"&limit=20";
		}
		
		return "";

	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
