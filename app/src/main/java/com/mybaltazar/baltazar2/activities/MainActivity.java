package com.mybaltazar.baltazar2.activities;

import android.content.Context;
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
import com.mybaltazar.baltazar2.webservices.CommonData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener
{
    class FragmentPage
    {
        public int icon;
        public int title;
        private BottomNavigationItem bottomNavigationItem;
        public BaseFragment fragment;
        private ShapeBadgeItem badge = null;

        public FragmentPage(int icon, int title, BaseFragment fragment, boolean createBadge) {
            this.icon = icon;
            this.title = title;
            this.bottomNavigationItem = new BottomNavigationItem(icon, title);
            this.fragment = fragment;
            if(createBadge) {
                badge = new ShapeBadgeItem();
                badge.setShape(ShapeBadgeItem.SHAPE_OVAL);
                badge.setShapeColorResource(R.color.red);
                badge.setGravity(Gravity.TOP | Gravity.RIGHT);
                bottomNavigationItem.setBadgeItem(badge);
            }
        }

        public void setBadgeItem(ShapeBadgeItem badgeItem) {
            bottomNavigationItem.setBadgeItem(badgeItem);
        }

        public BottomNavigationItem getBottomNavigationItem(){
            return bottomNavigationItem;
        }

        public ShapeBadgeItem getBadge() {
            return badge;
        }
    }

    public static final String NEW_BLOGS = "newBlog";
    public static final String NEW_ANSWERS = "newAnswer";
    public static final String NEW_SHOPS = "newShops";
    public static final String IS_TEACHER = "isTeacher";

    private FragmentPage qaFragment;
    private FragmentPage myQuestionsFragment;
    private FragmentPage shopFragment;
    private FragmentPage leagueFragment;
    private FragmentPage blogFragment;
    private List<FragmentPage> fragmentPages;

    private ProfileFragment profileFragment;
    private NewQuestionFragment newQuestionFragment;
    private BaseFragment currentFragment;

    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;

    @BindView(R.id.bottomNavigationBar)
    BottomNavigationBar bottomNavigationBar;

    private Menu menu;

    public MainActivity() {
        super(R.layout.activity_main, true);
    }

    public static void open(Context context, CommonData.Notifications notification, boolean isTeacher)
    {
        Intent intent = new Intent(context, MainActivity.class);
        if(notification != null)
        {
            intent.putExtra(MainActivity.NEW_BLOGS, notification.newBlogs);
            intent.putExtra(MainActivity.NEW_ANSWERS, notification.newAnswers);
            intent.putExtra(MainActivity.NEW_SHOPS, notification.newShops);
            intent.putExtra(MainActivity.IS_TEACHER, isTeacher);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        qaFragment = new FragmentPage(R.drawable.ic_question, R.string.qa, new QAFragment(), false);
        myQuestionsFragment = new FragmentPage(R.drawable.ic_raise_hand, R.string.my_questions, new MyQuestionsFragment(), true);
        leagueFragment = new FragmentPage(R.drawable.ic_cup, R.string.league, new LeagueFragment(), false);
        shopFragment = new FragmentPage(R.drawable.ic_basket, R.string.shop, new ShopFragment(), true);
        blogFragment = new FragmentPage(R.drawable.ic_newspaper, R.string.blog, new BlogFragment(), true);
        profileFragment = new ProfileFragment();
        newQuestionFragment = new NewQuestionFragment();

        boolean isTeacher = getIntent().getBooleanExtra(IS_TEACHER, false);
        fragmentPages = new ArrayList<>();
        if(isTeacher) {
            fragmentPages.add(qaFragment);
            fragmentPages.add(blogFragment);
        }
        else {
            fragmentPages.add(qaFragment);
            fragmentPages.add(myQuestionsFragment);
            fragmentPages.add(leagueFragment);
            fragmentPages.add(shopFragment);
            fragmentPages.add(blogFragment);
        }

        for (FragmentPage page : fragmentPages) {
            bottomNavigationBar.addItem(page.getBottomNavigationItem());
        }
        bottomNavigationBar.initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.selectTab(0, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
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
            blogFragment.getBadge().show();
        else
            blogFragment.getBadge().hide();

        if(newShops > 0)
            shopFragment.getBadge().show();
        else
            shopFragment.getBadge().hide();

        if(newAnswers > 0)
            myQuestionsFragment.getBadge().show();
        else
            myQuestionsFragment.getBadge().hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_profile:
                changeFragment(profileFragment);
                return true;

            case R.id.menu_item_logout:
                setToken(null);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            case R.id.menu_item_contact_us:
                startActivity(new Intent(this, ContactUsActivity.class));
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
        changeFragment(fragmentPages.get(position).fragment);
    }

    @Override
    public void onTabUnselected(int position) { }

    @Override
    public void onTabReselected(int position) {
        if(currentFragment != fragmentPages.get(position).fragment)
            changeFragment(fragmentPages.get(position).fragment);
    }

    private void changeFragment(BaseFragment fragment)
    {
        currentFragment = fragment;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();

        if(menu != null)
            menu.findItem(R.id.menu_item_filter).setVisible(fragment ==  qaFragment.fragment);
        if(fragment == blogFragment.fragment)
            blogFragment.getBadge().hide();
        else if(fragment == shopFragment.fragment)
            shopFragment.getBadge().hide();
        else if(fragment == myQuestionsFragment.fragment)
            myQuestionsFragment.getBadge().hide();

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
            changeFragment(myQuestionsFragment.fragment);
        else if(isPageFragment(currentFragment))
            super.onBackPressed();
        else
            changeFragment(qaFragment.fragment);
    }

    private boolean isPageFragment(BaseFragment fragment)
    {
        for (FragmentPage f : fragmentPages)
            if(f.fragment == fragment)
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
