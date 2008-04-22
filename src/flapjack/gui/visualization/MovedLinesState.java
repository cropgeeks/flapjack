package flapjack.gui.visualization;

import flapjack.data.*;
import flapjack.gui.*;

public class MovedLinesState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The state (that is, "order") of the lines before they were moved about
	private int[] undoLines;
	// And the state after the movement was finished
	private int[] redoLines;

	public MovedLinesState(GTViewSet viewSet, String menuStr)
	{
		this.viewSet = viewSet;
		this.menuStr = menuStr;
	}

	public String getMenuString()
	{
		return menuStr;
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