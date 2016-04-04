package com.ev112.codeblack.simpleclient.test;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.ev112.codeblack.simpleclient.alphasystem.AlphaSystem;

public class TestJFreeChart extends ApplicationFrame {

	private XYDataset	data;
	private Plot		plot;
	private JFreeChart	chart;
	private ChartPanel	panel;
	private AlphaSystem alpha = new AlphaSystem("TEST");

	public TestJFreeChart(String title) {
		super(title);
		data = getDataSet();
		plot = new XYPlot(data, new NumberAxis(), new NumberAxis(), new StandardXYItemRenderer());
		chart = new JFreeChart("HelloWorld JFreeChart", plot);
		panel = new ChartPanel(chart);
		add(panel);
		pack();
		setVisible(true);
	}

	private XYDataset getDataSet() {
		DefaultXYDataset ds = new DefaultXYDataset();
		ds.addSeries(new Comparable<String>() {
			public int compareTo(String o) {
				return 1;
			};
		}, new double[][] { { 0, 1, 2, 3 }, { 0, 10, 20, 30 } });
		return ds;
	}

	public static void main(String[] args) {
		RefineryUtilities.centerFrameOnScreen(new TestJFreeChart("Hello World"));
	}

}
