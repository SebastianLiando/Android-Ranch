package com.zetzaus.sunset;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.fragment.app.Fragment;

/**
 * A {@link Fragment} subclass that displays the scenery of a sunset.
 */
public class SunsetFragment extends Fragment {

    private View mSunView;
    private View mSkyView;
    private View mSceneView;
    private View mReflectionView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean mIsSetting = false;
    private boolean mIsNight = false;

    private AnimatorSet mAnimatorSet;

    /**
     * Returns {@link SunsetFragment}.
     *
     * @return {@link SunsetFragment}.
     */
    public static SunsetFragment newInstance() {
        SunsetFragment fragment = new SunsetFragment();
        return fragment;
    }

    /**
     * Sets up this fragment.
     *
     * @param inflater           the layout inflater.
     * @param container          the container to be inflated.
     * @param savedInstanceState the saved system state.
     * @return the inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSceneView = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSunView = mSceneView.findViewById(R.id.sun);
        mSkyView = mSceneView.findViewById(R.id.sky);
        mReflectionView = mSceneView.findViewById(R.id.reflection);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);

        startPulsate(mSunView);
        startPulsate(mReflectionView);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimatorSet != null) mAnimatorSet.cancel();

                startAnimation();

                mIsSetting = !mIsSetting;
            }
        });

        return mSceneView;
    }

    /**
     * Starts sunset or sunrise animation.
     */
    private void startAnimation() {
        int sunYStart = Math.round(mSunView.getY());
        int sunYStop = (int) (mSkyView.getHeight() + Math.round(mSunView.getHeight() * 0.6));

        int startSkyColor = getCurrentBgColor();
        int endSkyColor = mSunsetSkyColor;

        int startNightColor = mSunsetSkyColor;
        int endNightColor = mNightSkyColor;

        int reflectionYStart = mReflectionView.getBottom();
        int reflectionYEnd = 0;

        boolean playNight = true;

        if (mIsSetting) {
            sunYStop = (int) ((mSkyView.getHeight() / 2) - Math.round(mSunView.getHeight() * 0.6));

            startSkyColor = mIsNight ? mSunsetSkyColor : getCurrentBgColor();
            endSkyColor = mBlueSkyColor;

            startNightColor = getCurrentBgColor();
            endNightColor = mSunsetSkyColor;

            reflectionYStart = mReflectionView.getBottom();
            reflectionYEnd = mSceneView.getBottom();

            if (!mIsNight) playNight = false;
        }

        ObjectAnimator sunAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYStop)
                .setDuration(3000);
        sunAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator reflectionAnimator = ObjectAnimator
                .ofInt(mReflectionView, "bottom", reflectionYStart, reflectionYEnd)
                .setDuration(3000);
        if (mIsSetting)
            reflectionAnimator.setInterpolator(new AccelerateInterpolator());
        else
            reflectionAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator skyAnimator = getBgColorAnimator(mSkyView, startSkyColor,
                endSkyColor, 3000);
        skyAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsNight = false;
            }
        });

        ObjectAnimator nightAnimator = getBgColorAnimator(mSkyView, startNightColor,
                endNightColor, playNight ? 1500 : 0);
        nightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsNight = true;
            }
        });

        // Play animation
        if (!mIsSetting) {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.play(sunAnimator)
                    .with(skyAnimator)
                    .with(reflectionAnimator)
                    .before(nightAnimator);
        } else {
            mAnimatorSet = new AnimatorSet();

            mAnimatorSet.play(nightAnimator)
                    .before(sunAnimator)
                    .before(skyAnimator)
                    .before(reflectionAnimator);
        }

        mAnimatorSet.start();
    }

    /**
     * Returns an {@link ObjectAnimator} that animates a {@link View}'s color from one to another for the given duration.
     *
     * @param target     the target {@link View}.
     * @param startColor the starting color.
     * @param endColor   the ending color.
     * @param duration   animation duration.
     * @return the {@link ObjectAnimator}.
     */
    private ObjectAnimator getBgColorAnimator(View target, int startColor, int endColor, int duration) {
        ObjectAnimator bgAnimator = ObjectAnimator
                .ofInt(target, "backgroundColor", startColor, endColor)
                .setDuration(duration);
        bgAnimator.setEvaluator(new ArgbEvaluator());

        return bgAnimator;
    }

    /**
     * Returns the current scenery's sky color.
     *
     * @return the current scenery's sky color.
     */
    private int getCurrentBgColor() {
        ColorDrawable background = (ColorDrawable) mSkyView.getBackground();
        return background.getColor();
    }

    /**
     * Gives pulsating animation to the target {@link View}.
     *
     * @param target the target {@link View}.
     */
    private void startPulsate(View target) {
        PropertyValuesHolder holderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f);
        PropertyValuesHolder holderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", .65f, 1f);

        ObjectAnimator mHeatAnimator = ObjectAnimator.ofPropertyValuesHolder(target, holderScaleX, holderScaleY, holderAlpha)
                .setDuration(10 * 1000);
        mHeatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mHeatAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mHeatAnimator.start();
    }
}
