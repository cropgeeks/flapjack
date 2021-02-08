// Copyright 2007-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jhi.flapjack.io.*;

class FileDropAdapter extends DropTargetAdapter
{
	private WinMain winMain;

	FileDropAdapter(WinMain winMain)
	{
		this.winMain = winMain;
	}

	public void drop(DropTargetDropEvent dtde)
	{
		Transferable t = dtde.getTransferable();

		try
		{
			DataFlavor[] dataFlavors = t.getTransferDataFlavors();

			dtde.acceptDrop(DnDConstants.ACTION_COPY);

			for (int i = 0; i < dataFlavors.length; i++)
			{
				if (dataFlavors[i].isFlavorJavaFileListType())
				{
					List<?> list = (List<?>) t.getTransferData(dataFlavors[i]);

					final String[] filenames = new String[list.size()];
					for (int fn = 0; fn < filenames.length; fn++)
						filenames[fn] = list.get(fn).toString();


					Runnable r = () -> { winMain.mFile.handleDragDrop(filenames); };
					SwingUtilities.invokeLater(r);

					dtde.dropComplete(true);

					break;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}

	/*
	 * public void dropActionChanged(DropTargetDragEvent dtde) { }
	 *
	 * public void dragEnter(DropTargetDragEvent dtde) { }
	 *
	 * public void dragExit(DropTargetEvent dte) { }
	 *
	 * public void dragOver(DropTargetDragEvent dtde) { }
	 */
}