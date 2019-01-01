package com.mybaltazar.baltazar2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mybaltazar.baltazar2.activities.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, I> extends RecyclerView.Adapter<VH> implements View.OnClickListener {
    public final ArrayList<I> list;
    final WeakReference<BaseActivity> activityRef;
    private final int layoutId;

    public BaseRecyclerViewAdapter(BaseActivity activity, Collection<I> list, int layoutId) {
        this.list = new ArrayList<>(list);
        this.activityRef = new WeakReference<>(activity);
        this.layoutId = layoutId;
    }

    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return null;
        View v = activity.getLayoutInflater().inflate(layoutId, parent, false);
        v.setOnClickListener(this);
        return createViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        I item = list.get(position);
        holder.itemView.setTag(item);

    }

    protected abstract VH createViewHolder(View view);

    protected abstract void onBindViewHolder(VH vh, I item);

    private OnItemClickListener<I> clickListener = null;
    public void setOnItemClickListener(OnItemClickListener<I> clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        if(clickListener != null) {
            I item = (I) view.getTag();
            clickListener.onItemClick(item);
        }
    }
}
