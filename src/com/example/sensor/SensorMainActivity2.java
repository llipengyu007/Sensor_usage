package com.example.sensor;

import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.GraphicalView;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

public class SensorMainActivity2 extends ActionBarActivity {
	private Spinner m_Spinner;
	
	private TextView sensorTypeNameView;
	private static final String[] sensorTypeNames = {"Accelerometer","Magnetic","Light"};
	private int sensorType[] = {Sensor.TYPE_ACCELEROMETER,Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_LIGHT};
	private int sensorBeChoice=0;
	private String curveName[][] = {
			{"X","Y","Z"},{"X","Y","Z"},{"POWER"}
	};
	private double curveYMax[] = {35,50,25};
	private double curveYMin[] = {-35,-50,0};
	private int curveNum[]={3,3,1};
	
	private  SensorManager sm;  
	private double []data;
	
	private LinearLayout layoutForPlot;
	private GraphicalView graphicalViewForPlot;
	private ChartService ChartServiceForPlot;
	
	private double time = 0.0f;
	private int NumTime = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_main_activity2);
		
		data = new double[curveNum[0]];
		
		//创建一个SensorManager来获取系统的传感器服务  
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
    
		/*************************Spiner test*********************/
        m_Spinner = (Spinner) findViewById(R.id.spinnerSensorChoice);
        sensorTypeNameView = (TextView) findViewById(R.id.textViewSensorName);
        sensorTypeNameView.setTextColor(Color.RED);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, sensorTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_gallery_item);
        m_Spinner.setAdapter(adapter);
        final Context context = this;
        m_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        	{
        		sm.unregisterListener(sensorListener);
        		
        		sensorBeChoice = arg2;
        		ChartServiceForPlot.setXYMultipleSeriesDataset(curveName[arg2], curveNum[arg2], NumTime);
        		ChartServiceForPlot.setXYMultipleSeriesRenderer((double)NumTime, curveYMax[arg2], 0.0, curveYMin[arg2], sensorTypeNames[arg2], "value", "time", Color.RED, Color.RED, Color.BLACK, curveNum[arg2]);
        
        		data = new double[curveNum[sensorBeChoice]];
        		sm.registerListener(sensorListener,sm.getDefaultSensor(sensorType[arg2]),SensorManager.SENSOR_DELAY_NORMAL);
        		sensorTypeNameView.setText("x : \ny : \nz : ");
        	}
        	public void onNothingSelected(AdapterView<?> parent){;}
        });
        
        /*******************plot***********************************/
	    layoutForPlot = (LinearLayout)findViewById(R.id.linearLayoutPlot);
	    ChartServiceForPlot = new ChartService(context);
	    ChartServiceForPlot.setXYMultipleSeriesDataset(curveName[0], curveNum[0], NumTime);
	    ChartServiceForPlot.setXYMultipleSeriesRenderer((double)NumTime, curveYMax[0], 0.0, curveYMin[0], sensorTypeNames[0], "value", "time", Color.RED, Color.RED, Color.BLACK, curveNum[0]);
	    graphicalViewForPlot = ChartServiceForPlot.getGraphicalView();
	    layoutForPlot.addView(graphicalViewForPlot,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        /**********************************************************/
        /**************************update UI****************************/
        Timer updateTimer = new Timer("gForceUpdate");  
        updateTimer.scheduleAtFixedRate(
        		new TimerTask() 
        		{  
			        public void run() 
			        {  
			        	updateGUI();  
			        }  
        		}, 0, 100);
        /**********************************************************/        
	}
	
	
	
	/*************update UI************************/
	private void updateGUI()
	{
		runOnUiThread(
				new Runnable()
				{
					public void run()
					{
						String show;
						switch(curveNum[sensorBeChoice])
						{
						case 1: show = "value1 : " + Double.toString(data[0]) + "\n"; break;
						case 2: show = "value1 : " + Double.toString(data[0]) + "\n"
				                  +"value2 : " + Double.toString(data[1]) + "\n"; break;
						case 3: show = "value1 : " + Double.toString(data[0]) + "\n"
				                  +"value2 : " + Double.toString(data[1]) + "\n"
				                  +"value3 : " + Double.toString(data[2]) + "\n";break;
				         default: show = "value1 : " + Double.toString(data[0]) + "\n"
				                  +"value2 : " + Double.toString(data[1]) + "\n"
				                  +"value3 : " + Double.toString(data[2]) + "\n";break;
						}
						sensorTypeNameView.setText(show);
						
						
						ChartServiceForPlot.updateChart(time, data);
						time += 1;
					}
				}
				);
	}
	
	/**********************************************/
	 /***************************sensor ******************************************/
	final SensorEventListener sensorListener =
	
			new SensorEventListener()
			{  
		        //复写onSensorChanged方法  
		        public void onSensorChanged(SensorEvent sensorEvent)
		        {  
		            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		            {  
		            	data[0] = sensorEvent.values[0];  
		            	data[1] = sensorEvent.values[1]; 
		            	data[2] = sensorEvent.values[2]; 
		            }  
		            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		            {  
		            	data[0] = sensorEvent.values[0];  
		            	data[1] = sensorEvent.values[1]; 
		            	data[2] = sensorEvent.values[2]; 
		            }  
		            if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT)
		            {  
		            	data[0] = sensorEvent.values[0];  
		            }  
		        }  
	        //复写onAccuracyChanged方法  
		        public void onAccuracyChanged(Sensor sensor , int accuracy)
		        {  
		            ; 
		        }  
			}
			
			
    ;  
	 /**************************************************************************/  
	
	
	
    public void onPause(){  
        /* 
         * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率 
         * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。 
         * */  
    	if(sensorBeChoice != -1)
    	{
    		sm.unregisterListener(sensorListener);
    	}
        super.onPause();  
    }  
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_main_activity2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
