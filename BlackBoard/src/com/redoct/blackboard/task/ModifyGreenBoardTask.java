package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class ModifyGreenBoardTask extends IHttpTask {
	
	private String boardId;
	
	private String content;

	public ModifyGreenBoardTask(String boardId, String content) {
		super();
		this.boardId = boardId;
		this.content = content;
	}

	@Override
	public String justTodo() {

		return content;
	}

	@Override
	public int getMethod() {

		return PUT;
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
