package flapjack.data;

import java.io.*;

import junit.framework.*;

import flapjack.gui.visualization.*;
import flapjack.io.*;

/**
 * Tests basic undo manager functionality using the MovedLinesState undo class
 * from the flapjack.gui.visualization package
 * TODO: test MovedLinesState independantly?
 */
public class UndoManagerTest extends TestCase
{
	private static DataSet dataSet = new DataSet();
	private static GTViewSet viewSet;
	private static UndoManager manager;

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("flapjack.data.UndoManagerTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter
			= new GenotypeDataImporter(genoFile, dataSet, "", "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData();
	}

	public void testInitialState()
		throws Exception
	{
		load();

	 	viewSet = new GTViewSet(dataSet, "Default View");
		manager = viewSet.getUndoManager();

		// Initial state - manager should be an empty list
		assertEquals(manager.getStackPointer(), -1);
		assertEquals(manager.getStack().size(), 0);

		// With no actions possible
		assertEquals(manager.canUndo(), false);
		assertEquals(manager.canRedo(), false);
	}

	public void testAddingUndoState1()
	{
		MovedLinesState state = new MovedLinesState(viewSet);
		state.createUndoState();
		// simulate moving lines with a forced update
		viewSet.setLinesFromArray(new int[] { 4, 3, 2, 1, 0 });
		state.createRedoState();
		manager.addUndoState(state);

		assertEquals(manager.getStack().size(), 1);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);

		int[] lines = viewSet.getLinesAsArray();
		int[] expected = new int[] { 4, 3, 2, 1, 0 };
		assertEquals(compareArrays(lines, expected), true);
	}

	public void testAddingUndoState2()
	{
		MovedLinesState state = new MovedLinesState(viewSet);
		state.createUndoState();
		// simulate moving lines with a forced update
		viewSet.setLinesFromArray(new int[] { 2, 4, 0, 1, 3 });
		state.createRedoState();
		manager.addUndoState(state);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);

		int[] lines = viewSet.getLinesAsArray();
		int[] expected = new int[] { 2, 4, 0, 1, 3 };
		assertEquals(compareArrays(lines, expected), true);
	}

	public void testRunningUndo()
	{
		manager.processUndo();

		int[] lines = viewSet.getLinesAsArray();
		int[] expected = new int[] { 4, 3, 2, 1, 0 };
		assertEquals(compareArrays(lines, expected), true);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), true);
	}

	public void testRunningRedo()
	{
		manager.processRedo();

		int[] lines = viewSet.getLinesAsArray();
		int[] expected = new int[] { 2, 4, 0, 1, 3 };
		assertEquals(compareArrays(lines, expected), true);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);
	}

	public void testRunningUndoAll()
	{
		manager.processUndo();
		manager.processUndo();

		int[] lines = viewSet.getLinesAsArray();
		int[] expected = new int[] { 0, 1, 2, 3, 4 };
		assertEquals(compareArrays(lines, expected), true);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), false);
		assertEquals(manager.canRedo(), true);
	}

	public void testAddingFinalUndo()
	{
		// With the data back in its initial state, add a new operation (just
		// use the first test case)
		testAddingUndoState1();

		// Now, undo should be possible again, but no redo as the list will be
		// cleared
		assertEquals(manager.getStack().size(), 1);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);
	}

	private boolean compareArrays(int[] array1, int[] array2)
	{
		if (array1.length != array2.length)
			return false;

		for (int i = 0; i < array1.length; i++)
			if (array1[i] != array2[i])
				return false;

		return true;
	}
}