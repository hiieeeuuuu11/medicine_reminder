package com.dab.medireminder.dialog.picker;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dab.medireminder.R;

import java.util.Arrays;
import java.util.List;

public class PickerUIListView extends ListView {

    private final static int ROW_HEIGHT = 40;
    private PickerUIItemClickListener mItemClickListenerPickerUI;
    private PickerUIAdapter mPickerUIAdapter;
    private boolean scrollEnabled = false;
    private int lastPositionNotified;
    private int firstItem, scrollTop;
    private List<String> items;
    private int which;


    public PickerUIListView(Context context) {
        super(context);
        if (isInEditMode()) {
            createEditModeView(context);
        } else {
            init(items);
        }
    }

    public PickerUIListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            createEditModeView(context);
        } else {
            init(items);
        }
    }


    public PickerUIListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            createEditModeView(context);
        } else {
            init(items);
        }
    }


    public PickerUIListView(Activity context, List<String> items) {
        super(context);
        if (isInEditMode()) {
            createEditModeView(context);
        } else {
            init(items);
        }
    }


    private void createEditModeView(Context context) {
        String[] entries = new String[10];
        for (int i = 0; i < 10; i++) {
            entries[i] = "item " + i;
        }
        List<String> entriesList = Arrays.asList(entries);
        mPickerUIAdapter = new PickerUIAdapter(context, R.layout.pickerui_item, entriesList,
                entriesList.size() / 2, true, true);
        setAdapter(mPickerUIAdapter);
        setSelection(entriesList.size() / 2);
    }

    private void init(List<String> items) {
        this.items = items;

        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                scrollEnabled = true;

                if (PickerUIListView.this.items != null) {
                    selectListItem(PickerUIListView.this.items.size() / 2, false);
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {

                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

            }
        });

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    getItemInListCenter();
                    if (scrollTop < -ROW_HEIGHT) {
                        mPickerUIAdapter.handleSelectEvent(firstItem + 1 + 2);
                        selectListItem(firstItem + 1);
                    } else {
                        selectListItem(firstItem);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View v = getChildAt(0);

                scrollTop = (v == null) ? 0 : v.getTop();
                firstItem = firstVisibleItem;

                if (scrollEnabled) {
                    getItemInListCenter();
                }
            }
        });

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setNewPositionCenter(position);
            }
        });
    }


    public void setItems(Context context, List<String> items, int idRequestPickerUI, int position,
                         boolean itemsClickables) {
        this.items = items;
        this.which = idRequestPickerUI;
        mPickerUIAdapter = new PickerUIAdapter(context, R.layout.pickerui_item, items, position,
                itemsClickables, false);
        setAdapter(mPickerUIAdapter);
    }

    private void selectListItem(int position, final boolean notify) {
        setSelection(position);

        mItemClickListenerPickerUI
                .onItemClickItemPickerUI(which, position, items.get(position));

    }

    private void selectListItem(final int position) {
        selectListItem(position, true);
    }


    private void setNewPositionCenter(int position) {
        mPickerUIAdapter.handleSelectEvent(position);
        selectListItem(position - 2);
    }


    public int getItemInListCenter() {

        int position = pointToPosition(getWidth() / 2, getHeight() / 2);
        if (position != -1) {

            if (position != lastPositionNotified) {

                //Only refresh adapter on different positions
                lastPositionNotified = position;
                mPickerUIAdapter.handleSelectEvent(position);
            }
        }
        return position - 2;
    }

    PickerUIAdapter getPickerUIAdapter() {
        return mPickerUIAdapter;
    }


    void setOnClickItemPickerUIListener(PickerUIItemClickListener listener) {
        this.mItemClickListenerPickerUI = listener;
    }


    public interface PickerUIItemClickListener {
        void onItemClickItemPickerUI(int which, int position, String valueResult);
    }
}