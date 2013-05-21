package com.drawpad.interfaces;

public interface IUndoCommand {
	public void undo();

	public boolean canUndo();

	public void onDeleteFromUndoStack();

	public void onDeleteFromRedoStack();
}