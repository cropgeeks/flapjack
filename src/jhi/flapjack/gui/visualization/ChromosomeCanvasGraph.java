// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.visualization;

import java.awt.*;
import javax.swing.*;

import jhi.flapjack.data.*;

import scri.commons.gui.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

class ChromosomeCanvasGraph extends JPanel
{
	private ChromosomeCanvas chromCanvas;

	private JFreeChart chart;

	ChromosomeCanvasGraph(ChromosomeCanvas chromCanvas)
	{
		this.chromCanvas = chromCanvas;

		setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		ChartPanel panel = new ChartPanel(chart = createChart());
		panel.setMaximumDrawWidth(2000);
		panel.setMaximumDrawHeight(500);
		add(panel);

		display(-1);
	}

	public Dimension getPreferredSize()
		{ return new Dimension(0, 150); }

	void display(int chromosomeIndex)
	{
		// If no chromosome is being displayed:
		if (chromosomeIndex == -1)
		{
			setChartData(null);
			setBorder(BorderFactory.createTitledBorder(
				RB.getString("flapjack.gui.visualization.ChromosomePanel.graphTitle1")));
		}

		// Otherwise, build the graph data for it:
		else
		{
			GTView view = chromCanvas.views.get(chromosomeIndex);
			// Work out how many centimorgans each bin will represent
			int nBins = 500;
			double cmPerPixel = view.mapLength() / nBins;

			float[] values = chromCanvas.calculateMarkersPerPixel(view, cmPerPixel, nBins);

			double[][] data = new double[values.length][2];
			for (int i = 0; i < values.length; i++)
			{
				data[i][0] = i * cmPerPixel;
				data[i][1] = values[i];
			}

			chart.getXYPlot().getRangeAxis().setAutoRange(true);
			chart.getXYPlot().getDomainAxis().setAutoRange(true);

			setChartData(data);

			setBorder(BorderFactory.createTitledBorder(
				RB.format("flapjack.gui.visualization.ChromosomePanel.graphTitle2",
				chromCanvas.views.get(chromosomeIndex).getChromosomeMap().getName())));
		}

		repaint();
	}

	private void setChartData(double[][] data)
	{
		XYSeries series = new XYSeries("");

		if (data != null)
			for (int i = 0; i < data.length; i++)
				series.add(data[i][0], data[i][1]);

		XYSeriesCollection coll = new XYSeriesCollection(series);
		chart.getXYPlot().setDataset(coll);

		displayAxis(data != null);
	}

	private void displayAxis(boolean visible)
	{
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis range = plot.getRangeAxis();
		range.setVisible(visible);
		range = plot.getDomainAxis();
		range.setVisible(visible);
	}

	private JFreeChart createChart() {
		JFreeChart chart = ChartFactory.createXYAreaChart(null, null, // xaxis title
		null, // yaxis title
		null, PlotOrientation.VERTICAL, true, true, false);

		//setChartData(this.data);

		RenderingHints rh;
		rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		else
//			rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		chart.setRenderingHints(rh);
		chart.removeLegend();

		XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

//		(plot.getRenderer()).setPaint(Prefs.gui_graph_color);



		// plot.setDomainGridlinesVisible(false);
		// plot.setRangeGridlinesVisible(false);

		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		// xAxis.setLowerBound(1);
		// xAxis.setUpperBound(data[data.length-1]);
//		xAxis.setTickLabelFont(new JLabel().getFont());
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
//		yAxis.setLowerBound(0);

		yAxis.setTickLabelFont(new JLabel().getFont());
//		yAxis.set



		// Set the height of the graph to show 5% above the maximum value
		//adjustUpperYBound();

		// And set the width of the graph to fit the data exactly
//		xAxis.setLowerBound(0);
//		xAxis.setUpperBound(aData.getSequenceSet().getLength());

		return chart;
	}
}