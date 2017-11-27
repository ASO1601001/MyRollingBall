package com.example.izumin.myrollingball;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener,SurfaceHolder.Callback{
    SensorManager mSensorManager;
    Sensor mAccSensor;
    SurfaceHolder mHolder;
    int mSurfaceWidth;
    int mSurfaceHeight;

    static final float RADIUS = 50.0f;
    static final float COEF = 1000.0f;

    float mBallX; //ボールの現在のx座標
    float mBallY; //ボールの現在のy座標
    int mRectX;
    int mRectY;
    float mVX; //ボールのx軸方向への速度
    float mVY; //ボールのy軸方向への速度
    float dx;
    float dy;
    int i = 0;

    long mFrom; //前回、センサーから加速度を取得した時間
    long mTo; //今回、センサーから加速度を取得した時間

    boolean isAtari = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.d("MainActivity","x="+String.valueOf(event.values[0])+
                    "y="+String.valueOf(event.values[1])+
                    "z="+String.valueOf(event.values[2]));
            float x = -event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mTo = System.currentTimeMillis();
            float t = (float)(mTo - mFrom);
            t = t/1000.0f;

            dx = mVX * t + x * t * t / 2.0f;
            dy = mVY * t + y * t * t / 2.0f;
            mBallX = mBallX + dx * COEF;
            mBallY = mBallY + dy * COEF;
            mVX = mVX + x * t;
            mVY = mVY + y * t;

            if(mBallX - RADIUS < 0 && mVX < 0){
                mVX = -mVX / 1.5f;
                mBallX = RADIUS;
            }else if(mBallX + RADIUS > mSurfaceWidth && mVX > 0){
                mVX = -mVX / 1.5f;
                mBallX = mSurfaceWidth -RADIUS;
            }

            if(mBallY - RADIUS < 0 && mVY < 0){
                mVY = -mVY / 1.5f;
                mBallY = RADIUS;
            }else if(mBallY + RADIUS > mSurfaceHeight && mVY > 0){
                mVY = -mVY / 1.5f;
                mBallY = mSurfaceHeight-RADIUS;
            }else if(mBallX - RADIUS > 600 && mBallX - RADIUS < 700 && mBallY - RADIUS > 800 && mBallY - RADIUS < 1000){
                //ここが当たり判定
                mVX = -mVX / 1.5f;
                mBallX -= RADIUS;
                mVY = -mVY / 1.5f;
                mBallY -= RADIUS;
            }

            mFrom = System.currentTimeMillis();
            drawCanvas();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){}

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        mFrom = System.currentTimeMillis();
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mBallX = width / 2;
        mBallY = height / 2;
        mVX = 0;
        mVY = 0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        mSensorManager.unregisterListener(this);
    }

    private void drawCanvas(){
        Canvas c = mHolder.lockCanvas();
        c.drawColor(Color.CYAN);
        Paint paint = new Paint();
        mRectX = 500;
        mRectY = 800;
            paint.setColor(Color.MAGENTA);
            c.drawCircle(mBallX,mBallY,RADIUS,paint);


            Paint paint2 = new Paint();
            paint2.setColor(Color.YELLOW);
            Rect rect = new Rect(100, 100, 200, 300);
            rect.offset(mRectX,mRectY);
            c.drawRect(rect,paint2);

        mHolder.unlockCanvasAndPost(c);
    }
}
