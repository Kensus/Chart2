package com.example.kensus.chart2;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LineChart lineChart;
    private Thread thread;
    private boolean plotData = true;
    float disturbThreshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.mainChart);
        //create sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);//first sensor

        }

        //lineChart settings
        lineChart.getDescription().setText(" ");
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setNoDataTextColor(Color.GREEN);
        lineChart.setBackgroundColor(Color.WHITE);


        lineChart.setNoDataText("No data currently");
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDrawBorders(false);

        //initialize  LineData and add it to the chart
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        lineChart.setData(data);


        //getting Legend object
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLUE);

        //drawing x ang y axis
        //Date groupCount = new Date();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineWidth(3f);
        xAxis.setTextSize(12f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);


        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.BLACK);
        yAxisLeft.setTextSize(12f);
        yAxisLeft.setAxisMaximum(5f);
        yAxisLeft.setAxisLineWidth(3f);
        yAxisLeft.setAxisMaximum(150f);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisLineColor(Color.BLACK);


        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setTextColor(Color.BLACK);
        yAxisRight.setEnabled(false);



        //call  to  plotTheGraph() function
        //plotTheGraph();

    }
    //plot real time data

   /* private void plotTheGraph() {
        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    plotData = true;

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();


    }*/

    //add Entry to the line chart ( OnSensorChange )------------------------------------------------------
    private void upDateAccelerometer(SensorEvent event) {
        LineData data = lineChart.getData();

        if (data != null) { // if plot already exist

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                //create set and add
                set = createLineDataSet();
                data.addDataSet(set);
            }
            // adding new values (sensor data)

            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);

            lineChart.notifyDataSetChanged();
            lineChart.setMaxVisibleValueCount(120);
            lineChart.moveViewToX(data.getEntryCount()); // move to the next point
        }

    }

    //create data set
    private LineDataSet createLineDataSet() {
        LineDataSet lineDataSet1 = new LineDataSet(null, "Accelerometer data"); //draw data set
        //line settings here ....
        lineDataSet1.setFillAlpha(65);
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet1.setLineWidth(1f);
        //set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet1.setCubicIntensity(0.2f);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setValueTextColor(Color.RED);

        //list of IDataSet if more than one
        //List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //dataSets.add(lineDataSet1);


        return lineDataSet1;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //accelerometerValues
        if (plotData) {
            upDateAccelerometer(sensorEvent);

        }


    }
    // change


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //register again
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

}
