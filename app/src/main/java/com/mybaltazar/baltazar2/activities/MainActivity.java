package com.mybaltazar.baltazar2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
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
import khangtran.preferenceshelper.PrefHelper;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener
{
    private static final int[] PAGES_ICON = new int[] {
            R.drawable.ic_raise_hand,
            R.drawable.ic_basket,
            R.drawable.ic_cup,
            R.drawable.ic_newspaper,
            R.drawable.ic_user
    };

    private static final int[] PAGES_TITLE = new int[] {
            R.string.qa,
            R.string.shop,
            R.string.league,
            R.string.blog,
            R.string.profile
    };

    private BaseFragment[] pagesFragments;
    private BaseFragment currentFragment;
    private MyQuestionsFragment myQuestionsFragment;
    private NewQuestionFragment newQuestionFragment;

    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;

    @BindView(R.id.bottomNavigationBar)
    BottomNavigationBar bottomNavigationBar;

    private Menu menu;

    public MainActivity() {
        super(R.layout.activity_main, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pagesFragments = new BaseFragment[5];
        pagesFragments[0] = new QAFragment();
        pagesFragments[1] = new ShopFragment();
        pagesFragments[2] = new LeagueFragment();
        pagesFragments[3] = new BlogFragment();
        pagesFragments[4] = new ProfileFragment();
        myQuestionsFragment = new MyQuestionsFragment();
        newQuestionFragment = new NewQuestionFragment();

        for (int i = 0; i < 5; i++)
            bottomNavigationBar.addItem(new BottomNavigationItem(PAGES_ICON[i], PAGES_TITLE[i]));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_my_questions:
                changeFragment(myQuestionsFragment);
                return true;

            case R.id.menu_item_logout:
                PrefHelper.removeKey(PREF_TOKEN);
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
            menu.findItem(R.id.menu_item_filter).setVisible(fragment ==  pagesFragments[0]);

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
