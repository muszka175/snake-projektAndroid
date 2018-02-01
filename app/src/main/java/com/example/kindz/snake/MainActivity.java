package com.example.kindz.snake;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kindz.snake.engine.GameEngine;
import com.example.kindz.snake.enums.Direction;
import com.example.kindz.snake.enums.GameState;
import com.example.kindz.snake.views.SnakeView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //accelerometer
    TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;



    // snake
    private GameEngine gameEngine;
    private SnakeView snakeView;
    private final Handler handler = new Handler();
    private long updateDelay = 500;

    private float prevX, prevY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        gameEngine = new GameEngine();
        gameEngine.initGame();



        snakeView = (SnakeView)findViewById(R.id.snakeView);
       // snakeView.setOnTouchListener(this);
        startUpdateHandler();


        /*SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);*/


        // Create our Sensor Manager
       SM = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
       // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);

        // Assign TextView
       //  xText = (TextView)findViewById(R.id.xText);
        //yText = (TextView)findViewById(R.id.yText);
         //zText = (TextView)findViewById(R.id.zText);
    }


   @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


       float xChange = sensorEvent.values[0];
       float yChange = sensorEvent.values[1];

        updateSnakeDirection(xChange, yChange);

    }

    private void updateSnakeDirection(float xChange, float yChange) {
        if(xChange*xChange > yChange*yChange){
            if(gameEngine.getLastDirection() == Direction.North || gameEngine.getLastDirection() == Direction.South){
                if( xChange > 2)
                    gameEngine.UpdateDirection(Direction.West);

                else if (xChange < -2)
                    gameEngine.UpdateDirection(Direction.East);
            }
        }else{
            if(gameEngine.getLastDirection() != Direction.North && gameEngine.getLastDirection() != Direction.South){
                if(yChange > 2)
                    gameEngine.UpdateDirection(Direction.South);

                else if (yChange < -2)
                    gameEngine.UpdateDirection(Direction.North);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not in use

    }

    private void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDelay = gameEngine.getSnakeSpeed();
                gameEngine.Update();

                if(gameEngine.getCurrentGameState() == GameState.Running){
                    handler.postDelayed(this,updateDelay);
                }
                if(gameEngine.getCurrentGameState() == GameState.Lost){
                    OnGameLost();
                }


                snakeView.setSnakeViewMap(gameEngine.getMap());
                snakeView.invalidate();
            }
        },updateDelay);
    }

    private void OnGameLost(){
        Toast.makeText(this,"You lost!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}