package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;

public class LoadingSplashScreen extends AppCompatActivity
{
    private static final int TIME_NEW_APP = 3500;
    private static final int TIME_TEXT_DISPLAYED = 2000;

    private TextView appName;
    private TextView designerInfo;
    private LottieAnimationView lottieAnimationView;
    private static final String info = "Name : Darie-Teofil\n" + "Surname : Glanda\n" + "Major : Computer Science\n" +
            "Graduation Year : 2022/2023\n" + "Final project : IOT app\n" + "Technologies used : \n\t\t   - C++ (Arduino)" +
            "\n\t\t   - Java (Android Studio - backend)" + "\n\t\t   - XML (Android Studio - frontend)" +
            "\n\t\t   - Python (OpenCV, Cascade Classifier, Flask)" + "\n\t\t   - HTML/CSS (website - frontend)" +
            "\n\t\t   - Javascript (website - frontend, backend)" + "\n\t\t   - Firebase (Database)";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_splash_screen);

        appName = (TextView) findViewById(R.id.textViewCreatorName);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie);
        designerInfo = (TextView) findViewById(R.id.textViewCreatorInfo);

        makeTextItalic(appName, designerInfo); //make text italic for textViews
        createAnimation(appName, lottieAnimationView); //moves appName from down to up and car animation from left to right

        displayText(); //text will be displayed after 2000 seconds
        openNewActivity(); //app will close this activity and open a new one in 4500 seconds
    }

    public void makeTextItalic(TextView appName, TextView designerInfo)
    {
        appName.setTypeface(null, Typeface.ITALIC);
        designerInfo.setTypeface(null, Typeface.ITALIC);
    }

    public void createAnimation(TextView appName, LottieAnimationView lottie)
    {
        lottie.animate().translationX(2000).setDuration(2700).setStartDelay(1000); //animation for car
        appName.animate().translationY(-1400).setDuration(2500).setStartDelay(0); //animation for text
    }

    public void openNewActivity()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
                startActivity(intent);
                finish();
            }
        }, TIME_NEW_APP); //the time that the splash screen will be displayed
    }

    public void displayText()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                designerInfo.setText(info);
            }
        }, TIME_TEXT_DISPLAYED); //after the time the text will be displayed
    }
}