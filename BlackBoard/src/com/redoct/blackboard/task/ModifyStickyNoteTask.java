package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class ModifyStickyNoteTask extends IHttpTask {
	
	private String boardId;
	private String noteId;
	private String content;

	public ModifyStickyNoteTask(String boardId, String noteId, String content) {
		super();
		this.boardId = boardId;
		this.noteId = noteId;
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

		return HTTP_URL+"/greenboards/"+boardId+"/stickynotes/"+noteId;
	}

	@Override
	public boolean ifNeedSession() {

		return false;
	}

}
