package com.zetzaus.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import androidx.fragment.app.Fragment;

/**
 * A {@link Fragment} subclass that displays the scenery of a sunset.
 */
public class SunsetFragment extends Fragment {

    private View mSunView;
    private View mSkyView;
    private View mSceneView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean isSunSetting = false;

    public static SunsetFragment newInstance() {
        SunsetFragment fragment = new SunsetFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSceneView = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSunView = mSceneView.findViewById(R.id.sun);
        mSkyView = mSceneView.findViewById(R.id.sky);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });

        return mSceneView;
    }

    private void startAnimation() {
        int sunYStart = mSunView.getTop();
        int sunYStop = mSkyView.getHeight();
        ObjectAnimator sunAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYStop)
                .setDuration(3000);
        sunAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator skyAnimator = getBgColorAnimator(mSkyView, mBlueSkyColor,
                mSunsetSkyColor, 3000);

        ObjectAnimator nightAnimator = getBgColorAnimator(mSkyView, mSunsetSkyColor,
                mNightSkyColor, 1500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(sunAnimator)
                .with(skyAnimator)
                .before(nightAnimator);
        animatorSet.start();
    }

    private ObjectAnimator getBgColorAnimator(View target, int startColor, int endColor, int duration) {
        ObjectAnimator bgAnimator = ObjectAnimator
                .ofInt(target, "backgroundColor", startColor, endColor)
                .setDuration(duration);
        bgAnimator.setEvaluator(new ArgbEvaluator());

        return bgAnimator;
    }
}
