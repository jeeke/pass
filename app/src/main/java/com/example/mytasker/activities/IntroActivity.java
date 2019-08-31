package com.example.mytasker.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytasker.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("PassOn");
        sliderPage2.setDescription("Pass your task to the community of the hobbyist");
        sliderPage2.setImageDrawable(R.drawable.badge001);
        sliderPage2.setBgColor(getResources().getColor(R.color.orange));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("PassOn");
        sliderPage.setDescription("Pass your queries to the community");
        sliderPage.setImageDrawable(R.drawable.badge002);
        sliderPage.setBgColor(getResources().getColor(R.color.dark_blue_grey));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("PassOn");
        sliderPage3.setDescription("Pass ongoing and upcoming events to the feed");
        sliderPage3.setImageDrawable(R.drawable.badge003);
        sliderPage3.setBgColor(getResources().getColor(R.color.green_A400));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        // Hide Skip/Done button.
        showSkipButton(true);
        showStatusBar(false);
        setProgressButtonEnabled(true);
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