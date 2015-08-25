package it.jaschke.alexandria.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;

/**
 * @author Julio Mendoza on 8/25/15.
 */
public class Utils {

    public static Drawable getDrawable(@DrawableRes int resId, Context context)
    {
        Drawable drawable;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            drawable = context.getDrawable(resId);
        }
        else
        {
            //noinspection deprecation
            drawable = context.getResources().getDrawable(resId);
        }

        return drawable;
    }

}
