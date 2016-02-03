package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class CreateStickyNoteTask extends IHttpTask {
	
	private String boardId;
	private String content;
	
	public CreateStickyNoteTask(String boardId, String content) {
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

		return POST;
	}

	@Override
	public String getUrl() {

		return HTTP_URL+"/greenboards/"+boardId+"/stickynotes";
	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
