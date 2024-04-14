package com.dab.medireminder.dialog.picker;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dab.medireminder.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PickerUIAdapter extends ArrayAdapter<String> {

    private static final String EMPTY_STRING = "";

    private static final int ROTATION_CENTER = 0;
    private static final int ROTATION_TWICE_ABOVE = -25;
    private static final int ROTATION_FIRST = -50;
    private static final int ROTATION_BELOW = 25;
    private static final int ROTATION_TWICE_BELOW = 50;
    private static final int ROTATION_ABOVE_FAR = -55;
    private static final int ROTATION_BELOW_FAR = 55;

    private Context mContext;
    private List<String> items;
    private int centerPosition;
    private boolean itemsClickables = true;
    private SparseIntArray positionsNoClickables;
    private int mColorTextCenter = -1;
    private int mColorTextNoCenter = -1;
    private boolean isInEditMode = false;


    public PickerUIAdapter(Context context, int resource, List<String> items, int position,
                           boolean itemsClickables,
                           boolean isInEditMode) {
        super(context, resource, items);
        this.mContext = context;
        this.itemsClickables = itemsClickables;
        this.isInEditMode = isInEditMode;
        positionsNoClickables = new SparseIntArray(items.size());
        setItems(items, position);
        setPositonsNoClickables();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.pickerui_item, parent, false);
        }

        TextView textItem = ViewHolder.get(convertView, R.id.tv_item);
        String option = items.get(position);
        textItem.setText(option);

        if (!isInEditMode) {
            setTextItemStyle(textItem, position);
        }

        return convertView;
    }


    private void setTextItemStyle(TextView textItem, int position) {

        if (position == centerPosition) {
            textItem.setTextAppearance(mContext, R.style.PickerUI_Center_Item);
            setTextCenterColor(textItem);
            textItem.setRotationX(ROTATION_CENTER);
            textItem.setAlpha((float) 1.0);
        } else if (position - 1 == centerPosition) {
            textItem.setTextAppearance(mContext, R.style.PickerUI_Near_Center_Item);
            setTextNoCenterColor(textItem);
            textItem.setRotationX(ROTATION_TWICE_ABOVE);
            textItem.setAlpha((float) 1.0);
        } else if (position - 2 == centerPosition) {
            textItem.setTextAppearance(mContext, R.style.PickerUI_Far_Center_Item);
            setTextNoCenterColor(textItem);
            textItem.setRotationX(ROTATION_FIRST);
            textItem.setAlpha((float) 0.7);
        } else if (position + 1 == centerPosition) {
            textItem.setTextAppearance(mContext, R.style.PickerUI_Near_Center_Item);
            setTextNoCenterColor(textItem);
            textItem.setRotationX(ROTATION_BELOW);
            textItem.setAlpha((float) 1.0);
        } else if (position + 2 == centerPosition) {
            textItem.setTextAppearance(mContext, R.style.PickerUI_Far_Center_Item);
            setTextNoCenterColor(textItem);
            textItem.setRotationX(ROTATION_TWICE_BELOW);
            textItem.setAlpha((float) 0.7);
        } else {

            if (position < centerPosition) {
                textItem.setRotationX(ROTATION_BELOW_FAR);
            } else {
                textItem.setRotationX(ROTATION_ABOVE_FAR);
            }

            textItem.setTextAppearance(mContext, R.style.PickerUI_Small_Item);
        }
    }

    private void setTextCenterColor(TextView textItem) {
        if (mColorTextCenter != -1) {
            textItem.setTextColor(mColorTextCenter);
        }
    }

    private void setTextNoCenterColor(TextView textItem) {
        if (mColorTextNoCenter != -1) {
            textItem.setTextColor(mColorTextNoCenter);
        }
    }


    public void setColorTextCenter(int color) {
        mColorTextCenter = color;
    }

    public void setColorTextNoCenter(int color) {
        mColorTextNoCenter = color;
    }

    void setItems(List<String> rawItems, int position) {

        addEmptyRows(rawItems);

        if (position == -1) {
            centerPosition = 2;
        } else {
            centerPosition = position + 2;
        }

    }
    public void handleSelectEvent(int position) {
        this.centerPosition = position;
        this.notifyDataSetChanged();
    }

    public void setItemsClickables(boolean itemsClickables) {
        this.itemsClickables = itemsClickables;
    }

    private void setPositonsNoClickables() {

        positionsNoClickables.put(0, 0);
        positionsNoClickables.put(1, 1);
        positionsNoClickables.put(items.size() - 2, items.size() - 2);
        positionsNoClickables.put(items.size() - 1, items.size() - 1);
    }

    private void addEmptyRows(List<String> rawItems) {

        List<String> emptyRows = Arrays.asList(EMPTY_STRING, EMPTY_STRING);
        List<String> items = new ArrayList<String>();
        items.addAll(emptyRows);
        items.addAll(rawItems);
        items.addAll(emptyRows);

        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isEnabled(int position) {
        if (!itemsClickables) {
            return false;
        } else {

            boolean isClickable = positionsNoClickables.get(position, -1) == -1;
            return isClickable && super.isEnabled(position);
        }
    }

    public static class ViewHolder {

        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
