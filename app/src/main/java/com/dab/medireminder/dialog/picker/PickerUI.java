package com.dab.medireminder.dialog.picker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dab.medireminder.R;
import com.dab.medireminder.dialog.picker.blur.PickerUIBlurHelper;

import java.util.Arrays;
import java.util.List;

import static com.dab.medireminder.dialog.picker.PickerUI.SLIDE.*;


    public class PickerUI extends RelativeLayout implements PickerUIBlurHelper.BlurFinishedListener {
    private static final String LOG_TAG = PickerUI.class.getSimpleName();

    private boolean autoDismiss = PickerUISettings.DEFAULT_AUTO_DISMISS;
    private boolean itemsClickables = PickerUISettings.DEFAULT_ITEMS_CLICKABLES;
    private boolean itemsClickablesMinute = PickerUISettings.DEFAULT_ITEMS_CLICKABLES;

    private PickerUIItemClickListener mPickerUIListener;
    public PickerUIListView mPickerUIListView;
    public PickerUIListView mPickerUIListViewMinute;
    public View mHiddenPanelPickerUI;
    public View mHiddenPanelPickerUIHour;
    public View mHiddenPanelPickerUIMinute;
    private Context mContext;
    private List<String> items;
    private List<String> itemsMinute;
    private int position;
    private int positionMinute;
    private PickerUIBlurHelper mPickerUIBlurHelper;
    private int backgroundColorPanel;
    private int colorLines;
    private int mColorTextCenterListView;
    private int mColorTextNoCenterListView;
    private PickerUISettings mPickerUISettings;
    private TextView mCloseTv;
    public TextView mChosseTv;
    private TextView mTitleTv;
    int mWhich = 0;

    /**
     * Default constructor
     */
    public PickerUI(Context context) {
        super(context);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(null);
        }
    }


    public PickerUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(attrs);
            getAttributes(attrs);
        }
    }

    public PickerUI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(attrs);
            getAttributes(attrs);
        }
    }


    private void createEditModeView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pickerui, this, true);
    }


    private void createView(AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pickerui, this, true);
        mHiddenPanelPickerUI = view.findViewById(R.id.hidden_panel);
        mHiddenPanelPickerUIHour = view.findViewById(R.id.hidden_panel_hour);
        mHiddenPanelPickerUIMinute = view.findViewById(R.id.hidden_panel_minute);
        mPickerUIListView =  view.findViewById(R.id.picker_ui_listview);
        mPickerUIListViewMinute=  view.findViewById(R.id.picker_ui_listview_minute);
        mChosseTv = view.findViewById(R.id.choose_tv);
        mCloseTv = view.findViewById(R.id.close_tv);
        mTitleTv = view.findViewById(R.id.title_tv);

        setItemsClickables(itemsClickables);
        setItemsClickablesMinute(itemsClickablesMinute);
        mPickerUIBlurHelper = new PickerUIBlurHelper(mContext, attrs);
        mPickerUIBlurHelper.setBlurFinishedListener(this);
    }


    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PickerUI, 0, 0);

        if (typedArray != null) {
            try {
                autoDismiss = typedArray
                        .getBoolean(R.styleable.PickerUI_autoDismiss,
                                PickerUISettings.DEFAULT_AUTO_DISMISS);
                itemsClickables = typedArray
                        .getBoolean(R.styleable.PickerUI_itemsClickables,
                                PickerUISettings.DEFAULT_ITEMS_CLICKABLES);
                itemsClickablesMinute = typedArray
                        .getBoolean(R.styleable.PickerUI_itemsClickables,
                                PickerUISettings.DEFAULT_ITEMS_CLICKABLES);
                backgroundColorPanel = typedArray.getColor(R.styleable.PickerUI_backgroundColor,
                        getResources().getColor(R.color.background_panel_pickerui));
                colorLines = typedArray.getColor(R.styleable.PickerUI_linesCenterColor,
                        getResources().getColor(R.color.lines_panel_pickerui));
                mColorTextCenterListView = typedArray
                        .getColor(R.styleable.PickerUI_textCenterColor,
                                getResources().getColor(R.color.text_center_pickerui));
                mColorTextNoCenterListView = typedArray
                        .getColor(R.styleable.PickerUI_textNoCenterColor,
                                getResources().getColor(R.color.text_no_center_pickerui));

                int idItems;
                idItems = typedArray.getResourceId(R.styleable.PickerUI_entries, -1);
                if (idItems != -1) {
                    setItems(mContext, Arrays.asList(getResources().getStringArray(idItems)));
                    setItemsMinute(mContext, Arrays.asList(getResources().getStringArray(idItems)));
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while creating the view PickerUI: ", e);
            } finally {
                typedArray.recycle();
            }
        }
    }


    public void slide() {
        int position = 0;
        if (items != null) {
            position = items.size() / 2;
        }
        slide(position);
    }


    public void slide(final int position) {
       slideUp(position);

    }


    public void slide(SLIDE slide) {
        if (slide == UP) {
            if (!isPanelShown()) {
                int position = 0;
                if (items != null) {
                    position = items.size() / 2;
                }
                slideUp(position);
            }
        } else {
            // Hide the Panel
            hidePanelPickerUI();
        }
    }


    public void slideUp(int position) {
        //Render to do the blur effect
        this.position = position;
        mPickerUIBlurHelper.render();
    }

    public void slideUpMinute(int position) {
        //Render to do the blur effect
        this.positionMinute = position;
        mPickerUIBlurHelper.render();
    }


    private void hidePanelPickerUI() {
        Animation bottomDown = AnimationUtils
                .loadAnimation(mContext, R.anim.picker_panel_bottom_down);
        mHiddenPanelPickerUI.startAnimation(bottomDown);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // Hide the panel
                mHiddenPanelPickerUI.setVisibility(View.GONE);

                // Clear blur image.
                mPickerUIBlurHelper.handleRecycle();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }


    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }


    public void setBackgroundColorPanel(int color) {
        backgroundColorPanel = color;
    }


    public void setLinesColor(int color) {
        colorLines = color;
    }


    public void setItemsClickables(boolean itemsClickables) {
        this.itemsClickables = itemsClickables;
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
            mPickerUIListView.getPickerUIAdapter().setItemsClickables(itemsClickables);
        }
    }

    public void setItemsClickablesMinute(boolean itemsClickables) {
        this.itemsClickablesMinute = itemsClickables;
        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
            mPickerUIListViewMinute.getPickerUIAdapter().setItemsClickables(itemsClickables);
        }
    }

    private void setTextColorsListView() {
        setColorTextCenter(mColorTextCenterListView);
        setColorTextNoCenter(mColorTextNoCenterListView);
    }

    public boolean isPanelShown() {
        return mHiddenPanelPickerUI.getVisibility() == View.VISIBLE;
    }


    public void setItems(Context context, List<String> items) {
        if (items != null) {
            setItems(context, items, 0, items.size() / 2);
        }
    }

    public void setItemsMinute(Context context, List<String> items) {
        if (items != null) {
            setItemsMinute(context, items, 0, items.size() / 2);
        }
    }


    public void setItems(Context context, List<String> items, int which, int position) {
        if (items != null) {
            this.items = items;
            mPickerUIListView.setItems(context, items, which, position, itemsClickables);
            setTextColorsListView();
        }
    }

    public void setItemsMinute(Context context, List<String> items, int which, int position) {
        if (items != null) {
            this.itemsMinute = items;
            mPickerUIListViewMinute.setItems(context, items, which, position, itemsClickablesMinute);
            setTextColorsListView();
        }
    }

    @Override
    public void onBlurFinished(Bitmap bitmapWithBlur) {

        if (bitmapWithBlur != null) {
            mPickerUIBlurHelper.showBlurImage(bitmapWithBlur);
        }

        // Show the panel
        showPanelPickerUI();
    }


    public void setUseBlur(boolean useBlur) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setUseBlur(useBlur);
        }
    }


    public void setUseRenderScript(boolean useRenderScript) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setUseRenderScript(useRenderScript);
        }
    }


    public void setDownScaleFactor(float downScaleFactor) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setDownScaleFactor(downScaleFactor);
        }
    }

    public void setBlurRadius(int radius) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setBlurRadius(radius);
        }
    }

    public void setFilterColor(int filterColor) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setFilterColor(filterColor);
        }
    }


    public void setColorTextCenter(int color) {
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {

            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextCenterListView = newColor;
            mPickerUIListView.getPickerUIAdapter().setColorTextCenter(newColor);
        }

        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {

            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextCenterListView = newColor;
            mPickerUIListViewMinute.getPickerUIAdapter().setColorTextCenter(newColor);
        }
    }


    public void setColorTextNoCenter(int color) {
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextNoCenterListView = newColor;
            mPickerUIListView.getPickerUIAdapter().setColorTextNoCenter(newColor);
        }

        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextNoCenterListView = newColor;
            mPickerUIListViewMinute.getPickerUIAdapter().setColorTextNoCenter(newColor);
        }
    }


    private void showPanelPickerUI() {
        mHiddenPanelPickerUI.setVisibility(View.VISIBLE);
        setBackgroundPanel();
        setBackgroundLines();
        Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.picker_panel_bottom_up);
        mHiddenPanelPickerUIHour.startAnimation(bottomUp);
        bottomUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
                    mPickerUIListView.getPickerUIAdapter().handleSelectEvent(position + 2);
                    mPickerUIListView.setSelection(position);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Animation bottomUp2 = AnimationUtils.loadAnimation(mContext, R.anim.picker_panel_bottom_up);
        mHiddenPanelPickerUIMinute.startAnimation(bottomUp2);
        bottomUp2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
                    mPickerUIListViewMinute.getPickerUIAdapter().handleSelectEvent(positionMinute + 2);
                    mPickerUIListViewMinute.setSelection(positionMinute);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    private void setBackgroundPanel() {
        int color;
        try {
            color = getResources().getColor(backgroundColorPanel);
        } catch (Resources.NotFoundException e) {
            color = backgroundColorPanel;
        }
        mHiddenPanelPickerUIHour.setBackgroundColor(color);
        mHiddenPanelPickerUIMinute.setBackgroundColor(color);
    }

    private void setBackgroundLines() {
        int color;
        try {
            color = getResources().getColor(colorLines);
        } catch (Resources.NotFoundException e) {
            color = colorLines;
        }

        //Top line
        mHiddenPanelPickerUIMinute.findViewById(R.id.picker_line_top_minute).setBackgroundColor(color);
        mHiddenPanelPickerUIHour.findViewById(R.id.picker_line_top).setBackgroundColor(color);

        //Bottom line
        mHiddenPanelPickerUIHour.findViewById(R.id.picker_line_bottom).setBackgroundColor(color);
        mHiddenPanelPickerUIMinute.findViewById(R.id.picker_line_bottom_minute).setBackgroundColor(color);
    }

    public void setOnClickItemPickerUIListener(final PickerUIItemClickListener listener) {
        this.mPickerUIListener = listener;


        mPickerUIListView.setOnClickItemPickerUIListener(
                new PickerUIListView.PickerUIItemClickListener() {
                    @Override
                    public void onItemClickItemPickerUI(int which, int position,
                                                        String valueResult) {
                    }
                });

        mPickerUIListViewMinute.setOnClickItemPickerUIListener(
                new PickerUIListView.PickerUIItemClickListener() {
                    @Override
                    public void onItemClickItemPickerUI(int which, int position,
                                                        String valueResult) {
                      }
                });



        mCloseTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickerUIListener.onCloseClick(view);
            }
        });

        mChosseTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickerUIListener.onChooseClick(mWhich, mPickerUIListView.getItemInListCenter(), mPickerUIListViewMinute.getItemInListCenter());
            }
        });
    }

    public void setSettings(PickerUISettings pickerUISettings) {
        mPickerUISettings = pickerUISettings;
        setColorTextCenter(pickerUISettings.getColorTextCenter());
        setColorTextNoCenter(pickerUISettings.getColorTextNoCenter());
        setItems(mContext, pickerUISettings.getItems());
        setItemsMinute(mContext, pickerUISettings.getItemsMinute());
        setBackgroundColorPanel(pickerUISettings.getBackgroundColor());
        setLinesColor(pickerUISettings.getLinesColor());
        setItemsClickables(pickerUISettings.areItemsClickables());
        setItemsClickablesMinute(pickerUISettings.areItemsClickablesMinute());
        setUseBlur(pickerUISettings.isUseBlur());
        setUseRenderScript(pickerUISettings.isUseBlurRenderscript());
        setAutoDismiss(pickerUISettings.isAutoDismiss());
        setBlurRadius(pickerUISettings.getBlurRadius());
        setDownScaleFactor(pickerUISettings.getBlurDownScaleFactor());
        setFilterColor(pickerUISettings.getBlurFilterColor());
    }


    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putParcelable("stateSettings", mPickerUISettings);
        //save everything
        bundle.putBoolean("stateIsPanelShown", isPanelShown());
        bundle.putInt("statePosition", mPickerUIListView.getItemInListCenter());
        bundle.putInt("statePositionMinute", mPickerUIListViewMinute.getItemInListCenter());
        return bundle;
    }


    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            //load everything
            PickerUISettings pickerUISettings = bundle.getParcelable("stateSettings");
            if (pickerUISettings != null) {
                setSettings(pickerUISettings);
            }

            boolean stateIsPanelShown = bundle.getBoolean("stateIsPanelShown");
            if (stateIsPanelShown) {

                final int statePosition = bundle.getInt("statePosition");
                final int statePositionMinute = bundle.getInt("statePositionMinute");

                ViewTreeObserver observer = getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        slideUp(statePosition);

                        slideUp(statePositionMinute);

                        if (android.os.Build.VERSION.SDK_INT
                                >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            //noinspection deprecation
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                    }
                });

            }
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    public enum SLIDE {
        UP,
        DOWN
    }


    public interface PickerUIItemClickListener {


        void onItemClickPickerUI(int which, int position, String valueResult);

        void onCloseClick(View view);

        void onChooseClick(int which, int positionHour, int positionMinute);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

}