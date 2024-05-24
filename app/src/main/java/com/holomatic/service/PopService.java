package com.holomatic.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.holomatic.holopilotparking.soa.module.holodto.HoloDefPaaFuncSt;
import com.holomatic.holopilotparking.soa.module.holodto.HoloDefVehicleInfo;
import com.holomatic.someip.R;
import com.holomatic.someip.api.SomeIpEngine;


public class PopService extends Service {

    Handler mHandler;

    Handler mSubThreadHandler;

    private WindowManager mWindowManager;
    private View mFloatingView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("subThreadHandler");
        handlerThread.start();
        mSubThreadHandler = new Handler(handlerThread.getLooper());
        mHandler = new Handler(Looper.getMainLooper());
        // Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow, null);
        popWindow();

        /**
         * start
         *
         * mock :
         * PaaFuncSt-APA
         * displayInfo
         *
         */


        mFloatingView.findViewById(R.id.start_engin_btn).setOnClickListener(mClk);
        mFloatingView.findViewById(R.id.paaFuncApa_btn).setOnClickListener(mClk);

    }


    View.OnClickListener mClk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.start_engin_btn){
                mSubThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startEngin();
                    }
                });
            }else if(v.getId() == R.id.paaFuncApa_btn){
                mSubThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mockPaaFuncSt();
                    }
                });
            }
        }
    };

    SomeIpEngine engine;
    private void startEngin(){
        Log.i(TAG, "startEngin: ");
        engine = SomeIpEngine.getInstance();
        engine.init(getApplicationContext());
        engine.startEngine();
    }

    byte speed = 0;
    private void mockVehicleInfo(){
        Log.i(TAG, "mockVehicleInfo: ");
        HoloDefVehicleInfo holoDefVehicleInfo = new HoloDefVehicleInfo();
        holoDefVehicleInfo.Vehicle_Speed =speed++ ;
        engine.mockPkg((short) 0x6001,(short) 0x8001,holoDefVehicleInfo);
    }

    private void mockPaaFuncSt(){
        Log.i(TAG, "mockPaaFuncSt: ");
//        HoloDefPaaFuncSt st = new HoloDefPaaFuncSt();
//        st.paaFuncSt = 0x1;
        engine.mockPkg((short)0x6002,(short)0x9003,(byte)0x01);
    }



    private void popWindow(){

        // Add the view to the window.
        final WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        // Specify the view position
        params.gravity = Gravity.CENTER;        // Initially view will be added to top-left corner
//        params.x = 100;
//        params.y = 100;

        // Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        // The root element of the floating view layout
        View floatingWidget = mFloatingView;

        // Make the floating widget draggable
        floatingWidget.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            // Handle click event
                        }
                        lastAction = event.getAction();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });
    }


    private static final String TAG = "PopService";
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
