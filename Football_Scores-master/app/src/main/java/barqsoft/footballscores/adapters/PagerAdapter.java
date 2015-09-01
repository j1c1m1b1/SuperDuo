package barqsoft.footballscores.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.fragments.MainScreenFragment;
import barqsoft.footballscores.utils.Constants;

/**
 * @author Julio Mendoza on 9/1/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{

    SimpleDateFormat dateFormat;
    private Context context;
    private SimpleDateFormat dayFormat;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public void initialize(Context context)
    {
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        Date date = new Date(System.currentTimeMillis()+((position-2)*86400000));
        String fragmentDate = dateFormat.format(date);
        return MainScreenFragment.newInstance(fragmentDate);
    }

    @Override
    public int getCount() {
        return Constants.NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return getDayName(position);
    }

    public String getDayName(int position) {
        // If the tvDate is today, return the localized version of "Today" instead of the actual
        // day name.

        long dateInMillis = System.currentTimeMillis()+((position-2)*86400000);

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
            return dayFormat.format(dateInMillis);
        }
    }
}
