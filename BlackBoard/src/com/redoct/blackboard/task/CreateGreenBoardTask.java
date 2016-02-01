package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class CreateGreenBoardTask extends IHttpTask {
	
	private String content;

	public CreateGreenBoardTask(String content) {
		super();
		this.content = content;
	}

	@Override
	public String justTodo() {

		return content;
	}

	@Override
	public int getMethod() {

		return POST;
	}

	@Override
	public String getUrl() {

		return HTTP_URL+"/greenboards";
	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
