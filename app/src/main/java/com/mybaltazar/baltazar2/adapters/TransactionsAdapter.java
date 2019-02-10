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
        vh.lblDate.setText(StringUtils.getPersianDateString(item.date));
        String amountStr = String.valueOf(item.amount);
        if(item.amount > 0)
            amountStr = "+"+amountStr;
        vh.lblAmount.setText(amountStr);
        vh.lblAmount.setTextColor(activity.getResources().getColor(item.amount > 0 ? R.color.green : R.color.red));
        int typeStrId;
        switch (item.type)
        {
            case Buy:
                typeStrId = R.string.transaction_type_buy;
                break;
            case AskQuestion:
                typeStrId = R.string.transaction_type_ask_question;
                break;
            case AnswerQuestion:
                typeStrId = R.string.transaction_type_question_answer;
                break;
            case AnswerBaltazar:
                typeStrId = R.string.transaction_type_league_answer;
                break;
            case InviteFriend:
                typeStrId = R.string.invite_friends;
                break;
            case ProfileCompletion:
                typeStrId = R.string.additional_profile;
                break;
            default:
                return;
        }
        vh.lblType.setText(typeStrId);
    }
}
