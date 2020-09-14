package com.esselion.pass.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.esselion.pass.R;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro3));
        setSkipButtonEnabled(true);
        setIndicatorColor(Color.parseColor("#f47d31"), Color.parseColor("#000000"));
        showStatusBar(false);
        setIndicatorEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}