package flapjack.gui.visualization;

import flapjack.data.*;

public class MovedLinesState implements IUndoState
{
	private GTViewSet viewSet;
	private String menuStr;

	// The state (that is, "order") of the lines before they were moved about
	private LineInfo[] undoLines;
	// The comparisonLine (and index) before the move
	private Line undoComparisonLine;
	private int undoComparisonLineIndex;

	// And the state after the movement was finished
	private LineInfo[] redoLines;
	// The comparisonLine (and index) after the move
	private Line redoComparisonLine;
	private int redoComparisonLineIndex;


	public MovedLinesState(GTViewSet viewSet, String menuStr)
	{
		this.viewSet = viewSet;
		this.menuStr = menuStr;
	}

	public GTView getView()
		{ return null; }

	public String getMenuString()
	{
		return menuStr;
	}

	public void createUndoState()
	{
		undoLines = viewSet.getLinesAsArray();
		undoComparisonLine = viewSet.getComparisonLine();
		undoComparisonLineIndex = viewSet.getComparisonLineIndex();
	}

	public void applyUndoState()
	{
		viewSet.setLinesFromArray(undoLines);
		viewSet.setComparisonLine(undoComparisonLine);
		viewSet.setComparisonLineIndex(undoComparisonLineIndex);
	}

	public void createRedoState()
	{
		redoLines = viewSet.getLinesAsArray();
		redoComparisonLine = viewSet.getComparisonLine();
		redoComparisonLineIndex = viewSet.getComparisonLineIndex();
	}

	public void applyRedoState()
	{
		viewSet.setLinesFromArray(redoLines);
		viewSet.setComparisonLine(redoComparisonLine);
		viewSet.setComparisonLineIndex(redoComparisonLineIndex);
	}
}