package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.events.DeleteOrderClickEvent;
import com.mybaltazar.baltazar2.events.DeleteQuestionClickEvent;
import com.mybaltazar.baltazar2.models.BaseUserContent;
import com.mybaltazar.baltazar2.models.ShopOrder;
import com.mybaltazar.baltazar2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class OrderItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblName)   TextView lblName;
    @BindView(R.id.lblDate)         TextView lblDate;
    @BindView(R.id.lblStatus)       TextView lblStatus;
    @BindView(R.id.lblPrice)        TextView lblPrice;
    @BindView(R.id.btnDelete)       ImageView btnDelete;

    public OrderItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class OrderItemsAdapter extends BaseRecyclerViewAdapter<OrderItemViewHolder, ShopOrder>
{
    public OrderItemsAdapter(BaseActivity activity, List<ShopOrder> list) {
        super(activity, list, R.layout.item_order);
    }

    @Override
    protected OrderItemViewHolder createViewHolder(View view) {
        OrderItemViewHolder vh = new OrderItemViewHolder(view);
        vh.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopOrder item = (ShopOrder)v.getTag();
                EventBus.getDefault().post(new DeleteOrderClickEvent(item));
            }
        });
        return vh;
    }

    @Override
    protected void onBindViewHolder(OrderItemViewHolder vh, ShopOrder item)
    {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        vh.lblDate.setText(StringUtils.getPersianDateString(item.orderDate));
        vh.lblName.setText(item.shopItemName);
        vh.lblPrice.setText(String.valueOf(item.coinCost));
        vh.btnDelete.setTag(item);
        vh.btnDelete.setVisibility(item.status == ShopOrder.OrderStatus.WaitForApprove ? View.VISIBLE : View.INVISIBLE);

        int statusStrId = 0;
        int statusColorId = 0;
        switch (item.status) {
            case WaitForApprove:
                statusColorId = R.color.red;
                statusStrId = R.string.unpublished;
                break;
            case Approved:
                statusColorId = R.color.colorAccent;
                statusStrId = R.string.sent;
                break;
            case Delivered:
                statusColorId = R.color.green;
                statusStrId = R.string.sent;
                break;
            case Rejected:
                statusColorId = R.color.red;
                statusStrId = R.string.rejected;
                break;
        }
        vh.lblStatus.setText(statusStrId);
        vh.lblStatus.setTextColor(activity.getResources().getColor(statusColorId));
    }
}
