package com.redoct.blackboard.task;

import com.redoct.blackboard.network.IHttpTask;

public class DeleteStickyNoteTask extends IHttpTask {
	
	private String boardId;
	private String noteId;

	public DeleteStickyNoteTask(String boardId, String noteId) {
		super();
		this.boardId = boardId;
		this.noteId = noteId;
	}

	@Override
	public int getMethod() {

		return DELETE;
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
