package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class DeleteGreenBoardTask extends IHttpTask {
	
	private String boardId;

	public DeleteGreenBoardTask(String boardId) {
		super();
		this.boardId = boardId;
	}

	@Override
	public int getMethod() {

		return DELETE;
	}

	@Override
	public String getUrl() {

		return HTTP_URL+"/greenboards/"+boardId;
	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
