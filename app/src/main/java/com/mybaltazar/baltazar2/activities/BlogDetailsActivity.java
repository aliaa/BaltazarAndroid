package com.mybaltazar.baltazar2.activities;

import android.view.View;
import android.widget.TextView;

import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.models.Blog;
import com.mybaltazar.baltazar2.utils.StringUtils;

import butterknife.BindView;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class BlogDetailsActivity extends BaseActivity
{
    @BindView(R.id.lblTitle)    TextView lblTitle;
    @BindView(R.id.lblDate)     TextView lblDate;
    @BindView(R.id.lblContent)  TextView lblContent;
    @BindView(R.id.img)         ImageViewZoom img;

    public BlogDetailsActivity() {
        super(R.layout.activity_blog_details, false, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Blog blog = (Blog)getIntent().getSerializableExtra("item");
        loadUI(blog);
    }

    private void loadUI(Blog blogItem)
    {
        lblTitle.setText(blogItem.title);
        lblDate.setText(StringUtils.getPersianDateString(blogItem.dateAdded));

        String content = blogItem.htmlContent;
        if(content == null || content.equals(""))
            content = blogItem.summary;
        setHtmlContentToTextView(lblContent, content);

        if(blogItem.hasImage)
        {
            String url = getImageUrlById(blogItem.id);
            BaseActivity.loadImage(url, img);
        }
        else
            img.setVisibility(View.GONE);
    }
}
