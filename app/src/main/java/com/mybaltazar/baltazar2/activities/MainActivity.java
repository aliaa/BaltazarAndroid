package com.mybaltazar.baltazar2.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_my_questions:
                changeFragment(myQuestionsFragment);
                return true;
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

    @Override
    public void onBackPressed() {
        if(isPageFragment(currentFragment))
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
}
