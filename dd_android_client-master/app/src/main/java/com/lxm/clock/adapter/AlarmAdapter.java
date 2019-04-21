package com.lxm.clock.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxm.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context mContext;
    private List<String> timeList;
    private ItemClickListener itemClickListener;

    public AlarmAdapter(Context context, List<String> timeList) {
        this.mContext = context;
        this.timeList = timeList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder alarmViewHolder, int position) {
        alarmViewHolder.timeTv.setText(timeList.get(position));
        final int i = position;
        alarmViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeList == null ? 0 : timeList.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time)
        TextView timeTv;

        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }
}
