package com.dab.medireminder.ui.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.dab.medireminder.R;
import com.dab.medireminder.data.model.BloodPressure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BloodPressureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BloodPressure> itemList;
    private BloodPressureListener itemListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public BloodPressureAdapter(Activity activity, List<BloodPressure> itemList, BloodPressureListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_pressure, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final BloodPressure bloodPressure = itemList.get(position);

        itemHolder.tv_blood_pressure.setText(Html.fromHtml("Huyết áp: <font color='red'>" + bloodPressure.getMax() + "</font>/<font color='green'>" + bloodPressure.getMin() + "</font>"));

        itemHolder.tv_time.setText(simpleDateFormat.format(new Date(bloodPressure.getTimer())));

        itemHolder.itemView.setOnClickListener(v -> itemListener.onClickBloodPressure(bloodPressure));

        itemHolder.btnDelete.setOnClickListener(v -> itemListener.onClickDeleteBloodPressure(bloodPressure, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        @BindView(R.id.tv_blood_pressure)
        TextView tv_blood_pressure;

        @BindView(R.id.tv_time)
        TextView tv_time;

        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface BloodPressureListener {
        void onClickBloodPressure(BloodPressure bloodPressure);

        void onClickDeleteBloodPressure(BloodPressure bloodPressure, int position);
    }

}

