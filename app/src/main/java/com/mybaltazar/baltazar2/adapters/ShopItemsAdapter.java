package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.ShopItem;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class ShopItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblName)     TextView lblName;
    @BindView(R.id.lblDate)     TextView lblDate;
    @BindView(R.id.img)         ImageViewZoom img;
    @BindView(R.id.lblCount)    TextView lblCount;
    @BindView(R.id.btnBuy)      Button btnBuy;

    ShopItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class ShopItemsAdapter extends BaseRecyclerViewAdapter<ShopItemViewHolder, ShopItem>
{
    public ShopItemsAdapter(BaseActivity activity, List<ShopItem> list) {
        super(activity, list, R.layout.item_shop);
    }

    @Override
    public void onClick(View view) { }

    @Override
    protected ShopItemViewHolder createViewHolder(View view) {
        ShopItemViewHolder vh = new ShopItemViewHolder(view);
        vh.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null)
                    clickListener.onItemClick((ShopItem)v.getTag());
            }
        });
        return vh;
    }

    @Override
    protected void onBindViewHolder(ShopItemViewHolder vh, ShopItem item) {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        vh.itemView.setTag(item);
        vh.lblName.setText(item.name);
        vh.lblCount.setText(String.valueOf(item.coinCost));
        vh.lblDate.setText(StringUtils.getPersianDate(item.dateAdded));
        if(item.hasImage) {
            String url = activity.getString(R.string.media_base_url) + activity.getString(R.string.image_dir) + item.id + ".jpg";
            BaseActivity.loadImage(url, vh.img);
        }
    }
}
