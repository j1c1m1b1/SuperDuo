package barqsoft.footballscores.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activities.MainActivity;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        myPageAdapter mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentDate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(dateFormat.format(fragmentDate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.currentFragment);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FootballScoresSyncAdapter.syncImmediately(getActivity());
    }

    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            long dateInMillis = System.currentTimeMillis()+((position-2)*86400000);
            return getDayName(getActivity(), dateInMillis);
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the tvDate is today, return the localized version of "Today" instead of the actual
            // day name.

            Calendar calendar = Calendar.getInstance();

            int currentJulianDay = calendar.get(GregorianCalendar.DAY_OF_WEEK);

            calendar.setTimeInMillis(dateInMillis);

            int julianDay = calendar.get(GregorianCalendar.DAY_OF_WEEK);

            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
