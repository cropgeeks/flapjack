package flapjack.gui.visualization;

import flapjack.data.*;
import flapjack.gui.*;

public class MovedLinesState implements IUndoState
{
	private GTViewSet viewSet;

	// The state (that is, "order") of the lines before they were moved about
	private int[] undoLines;
	// And the state after the movement was finished
	private int[] redoLines;

	public MovedLinesState(GTViewSet viewSet)
	{
		this.viewSet = viewSet;
	}

	public String getMenuString()
	{
		return "";
	}

	public void createUndoState()
	{
		undoLines = viewSet.getLinesAsArray();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromArray(undoLines);
	}

	public void createRedoState()
	{
		redoLines = viewSet.getLinesAsArray();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromArray(redoLines);
	}
}