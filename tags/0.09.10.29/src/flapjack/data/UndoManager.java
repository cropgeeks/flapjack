// Copyright 2007-2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package flapjack.data;

import java.util.*;

/**
 * Holds a stack of undoable state objects.
 */
public class UndoManager
{
	private LinkedList<IUndoState> stack = new LinkedList<IUndoState>();

	// The pointer should always be pointing to the most recent undo state that
	// can be applied
	private int stackPointer = -1;

	UndoManager()
	{
	}

	// Only used for JUnit testing
	LinkedList<IUndoState> getStack()
		{ return stack; }

	// Only used for JUnit testing
	int getStackPointer()
		{ return stackPointer; }

	public void addUndoState(IUndoState state)
	{
		// A new undo erases all subsequent redos that were on the stack
		for (int i = stack.size()-1; i > stackPointer; i--)
			stack.remove(i);

		stack.add(state);
		stackPointer++;

		// TODO: How large is the stack getting? Do we want to remove old items?
	}

	public boolean canUndo()
	{
		// Undo can be applied so long as elements are on the stack
		return stackPointer >= 0;
	}

	public String getNextUndoString()
	{
		if (canUndo())
			return stack.get(stackPointer).getMenuString();
		else
			return "";
	}

	/**
	 * Peeks at the stack for the next undo state, runs it, and returns it,
	 * leaving the element on the stack.
	 */
	public IUndoState processUndo()
	{
		IUndoState state = stack.get(stackPointer);
		state.applyUndoState();

		stackPointer--;

		return state;
	}

	public boolean canRedo()
	{
		// Redo can be applied when there are elements on the stack beyond the
		// current undoable position
		return (stack.size() > 0) && (stackPointer < stack.size()-1);
	}

	public String getNextRedoString()
	{
		if (canRedo())
			return stack.get(stackPointer+1).getMenuString();
		else
			return "";
	}

	/**
	 * Peeks at the stack for the next redo state, runs it, and returns it,
	 * leaving the element on the stack.
	 */
	public IUndoState processRedo()
	{
		stackPointer++;

		IUndoState state = stack.get(stackPointer);
		state.applyRedoState();

		return state;
	}
}