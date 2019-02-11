package com.mybaltazar.baltazar2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.mybaltazar.baltazar2.R;
import com.mybaltazar.baltazar2.fragments.BaseFragment;
import com.mybaltazar.baltazar2.fragments.BlogFragment;
import com.mybaltazar.baltazar2.fragments.LeagueFragment;
import com.mybaltazar.baltazar2.fragments.MyQuestionDetailsFragment;
import com.mybaltazar.baltazar2.fragments.MyQuestionsFragment;
import com.mybaltazar.baltazar2.fragments.NewQuestionFragment;
import com.mybaltazar.baltazar2.fragments.ProfileFragment;
import com.mybaltazar.baltazar2.fragments.QAFragment;
import com.mybaltazar.baltazar2.fragments.QuestionDetailFragment;
import com.mybaltazar.baltazar2.fragments.ShopFragment;
import com.mybaltazar.baltazar2.models.Question;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener
{
    private static final int[] PAGES_ICON = new int[] {
            R.drawable.ic_raise_hand,
            R.drawable.ic_inbox,
            R.drawable.ic_basket,
            R.drawable.ic_cup,
            R.drawable.ic_newspaper,
//            R.drawable.ic_user
    };

    private static final int[] PAGES_TITLE = new int[] {
            R.string.qa,
            R.string.my_questions,
            R.string.shop,
            R.string.league,
            R.string.blog,
//            R.string.profile
    };

    public static final String NEW_BLOGS = "newBlog";
    public static final String NEW_ANSWERS = "newAnswer";
    public static final String NEW_SHOPS = "newShops";

    private QAFragment qaFragment;
    private ShopFragment shopFragment;
    private LeagueFragment leagueFragment;
    private BlogFragment blogFragment;
    private ProfileFragment profileFragment;
    private MyQuestionsFragment myQuestionsFragment;
    private NewQuestionFragment newQuestionFragment;
    private BaseFragment[] pagesFragments;
    private BaseFragment currentFragment;
    private BottomNavigationItem[] bottomNavigationItems;

    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;

    @BindView(R.id.bottomNavigationBar)
    BottomNavigationBar bottomNavigationBar;

    ShapeBadgeItem myQuestionsBadge;
    ShapeBadgeItem blogBadge;
    ShapeBadgeItem shopBadge;

    private Menu menu;

    public MainActivity() {
        super(R.layout.activity_main, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pagesFragments = new BaseFragment[PAGES_ICON.length];
        pagesFragments[0] = qaFragment = new QAFragment();
        pagesFragments[1] = myQuestionsFragment = new MyQuestionsFragment();
        pagesFragments[2] = shopFragment = new ShopFragment();
        pagesFragments[3] = leagueFragment = new LeagueFragment();
        pagesFragments[4] = blogFragment = new BlogFragment();
        profileFragment = new ProfileFragment();
        newQuestionFragment = new NewQuestionFragment();

        myQuestionsBadge = createBadge();
        blogBadge = createBadge();
        shopBadge = createBadge();

        bottomNavigationItems = new BottomNavigationItem[PAGES_ICON.length];
        for (int i = 0; i < PAGES_ICON.length; i++) {
            BottomNavigationItem item = new BottomNavigationItem(PAGES_ICON[i], PAGES_TITLE[i]);
            bottomNavigationItems[i] = item;
            if(PAGES_ICON[i] == R.drawable.ic_inbox)
                item.setBadgeItem(myQuestionsBadge);
            else if(PAGES_ICON[i] == R.drawable.ic_newspaper)
                item.setBadgeItem(blogBadge);
            else if(PAGES_ICON[i] == R.drawable.ic_basket)
                item.setBadgeItem(shopBadge);
            bottomNavigationBar.addItem(item);
        }
        bottomNavigationBar.initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.selectTab(0, true);
    }

    private ShapeBadgeItem createBadge()
    {
        return new ShapeBadgeItem()
            .setShape(ShapeBadgeItem.SHAPE_OVAL)
            .setShapeColorResource(R.color.red)
            .setGravity(Gravity.TOP | Gravity.RIGHT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

//        int newAnswers = getIntent().getIntExtra(NEW_ANSWERS, 0);
//        MenuItem myQuestionsMenuItem = menu.findItem(R.id.menu_item_my_questions);
//        if(newAnswers > 0)
//            myQuestionsMenuItem.setIcon(R.drawable.ic_inbox_active);
//        else
//            myQuestionsMenuItem.setIcon(R.drawable.ic_inbox);

        return true;
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        int newBlogs = getIntent().getIntExtra(NEW_BLOGS, 0);
        int newShops = getIntent().getIntExtra(NEW_SHOPS, 0);
        int newAnswers = getIntent().getIntExtra(NEW_ANSWERS, 0);

        if(newBlogs > 0)
            blogBadge.show();
        else
            blogBadge.hide();

        if(newShops > 0)
            shopBadge.show();
        else
            shopBadge.hide();

        if(newAnswers > 0)
            myQuestionsBadge.show();
        else
            myQuestionsBadge.hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_item_my_questions:
//                changeFragment(myQuestionsFragment);
//                return true;
            case R.id.menu_item_profile:
                changeFragment(profileFragment);
                return true;

            case R.id.menu_item_logout:
                setToken(null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            case R.id.menu_item_contact_us:
                showOkDialog(R.string.contact_us, R.string.contact_us_text);
                return true;

            case R.id.menu_item_filter:
                if(currentFragment instanceof QAFragment)
                    ((QAFragment)currentFragment).showFilterDialog();
                break;
        }
        return false;
    }


    @Override
    public void onTabSelected(int position) {
        changeFragment(pagesFragments[position]);
    }

    @Override
    public void onTabUnselected(int position) { }

    @Override
    public void onTabReselected(int position) {
        if(currentFragment != pagesFragments[position])
            changeFragment(pagesFragments[position]);
    }

    private void changeFragment(BaseFragment fragment)
    {
        currentFragment = fragment;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();

        if(menu != null)
            menu.findItem(R.id.menu_item_filter).setVisible(fragment ==  qaFragment);
        if(fragment == blogFragment)
            blogBadge.hide();
        else if(fragment == shopFragment)
            shopBadge.hide();
        else if(fragment == myQuestionsFragment)
            myQuestionsBadge.hide();

        refreshTitle();
    }

    private void refreshTitle()
    {
        int titleId = currentFragment.getTitleId();
        if(titleId == 0)
            setTitle(R.string.app_name);
        else
            setTitle(titleId);
    }

    public void openNewQuestionFragment() {
        changeFragment(newQuestionFragment);
    }

    public void openQuestionDetailsFragment(Question item) {
        QuestionDetailFragment frag = new QuestionDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        frag.setArguments(bundle);
        changeFragment(frag);
    }

    public void openMyQuestionDetailsFragment(Question item) {
        MyQuestionDetailsFragment frag = new MyQuestionDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        frag.setArguments(bundle);
        changeFragment(frag);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof MyQuestionDetailsFragment)
            changeFragment(myQuestionsFragment);
        else if(isPageFragment(currentFragment))
            super.onBackPressed();
        else
            changeFragment(pagesFragments[0]);
    }

    private boolean isPageFragment(BaseFragment fragment)
    {
        for (BaseFragment f : pagesFragments)
            if(f == fragment)
                return true;
        return false;
    }

    public void setFilterMenuItemActive(boolean active) {
        if(menu == null)
            return;
        MenuItem filterItem = menu.findItem(R.id.menu_item_filter);
        if(filterItem != null)
            filterItem.setIcon(active ? R.drawable.ic_filter_active : R.drawable.ic_filter);
    }
}
