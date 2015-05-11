package com.example.sensor;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.TextView;

public class ChartService {
	private GraphicalView mGraphicalView;
	private XYMultipleSeriesDataset multipleSeriesDataset;// 数据集容器
	private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
	private Context context;
	
	private int lengthX, currentX, Numcurve;
	private double dataY[][];
	private double dataX[];
	private String curveTitle[];
	
	
	public ChartService(Context context)
	{
		this.context = context;
		
		multipleSeriesDataset = new XYMultipleSeriesDataset();
		multipleSeriesRenderer = new XYMultipleSeriesRenderer(); 
	}
	
	public GraphicalView getGraphicalView() 
	{
		mGraphicalView = ChartFactory.getCubeLineChartView(context,
				multipleSeriesDataset, multipleSeriesRenderer, 0.1f);
		return mGraphicalView;
	}
	
	public void setXYMultipleSeriesDataset(String[] curveTitle_,int Numcurve_, int lengthX_) 
	{
		multipleSeriesDataset.clear();
		lengthX = lengthX_;
		currentX = 0;
		Numcurve = Numcurve_;
		dataY = new double[Numcurve][lengthX];
		dataX = new double[lengthX];
		for(int i = 0; i < lengthX ; i++)
			dataX[i] = i;
		curveTitle = new String[Numcurve];
		for(int i = 0 ; i < Numcurve ; i++)
		{
			curveTitle[i] =  curveTitle_[i];
			XYSeries mSeries = new XYSeries(curveTitle[i]);
			multipleSeriesDataset.addSeries(mSeries);
		}
	}
	/**
	       * 获取渲染器
	       * 
	       * @param maxX
	       *            x轴最大值
	       * @param maxY
	       *            y轴最大值
	       * @param minX
	       *            x轴最小值
	       * @param minY
	       *            y轴最小值
	       * @param chartTitle
	       *            曲线的标题
	       * @param xTitle
	       *            x轴标题
	       * @param yTitle
	       *            y轴标题
	       * @param axeColor
	       *            坐标轴颜色
	       * @param labelColor
	       *            标题颜色
	       * @param curveColor
	       *            曲线颜色
	       * @param gridColor
	       *            网格颜色
	 */
	public void setXYMultipleSeriesRenderer(double maxX, double maxY,
			double minX, double minY, String chartTitle, String xTitle, 
			String yTitle, int axeColor,int labelColor, int gridColor,
			int Numcurve)
	{
		multipleSeriesRenderer.removeAllRenderers();
		int []colors = {Color.RED, Color.GREEN, Color.BLUE};
		PointStyle []points = {PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.TRIANGLE};
		
		if (chartTitle != null) 
		{
			multipleSeriesRenderer.setChartTitle(chartTitle);
		}
		 
		for (int i = 0; i < Numcurve; i++)  
		{  
			XYSeriesRenderer mRenderer = new XYSeriesRenderer();  
			mRenderer.setColor(colors[i]);  
			mRenderer.setPointStyle(points[i]);
			mRenderer.setLineWidth(10);
			multipleSeriesRenderer.addSeriesRenderer(mRenderer);  
		}  

		 multipleSeriesRenderer.setXTitle(xTitle);
		 multipleSeriesRenderer.setXTitle(yTitle);
		 multipleSeriesRenderer.setRange(new double[] { minX, maxX, minY, maxY });
		 multipleSeriesRenderer.setLabelsColor(labelColor);
		 multipleSeriesRenderer.setXLabels(10);
		 multipleSeriesRenderer.setYLabels(10);
		 multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);
		 multipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
		 multipleSeriesRenderer.setAxisTitleTextSize(20);
		 multipleSeriesRenderer.setChartTitleTextSize(20);
		 multipleSeriesRenderer.setLabelsTextSize(20);
		 multipleSeriesRenderer.setLegendTextSize(20);
		 multipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
		 multipleSeriesRenderer.setFitLegend(true);
		 multipleSeriesRenderer.setMargins(new int[] { 20, 30, 15, 20 });
		 multipleSeriesRenderer.setShowGrid(true);
		 multipleSeriesRenderer.setZoomEnabled(true, false);
		 multipleSeriesRenderer.setAxesColor(axeColor);
		 multipleSeriesRenderer.setGridColor(gridColor);
		 multipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
		 multipleSeriesRenderer.setMarginsColor(Color.WHITE);//边距背景色，默认背景色为黑色，这里修改为白色
//		 mRenderer = new XYSeriesRenderer();
//		 mRenderer.setColor(curveColor);
//		 mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
//		 multipleSeriesRenderer.addSeriesRenderer(mRenderer);
	}
	
	 /**
	      * 根据新加的数据，更新曲线，只能运行在主线程
	      * 
	      * @param x
	      *            新加点的x坐标
	      * @param y
	      *            新加点的y坐标
	      */ 
	public void updateChart(double x, double []y) 
	{
		if(currentX > lengthX-1)
		{
			multipleSeriesDataset.clear();
			for(int i = 0 ; i < Numcurve ; i++)
			{
				XYSeries mSeries = new XYSeries(curveTitle[i]);
				for(int j = 1 ; j < lengthX ; j++)
				{
					dataY[i][j-1] = dataY[i][j];
					mSeries.add(dataX[j-1], dataY[i][j-1]);
				}
				dataY[i][lengthX-1] = y[i];
				mSeries.add(dataX[lengthX-1], dataY[i][lengthX-1]);
				
				multipleSeriesDataset.addSeries(mSeries);  
			}
		}
		else
		{
			for(int i = 0 ; i < Numcurve ; i++)
			{
				XYSeries mSeries[] = multipleSeriesDataset.getSeries();
				mSeries[i].add(dataX[currentX], y[i]);
				
				dataY[i][currentX] = y[i];
			}
		}
		currentX++;
		
		mGraphicalView.repaint();//此处也可以调用invalidate()
	}
	
	public void dataChanged(String[] curveTitle_,int Numcurve_, int lengthX_)
	{

		lengthX = lengthX_;
		currentX = 0;
		Numcurve = Numcurve_;
		dataY = new double[Numcurve][lengthX];
		dataX = new double[lengthX];
		dataY = new double[Numcurve][lengthX];
		for(int i = 0; i < lengthX ; i++)
			dataX[i] = i;
	
		multipleSeriesDataset.clear();
		curveTitle = new String[Numcurve];
		for(int i = 0 ; i < Numcurve ; i++)
		{
			curveTitle[i] =  curveTitle_[i];
			XYSeries mSeries = new XYSeries(curveTitle[i]);
			multipleSeriesDataset.addSeries(mSeries);
			
		}
	}
	
//	public void rendererChanged(double maxX, double maxY,
//			double minX, double minY, String chartTitle, String xTitle, 
//			String yTitle, int axeColor,int labelColor, int gridColor,
//			int Numcurve,TextView tmpView)
//	{
//		//multipleSeriesRenderer = new XYMultipleSeriesRenderer(); 
//		if (chartTitle != null) 
//		{
//			multipleSeriesRenderer.setChartTitle(chartTitle);
//			
//			tmpView.setText(chartTitle);
//		}
//		 
//		for (int i = 0; i < Numcurve; i++)  
//		{  
//			XYSeriesRenderer mRenderer = new XYSeriesRenderer();  
//			mRenderer.setColor(colors[i]);  
//			mRenderer.setPointStyle(points[i]);
//			mRenderer.setLineWidth(10);
//			multipleSeriesRenderer.addSeriesRenderer(mRenderer);  
//		}  
//
//		 multipleSeriesRenderer.setXTitle(xTitle);
//		 multipleSeriesRenderer.setXTitle(yTitle);
//		 multipleSeriesRenderer.setRange(new double[] { minX, maxX, minY, maxY });
//		 multipleSeriesRenderer.setLabelsColor(labelColor);
//		 multipleSeriesRenderer.setXLabels(10);
//		 multipleSeriesRenderer.setYLabels(10);
//		 multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);
//		 multipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
//		 multipleSeriesRenderer.setAxisTitleTextSize(20);
//		 multipleSeriesRenderer.setChartTitleTextSize(20);
//		 multipleSeriesRenderer.setLabelsTextSize(20);
//		 multipleSeriesRenderer.setLegendTextSize(20);
//		 multipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
//		 multipleSeriesRenderer.setFitLegend(true);
//		 multipleSeriesRenderer.setMargins(new int[] { 20, 30, 15, 20 });
//		 multipleSeriesRenderer.setShowGrid(true);
//		 multipleSeriesRenderer.setZoomEnabled(true, false);
//		 multipleSeriesRenderer.setAxesColor(axeColor);
//		 multipleSeriesRenderer.setGridColor(gridColor);
//		 multipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
//		 multipleSeriesRenderer.setMarginsColor(Color.WHITE);//边距背景色，默认背景色为黑色，这里修改为白色
//		 multipleSeriesRenderer.setBarWidth(100);
////		 mRenderer = new XYSeriesRenderer();
////		 mRenderer.setColor(curveColor);
////		 mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
////		 multipleSeriesRenderer.addSeriesRenderer(mRenderer);
//	}
}
