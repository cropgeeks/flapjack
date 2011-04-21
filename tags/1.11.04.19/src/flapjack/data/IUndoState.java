// Copyright 2007-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

/**
 * A Flapjack "undo" state actually holds two states - the state BEFORE the
 * action was carried out, and the state AFTER it was carried out. This allows
 * the undo manager to instantly apply undo/redo states without having to
 * perform the actual action. This differs from a normal implementation of undo/
 * redo where the undo/redo action will actually carry out the operation itself.
 * Flapjack's implementation requires more memory but is easy to manage.
 */
public interface IUndoState
{
	public String getMenuString();

	/**
	 * Returns the view that this operation relates to (or null if not view
	 * specific).
	 */
	public GTView getView();

	/** Creates ('remembers') the state before the action will happen. */
	public void createUndoState();

	/** Restores the object to its state before the last action. */
	public void applyUndoState();

	/** Creates ('remembers') the state after the action has happened. */
	public void createRedoState();

	/** Reapplies the object to its state after the last action. */
	public void applyRedoState();
}