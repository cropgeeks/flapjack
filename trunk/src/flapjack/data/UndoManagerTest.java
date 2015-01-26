// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package flapjack.data;

import java.io.*;

import junit.framework.*;

import flapjack.gui.visualization.*;
import flapjack.gui.visualization.undo.*;
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
//		org.junit.runner.JUnitCore.main("flapjack.data.UndoManagerTest");
	}

	private void load()
		throws Exception
	{
		File mapFile = new File("tests\\tiny.map");
		File genoFile = new File("tests\\tiny.data");

		ChromosomeMapImporter mapImporter
			= new ChromosomeMapImporter(mapFile, dataSet);
		GenotypeDataImporter genoImporter = new GenotypeDataImporter(genoFile,
			dataSet, mapImporter.getMarkersHashMap(), "", true, "/");

		mapImporter.importMap();
		genoImporter.importGenotypeData(false);
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
/*
	public void testAddingUndoState1()
	{
		MovedLinesState state = new MovedLinesState(viewSet, null);
		state.createUndoState();
		// simulate moving lines with a forced update
		viewSet.setLinesFromArray(createArray(new int[] { 4, 3, 2, 1, 0 }), true);
		state.createRedoState();
		manager.addUndoState(state);

		assertEquals(manager.getStack().size(), 1);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);

		LineInfo[] lines = viewSet.getLinesAsArray(true);
		LineInfo[] expected = createArray(new int[] { 4, 3, 2, 1, 0 });
		assertEquals(compareArrays(lines, expected), true);
	}

	public void testAddingUndoState2()
	{
		MovedLinesState state = new MovedLinesState(viewSet, null);
		state.createUndoState();
		// simulate moving lines with a forced update
		viewSet.setLinesFromArray(createArray(new int[] { 2, 4, 0, 1, 3 }), true);
		state.createRedoState();
		manager.addUndoState(state);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);

		LineInfo[] lines = viewSet.getLinesAsArray(true);
		LineInfo[] expected = createArray(new int[] { 2, 4, 0, 1, 3 });
		assertEquals(compareArrays(lines, expected), true);
	}

	public void testRunningUndo()
	{
		manager.processUndo();

		LineInfo[] lines = viewSet.getLinesAsArray(true);
		LineInfo[] expected = createArray(new int[] { 4, 3, 2, 1, 0 });
		assertEquals(compareArrays(lines, expected), true);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), true);
	}

	public void testRunningRedo()
	{
		manager.processRedo();

		LineInfo[] lines = viewSet.getLinesAsArray(true);
		LineInfo[] expected = createArray(new int[] { 2, 4, 0, 1, 3 });
		assertEquals(compareArrays(lines, expected), true);

		assertEquals(manager.getStack().size(), 2);
		assertEquals(manager.canUndo(), true);
		assertEquals(manager.canRedo(), false);
	}

	public void testRunningUndoAll()
	{
		manager.processUndo();
		manager.processUndo();

		LineInfo[] lines = viewSet.getLinesAsArray(true);
		LineInfo[] expected = createArray(new int[] { 0, 1, 2, 3, 4 });
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

	// Quick method to make (fake) LineInfo arrays storing the index positions
	// we want to test (the Line info doesn't matter, hence null)
	private LineInfo[] createArray(int[] indices)
	{
		LineInfo[] array = new LineInfo[indices.length];
		for (int i = 0; i < array.length; i++)
			array[i] = new LineInfo(null, indices[i]);

		return array;
	}

	private boolean compareArrays(LineInfo[] array1, LineInfo[] array2)
	{
		if (array1.length != array2.length)
			return false;

		for (int i = 0; i < array1.length; i++)
			if (array1[i].index != array2[i].index)
				return false;

		return true;
	}
*/
}