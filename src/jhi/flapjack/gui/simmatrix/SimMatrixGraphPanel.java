// Copyright 2007-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.flapjack.gui.simmatrix;

import java.awt.*;
import java.awt.event.*;
import java.math.*;
import javax.swing.*;

import jhi.flapjack.data.*;
import jhi.flapjack.data.results.*;

import scri.commons.gui.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.statistics.*;
import org.jfree.data.xy.*;

public class SimMatrixGraphPanel extends JPanel
{
	private SimMatrix matrix;
	private JFreeChart chart;
	private SimMatrixGraphPanelNB controls;

	SimMatrixGraphPanel(SimMatrix matrix)
	{
		this.matrix = matrix;

		controls = new SimMatrixGraphPanelNB(this);

//		setBackground(Color.RED);
		setLayout(new BorderLayout());
		add(controls, BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				initData();
			}
		});
	}

	void initData()
	{
		SimpleHistogramDataset dataset = new SimpleHistogramDataset("Series 1");

		long s = System.currentTimeMillis();

		int numBins = controls.getNumBins();
		BigDecimal binSize = BigDecimal.valueOf(1d/numBins);
		System.out.println("binSize: " + binSize);

		for (int i = 0; i < numBins; i++)
		{
			BigDecimal b1 = binSize.multiply(BigDecimal.valueOf(i));
			BigDecimal b2 = b1.add(binSize);
//			System.out.println(i + ": " + b1 + " to " + b2);

			// We want this to be false except on the last loop iteration
			boolean includeUpperBound = (i == numBins-1);
			dataset.addBin(new SimpleHistogramBin(b1.doubleValue(), b2.doubleValue(), true, includeUpperBound));
		}


		int size = matrix.size();
		System.out.println("size is " + size);

		double[] data;

		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
			{
				if (j <= i)
					dataset.addObservation(matrix.valueAt(i, j));
				else
					dataset.addObservation(matrix.valueAt(j, i));
			}
//        dataset.addObservations(new double[] {20, 15, 42, 65, 54, 54, 80, 15,
  //              10, 45, 30, 28, 29, 14, 11, 46});


		long e = System.currentTimeMillis();
		System.out.println("Chart (data) creation time: " + (e-s) + " ms");

		ChartPanel panel = new ChartPanel(chart = createChart(dataset));
//		panel.setMaximumDrawWidth(2000);
//		panel.setMaximumDrawHeight(500);
		add(panel);



/*		// If no chromosome is being displayed:
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
*/
//		repaint();
	}

	private void setChartData(double[][] data)
	{
		XYSeries series = new XYSeries("");

		if (data != null)
			for (int i = 0; i < data.length; i++)
				series.add(data[i][0], data[i][1]);

		XYSeriesCollection coll = new XYSeriesCollection(series);
		chart.getXYPlot().setDataset(coll);

		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis range = plot.getRangeAxis();
		range.setVisible(true);
		range = plot.getDomainAxis();
//		range.setVisible(visible);


	}

	private JFreeChart createChart(IntervalXYDataset dataset) {
//		JFreeChart chart = ChartFactory.createXYAreaChart(null, null, // xaxis title
//		null, // yaxis title
//		null, PlotOrientation.VERTICAL, true, true, false);

		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

		JFreeChart chart = ChartFactory.createHistogram(
                null, null, null, dataset,
                PlotOrientation.VERTICAL, true, true, false);

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

//	public Dimension getPreferredSize()
//		{ return new Dimension(0, 150); }
}