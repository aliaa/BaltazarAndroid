package com.mybaltazar.baltazar2.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.models.CoinTransaction;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class TransactionItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblType)     TextView lblType;
    @BindView(R.id.lblDate)     TextView lblDate;
    @BindView(R.id.lblAmount)   TextView lblAmount;

    TransactionItemViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class TransactionsAdapter extends BaseRecyclerViewAdapter<TransactionItemViewHolder, CoinTransaction>
{
    public TransactionsAdapter(BaseActivity activity, List<CoinTransaction> list) {
        super(activity, list, R.layout.item_transaction);
    }

    @Override
    protected TransactionItemViewHolder createViewHolder(View view)
    {
        return new TransactionItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(TransactionItemViewHolder vh, CoinTransaction item)
    {
        BaseActivity activity = activityRef.get();
        if(activity == null)
            return;
        vh.lblDate.setText(StringUtils.getPersianDate(item.date));

        int typeStrId;
        if(item.question != null)
        {
            if(item.question.fromBaltazar)
                typeStrId = R.string.transaction_type_league_answer;
            else
                typeStrId = R.string.transaction_type_question_answer;
            vh.lblAmount.setTextColor(activity.getResources().getColor(R.color.green));
            vh.lblAmount.setText(String.valueOf(item.amount));
        }
        else {
            typeStrId = R.string.transaction_type_buy;
            vh.lblAmount.setTextColor(activity.getResources().getColor(R.color.red));
            vh.lblAmount.setText(String.valueOf(-item.amount));
        }

        vh.lblType.setText(typeStrId);
    }
}
