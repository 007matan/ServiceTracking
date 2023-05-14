package com.cohen.servicetracking;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class TapDetector {

    public interface CallBack_taps {
        void threeTap();
    }


    int tapCounter = 0;
    long timeStamp = 0;

    private CallBack_taps callBack_taps;

    /**
     * Step detector constructor
     * @param context the context of the activity or application or service
     * @param _callBack_taps the listener to steps
     */
    public TapDetector(Context context, CallBack_taps _callBack_taps) {

        this.callBack_taps = _callBack_taps;
    }

    public int getStepCounter() {
        return tapCounter;
    }

    public void start(){

        //myView.myView.setOnTouchListener(onTouchListener);
        Log.d("pp", "start: ");

    }
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int X = (int) motionEvent.getX();
            int Y = (int) motionEvent.getY();
            int eventaction = motionEvent.getAction();

            calculateTap();

            return true;
        }


    };

    private void calculateTap() {
        if(timeStamp == 0 && tapCounter == 0){
            timeStamp = System.currentTimeMillis();
            tapCounter = 1;
        }
        else {
            if (System.currentTimeMillis() - timeStamp < 170) {
                timeStamp = System.currentTimeMillis();
                tapCounter++;
                if (tapCounter == 3) {
                    tapCounter = 0;
                    timeStamp = 0;
                    if (callBack_taps != null) {
                        callBack_taps.threeTap();
                    }
                }
            }
        }
    }
}
