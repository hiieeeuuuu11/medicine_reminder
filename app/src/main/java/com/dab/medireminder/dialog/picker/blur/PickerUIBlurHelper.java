package com.dab.medireminder.dialog.picker.blur;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dab.medireminder.R;

public class PickerUIBlurHelper {

    private static final String LOG_TAG = PickerUIBlurHelper.class.getSimpleName();

    private float mDownScaleFactor = PickerUIBlur.DEFAULT_DOWNSCALE_FACTOR;

    private ImageView mBlurredImageView;

    private int mBlurRadius = PickerUIBlur.DEFAULT_BLUR_RADIUS;

    private ViewGroup mRootView;
    private Context mContext;
    private int mFilterColor = -1;
    private int mAlpha = PickerUIBlur.CONSTANT_DEFAULT_ALPHA;
    private boolean mUseBlur = PickerUIBlur.DEFAULT_USE_BLUR;
    private boolean mUseRenderScript = PickerUIBlur.DEFAULT_USE_BLUR_RENDERSCRIPT;
    private BlurFinishedListener mBlurFinishedListener;

    /**
     * Default constructor
     */
    public PickerUIBlurHelper(Context context, AttributeSet attrs) {
        mContext = context;
        getAttributes(attrs);
        createImageViewBlur();
    }

    public PickerUIBlurHelper() {

    }


    public Bitmap loadBitmapFromView(View view) {
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            view.draw(c);

            return b;
        }
        return null;
    }



    public Bitmap downscaleBitmap(Bitmap bitmap) {
        int width = (int) (bitmap.getWidth() / mDownScaleFactor);
        int height = (int) (bitmap.getHeight() / mDownScaleFactor);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PickerUI, 0, 0);

        try {
            mUseBlur = typedArray.getBoolean(R.styleable.PickerUI_blur,
                    PickerUIBlur.DEFAULT_USE_BLUR);
            mBlurRadius = typedArray.getInteger(R.styleable.PickerUI_blur_radius,
                    PickerUIBlur.DEFAULT_BLUR_RADIUS);
            mDownScaleFactor = typedArray.getFloat(R.styleable.PickerUI_blur_downScaleFactor,
                    PickerUIBlur.DEFAULT_DOWNSCALE_FACTOR);
            mFilterColor = typedArray.getColor(R.styleable.PickerUI_blur_FilterColor, -1);
            mUseRenderScript = typedArray.getBoolean(R.styleable.PickerUI_blur_use_renderscript,
                    PickerUIBlur.DEFAULT_USE_BLUR_RENDERSCRIPT);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while creating the view PickerUI with PickerUIBlurHelper: ",
                    e);
        } finally {
            typedArray.recycle();
        }
    }


    public void setDownScaleFactor(float downScaleFactor) {
        if (!PickerUIBlur.isValidDownscale(downScaleFactor)) {
            throw new IllegalArgumentException("Invalid downsampling");
        }
        mDownScaleFactor = downScaleFactor < PickerUIBlur.MIN_DOWNSCALE ? PickerUIBlur.MIN_DOWNSCALE
                : downScaleFactor;
    }

    public void setUseBlur(boolean useBlur) {
        mUseBlur = useBlur;
    }


    public void setUseRenderScript(boolean useRenderScript) {
        mUseRenderScript = useRenderScript;
    }


    public void setBlurRadius(int blurRadius) {
        if (!PickerUIBlur.isValidBlurRadius(blurRadius)) {
            throw new IllegalArgumentException("Invalid blur radius");
        }
        mBlurRadius = Math.max(blurRadius, PickerUIBlur.MIN_BLUR_RADIUS);
    }

    private void setAlpha(View view, float alpha, long durationMillis) {
        view.setAlpha(alpha);
    }


    public void setFilterColor(int filterColor) {
        this.mFilterColor = filterColor;
    }


    private void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color) {

        Bitmap resultBitmap = Bitmap
                .createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1,
                        sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY);
        p.setColorFilter(filter);
        image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
    }


    void setBackground(Bitmap blurBitmap) {
        BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), blurBitmap);
        bd.setAlpha(mAlpha);
        if (mFilterColor != -1) {
            changeBitmapColor(bd.getBitmap(), mBlurredImageView, mFilterColor);
        } else {
            mBlurredImageView.setImageBitmap(blurBitmap);
        }
    }


    public void showBlurImage(Bitmap bitmapWithBlur) {
        if (mUseBlur) {
            mBlurredImageView.setImageBitmap(null);
            mBlurredImageView.setVisibility(View.VISIBLE);

            // Set the blurred background
            setBackground(bitmapWithBlur);
        }
    }


    public void handleRecycle() {
        if (mUseBlur) {
            Drawable drawable = mBlurredImageView.getDrawable();

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable.getBitmap();

                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            mBlurredImageView.setVisibility(View.GONE);
            mBlurredImageView.setImageBitmap(null);
        }
    }


    private void createImageViewBlur() {

        if (mUseBlur) {
            mRootView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView()
                    .findViewById(android.R.id.content);

            mBlurredImageView = new ImageView(mContext);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            Log.d("YYYYY width", "" + mRootView.getWidth());
            Log.d("YYYYY height", "" + mRootView.getHeight());
            mBlurredImageView.setLayoutParams(params);
            mBlurredImageView.setClickable(false);
            mBlurredImageView.setVisibility(View.GONE);
            mBlurredImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    // Add the ImageView with blurred view
                    ((ViewGroup) mRootView.getChildAt(0))
                            .addView(mBlurredImageView, mRootView.getChildCount());
                }
            });
        }
    }


    public void render() {
        if (mUseBlur) {
            PickerUIBlurTask pickerUIBlurTask = new PickerUIBlurTask((Activity) mContext,
                    mBlurRadius,
                    mBlurFinishedListener, mUseRenderScript);
            pickerUIBlurTask.execute();
        } else {
            if (mBlurFinishedListener == null) {
                throw new IllegalStateException(
                        "You must assign a valid BlurFinishedListener first!");
            }
            mBlurFinishedListener.onBlurFinished(null);
        }
    }


    public void setBlurFinishedListener(BlurFinishedListener listener) {
        this.mBlurFinishedListener = listener;
    }

    public interface BlurFinishedListener {
        void onBlurFinished(Bitmap bitmapWithBlur);
    }
}