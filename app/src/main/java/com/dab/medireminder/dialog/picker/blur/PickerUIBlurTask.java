package com.dab.medireminder.dialog.picker.blur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;


public class PickerUIBlurTask extends AsyncTask<Void, Void, Bitmap> {


    private final PickerUIBlurHelper.BlurFinishedListener mBlurFinishedListener;

    private State mState = State.READY;

    private Bitmap mBitmapDownscaled;
    private int mBlurRadius;
    private Activity activity;
    private boolean useRenderScript;



    public PickerUIBlurTask(Activity a, int radius,
                            PickerUIBlurHelper.BlurFinishedListener blurFinishedListener, boolean useRenderScript) {
        activity = a;
        mBlurRadius = radius < 1 ? 1 : radius;
        mBlurFinishedListener = blurFinishedListener;
        this.useRenderScript = useRenderScript;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mState.equals(State.READY)) {
            mState = State.EXECUTING;
            View snapshotView = activity.getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            PickerUIBlurHelper pickerUIBlurHelper =  new PickerUIBlurHelper();
            Bitmap bitmapDecorView = pickerUIBlurHelper.loadBitmapFromView(snapshotView);
            if(bitmapDecorView!=null)
                mBitmapDownscaled = pickerUIBlurHelper.downscaleBitmap(bitmapDecorView);
        } else {
            cancel(true);
        }
    }


    @Override
    protected Bitmap doInBackground(Void... params) {
        if (mState.equals(State.EXECUTING) && mBitmapDownscaled != null) {
            return Blur.apply(activity, mBitmapDownscaled, mBlurRadius, useRenderScript);
        } else {
            return null;
        }
    }


    @Override
    protected void onPostExecute(Bitmap blurredBitmap) {

        super.onPostExecute(blurredBitmap);
        activity = null;
        if (mBlurFinishedListener == null) {
            throw new IllegalStateException("You must assign a valid BlurFinishedListener first!");
        }
        mBlurFinishedListener.onBlurFinished(blurredBitmap);

        mState = State.READY;
    }

    private enum State {
        READY,
        EXECUTING
    }
}
