package com.mybaltazar.baltazar2.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.activities.BaseActivity;
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
        return new BlogItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(BlogItemViewHolder vh, Blog item) {
        vh.lblTitle.setText(item.title);
        vh.lblDate.setText(StringUtils.getPersianDate(item.dateAdded));

        vh.img.setVisibility(item.hasImage ? View.VISIBLE : View.GONE);
        if(item.hasImage) {
            BaseActivity activity = activityRef.get();
            if(activity != null) {
                String url = activity.getImageUrlById(item.id);
                BaseActivity.loadImage(url, vh.img);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            vh.lblContent.setText(Html.fromHtml(item.htmlContent, Html.FROM_HTML_MODE_COMPACT));
        else
            vh.lblContent.setText(Html.fromHtml(item.htmlContent));
        vh.lblContent.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
