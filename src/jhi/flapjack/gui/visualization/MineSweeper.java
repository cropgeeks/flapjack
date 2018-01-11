// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

class MineSweeper extends MouseAdapter implements IOverlayRenderer
{
	private GenotypeCanvas canvas;

	private MinePoint[][] grid;
	private int gridCount;

	private int boxW, boxH;
	private int x1, y1, x2, y2;

	// Font width and height
	private Font font;
	private int fW, fH;

	// Game variables
	private int totalMines;
	private int minesFlagged;
	private int nonMinesCleared;
	private boolean gameOverLost;
	private boolean gameOverWon;

	private CanvasMouseListener mouseListener;


	MineSweeper(GenotypeCanvas canvas, CanvasMouseListener mouseListener)
	{
		// TODO: Stick in an option to disable minesweeper!

		this.canvas = canvas;
		this.mouseListener = mouseListener;

		canvas.overlays.add(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);

		init();
	}

	private void init()
	{
		boxW = canvas.boxW;
		boxH = canvas.boxH;

		x1 = 2 + canvas.pX1 / boxW;
		y1 = 2 + canvas.pY1 / boxH;

		x2 = canvas.boxCountX - 4;
		y2 = canvas.boxCountY - 4;

		createGrid(x2, y2);

		int size = 0;
		while (true)
		{
			font = new Font("Monospaced", Font.BOLD, ++size);
			FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
				.getGraphics().getFontMetrics(font);

			if (fm.charWidth('8') > boxW || fm.getHeight() > boxH)
			{
				fW = fm.charWidth('8');
				fH = fm.getHeight();

				break;
			}
		}

		canvas.repaint();
	}

	private void createGrid(int xCount, int yCount)
	{
		// Create the grid
		grid = new MinePoint[xCount][yCount];
		gridCount = xCount * yCount;

		// And initialise it
		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid[x].length; y++)
			{
				grid[x][y] = new MinePoint();

				// Work out its exact x/y position on the canvas
				grid[x][y].x = boxW * (x1 + x);
				grid[x][y].y = boxH * (y1 + y);
			}

		// Decide how many mines we want
		totalMines = (int) (gridCount * 0.15);

		// And then randomly assign them to the board
		Random r = new Random();
		for (int i = 0; i < totalMines; i++)
		{
			boolean placedMine = false;

			while (placedMine == false)
			{
				int x = r.nextInt(xCount);
				int y = r.nextInt(yCount);

				if (grid[x][y].isMine == false)
					grid[x][y].isMine = placedMine = true;
			}
		}

		// Now work out the mine count for each adjacent point
		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid[x].length; y++)
			{
				if (grid[x][y].isMine)
					continue;

				for (int col = y-1; col <= y+1; col++)
					for (int row = x-1; row <= x+1; row++)
					{
						try
						{
							if (grid[row][col].isMine)
								grid[x][y].mineCount++;
						}
						catch (ArrayIndexOutOfBoundsException e) {}
					}
			}

		// Reset other game variables
		gameOverLost = gameOverWon = false;
		minesFlagged = 0;
		nonMinesCleared = 0;
	}

	public void mouseClicked(MouseEvent e)
	{
		// Disable and return to normal mode
		if (e.isControlDown() && e.isAltDown() && e.getClickCount() == 2)
		{
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			canvas.overlays.remove(this);

			canvas.addMouseListener(mouseListener);
		}

		// Reset the game
		else if (e.isAltDown() && e.getClickCount() == 2)
			init();
	}

	public void mouseReleased(MouseEvent e)
	{
		// Determine the indices within the mine grid that were clicked
		int x = e.getPoint().x / boxW - x1;
		int y = e.getPoint().y / boxH - y1;

		// Ignore it if it's outside of the grid
		if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length)
		{
			canvas.repaint();
			return;
		}

		if (!gameOverWon && !gameOverLost)
			grid[x][y].click(e.isMetaDown());

		canvas.repaint();
	}

	// Cascade algorithm for clearing squares:
	//  1: Count mines adjacent to current square
	//  2: If adjacent mine count is zero, uncover all adjacent covered squares
	//     and make a recursive call for every one of them
	void cascade(MinePoint m)
	{
		// Search the array for this point
		int x = 0, y = 0;
		for (int col = 0; col < grid.length; col++)
			for (int row = 0; row < grid[col].length; row++)
				if (grid[col][row] == m)
				{
					x = col;
					y = row;
					break;

				}

		if (m.mineCount == 0)
		{
			for (int col = y-1; col <= y+1; col++)
				for (int row = x-1; row <= x+1; row++)
				{
					try
					{
						if (grid[row][col].isClear == false)
						{
							grid[row][col].setClear();
							cascade(grid[row][col]);
						}
					}
					catch (ArrayIndexOutOfBoundsException e) {}
				}
		}
	}

	public void render(Graphics2D g)
	{
		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid[x].length; y++)
			{
				grid[x][y].draw(g);
			}

		g.setStroke(new BasicStroke(4));
		g.setColor(Color.red);
		g.drawRect(x1*boxW, y1*boxH, grid.length*boxW, grid[0].length*boxH);
	}


	private class MinePoint
	{
		// This point's exact position on the canvas (not its array indices)
		int x, y;

		// Is there a mine on this location?
		boolean isMine = false;
		// How many mines are next to it?
		int mineCount = 0;
		// Has it been cleared?
		boolean isClear = false;
		// What is its current state? 1=normal, 2=marked, 3=unsure
		int state = 1;

		void setClear()
		{
			if (isClear == false)
			{
				isClear = true;
				nonMinesCleared++;
			}
		}

		void click(boolean rightClick)
		{
			// Left click
			if (rightClick == false)
			{
				if (isMine)
					gameOverLost = true;

				else if (isClear == false)
				{
					setClear();

					// We don't cascade clear if it has a number
					if (mineCount == 0)
						cascade(this);
				}
			}
			// Right click
			else
			{
				// If a mine that WAS flagged, isn't any more...
				if (isMine && state == 2)
					minesFlagged--;

				// Otherwise, rotate its state
				state++;
				if (state == 4)
					state = 1;

				// If a mine has now BEEN flagged...
				if (isMine && state == 2)
					minesFlagged++;
			}

			if (minesFlagged + nonMinesCleared == gridCount)
			{
				gameOverWon = true;
				canvas.repaint();

				javax.swing.JOptionPane.showMessageDialog(jhi.flapjack.gui.Flapjack.winMain, "Well done! Now do some work.");
			}
		}

		void draw(Graphics2D g)
		{
			g.setColor(Color.black);
			g.setFont(font);

			int fx = x + (int) (boxW/2) - (fW/2);
			int fy = y + (int) (boxH/4) + (fH/2);

			if (isMine && gameOverLost)
				g.drawString("B", fx, fy);

			else if (isClear && mineCount == 0)
			{
				g.setPaint(new Color(235, 235, 235, 175));
				g.fillRect(x, y, boxW, boxH);
			}
			else if (isClear)
			{
				g.setPaint(new Color(235, 235, 235, 175));
				g.fillRect(x, y, boxW, boxH);

				switch (mineCount)
				{
					case 1: g.setPaint(Color.blue); break;
					case 2: g.setPaint(Color.green); break;
					case 3: g.setPaint(Color.red); break;
					case 4: g.setPaint(Color.blue.darker()); break;

					default: g.setPaint(Color.red.darker());
				}

				g.drawString("" + mineCount, fx, fy);
			}

			else if (state == 2)
			{
				g.drawString("F", fx, fy);
			}

			else if (state == 3)
			{
				g.drawString("?", fx, fy);
			}

			g.setColor(Color.black);
			g.drawRect(x, y, boxW, boxH);
		}
	}
}