package com.bryonnicoson.accelerometerexample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// started here:
// https://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float vibrateThreshold = 0;
    private float shakeThreshold = 0;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, shakeCount, shakeBound;
    public Vibrator v;
    private Button clearMax;
    private Integer shakes = 0;

    // TODO: on first breach of shakeThreshold, set Double firstBreachTime & boolean firstBreach = true;
    // TODO:   if ((firstBreach) && (currentTime - firstBreachTime < shakeWindow)) { shakes += 1 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
            shakeThreshold = accelerometer.getMaximumRange() / 4;
        } else {

        }

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
        shakeCount = (TextView) findViewById(R.id.shakeCount);
        shakeBound = (TextView) findViewById(R.id.shakeBound);
        clearMax = (Button) findViewById(R.id.clearMax);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMaxValues();
            }
        };
        clearMax.setOnClickListener(listener);
    }
    // start listening
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    // stop listening
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        displayCleanValues();
        displayCurrentValues();
        displayMaxValues();

        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        vibrate();
    }

    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
        if (deltaZ > shakeThreshold) {
            shakes += 1;
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        shakeCount.setText(String.format("Shake count: %d", shakes));
        shakeBound.setText(String.format("Shake threshold: %f", shakeThreshold));
    }

    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

    public void clearMaxValues() {
        deltaXMax = 0;
        deltaYMax = 0;
        deltaZMax = 0;
        shakes = 0;
        maxX.setText("0.0");
        maxY.setText("0.0");
        maxZ.setText("0.0");
        shakeCount.setText("0");
    }
}
