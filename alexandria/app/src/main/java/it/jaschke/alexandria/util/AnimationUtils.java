package it.jaschke.alexandria.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author Julio Mendoza on 8/21/15.
 */
public class AnimationUtils {

    public static ObjectAnimator appear(final View view)
    {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alpha.setDuration(Constants.DEFAULT_DURATION);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return alpha;
    }


    public static AnimatorSet playSequentially(Animator... animators)
    {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);

        return set;
    }

    public static AnimatorSet playTogether(Animator... animators)
    {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);

        return set;
    }

    public static ObjectAnimator disappear(final View view)
    {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        alpha.setDuration(Constants.DEFAULT_DURATION);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return  alpha;
    }
}
