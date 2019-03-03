package com.mybaltazar.baltazar2.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
import com.mybaltazar.baltazar2.activities.BlogDetailsActivity;
import com.mybaltazar.baltazar2.models.Blog;
import com.mybaltazar.baltazar2.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

class BlogItemViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.lblTitle)    TextView lblTitle;
    @BindView(R.id.lblDate)     TextView lblDate;
    @BindView(R.id.lblContent)  TextView lblContent;
    @BindView(R.id.img)         ImageViewZoom img;
    @BindView(R.id.btnReadMore) Button btnReadMore;

    BlogItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

public class BlogAdapter extends BaseRecyclerViewAdapter<BlogItemViewHolder, Blog>
{
    public BlogAdapter(BaseActivity activity, List<Blog> list) {
        super(activity, list, R.layout.item_blog);
    }

    @Override
    protected BlogItemViewHolder createViewHolder(View view) {
        BlogItemViewHolder vh = new BlogItemViewHolder(view);
        vh.btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Blog item = (Blog)v.getTag();
                BaseActivity activity = activityRef.get();
                if(item != null && activity != null)
                {
                    Intent i = new Intent(activity, BlogDetailsActivity.class);
                    i.putExtra("item", item);
                    activity.startActivity(i);
                }
            }
        });
        return vh;
    }

    @Override
    protected void onBindViewHolder(BlogItemViewHolder vh, Blog item)
    {
        vh.lblTitle.setText(item.title);
        vh.lblDate.setText(StringUtils.getPersianDateString(item.dateAdded));

        vh.img.setVisibility(item.hasImage ? View.VISIBLE : View.GONE);
        if(item.hasImage) {
            BaseActivity activity = activityRef.get();
            if(activity != null) {
                String url = activity.getImageUrlById(item.id);
                BaseActivity.loadImage(url, vh.img);
            }
        }
        String summary = item.summary;
        if(summary == null || summary.equals("")) {
            int length = Math.min(100, item.htmlContent.length());
            summary = item.htmlContent.substring(0, length) + "...";
        }
        BaseActivity.setHtmlContentToTextView(vh.lblContent, summary);
        vh.btnReadMore.setTag(item);
    }
}
