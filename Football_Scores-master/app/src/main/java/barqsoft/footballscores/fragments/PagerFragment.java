package barqsoft.footballscores.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.MainActivity;
import barqsoft.footballscores.adapters.PagerAdapter;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.Constants;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{

    public ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);

        final PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.initialize(getActivity());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MainActivity.currentFragment);

        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    public void showDetail()
    {
        viewPager.setCurrentItem(Constants.TODAY_FRAGMENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        FootballScoresSyncAdapter.syncImmediately(getActivity());
    }
}
