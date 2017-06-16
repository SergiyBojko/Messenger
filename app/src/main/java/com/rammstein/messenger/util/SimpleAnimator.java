package com.rammstein.messenger.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by user on 10.06.2017.
 */

public class SimpleAnimator {
    public static void animateHeight (final View view, final int height, final int delta){
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                float currentDelta = delta*value;
                params.height = height + (int)currentDelta;
                view.setLayoutParams(params);

            }
        });
        animator.start();
    }

}
