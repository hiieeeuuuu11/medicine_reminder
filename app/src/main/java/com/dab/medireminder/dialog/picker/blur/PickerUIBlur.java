package com.dab.medireminder.dialog.picker.blur;

/**
 */

public class PickerUIBlur {

    public static final int MIN_BLUR_RADIUS = 1;

    public static final float MIN_DOWNSCALE = 1.0f;

    private static final int MAX_BLUR_RADIUS = 25;

    private static final float MAX_DOWNSCALE = 6.0f;

    public static final boolean DEFAULT_USE_BLUR = true;

    public static final boolean DEFAULT_USE_BLUR_RENDERSCRIPT = false;

    public static final int DEFAULT_BLUR_RADIUS = 15;

    public static final float DEFAULT_DOWNSCALE_FACTOR = 5.0f;

    public static final int CONSTANT_DEFAULT_ALPHA = 100;

    public static boolean isValidBlurRadius(int value) {
        return value >= MIN_BLUR_RADIUS && value <= MAX_BLUR_RADIUS;
    }


    public static boolean isValidDownscale(float value) {
        return value >= MIN_DOWNSCALE && value <= MAX_DOWNSCALE;
    }
}