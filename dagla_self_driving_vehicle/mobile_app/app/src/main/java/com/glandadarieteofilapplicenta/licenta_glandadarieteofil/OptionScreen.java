package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;

public class OptionScreen extends AppCompatActivity
{
    private ImageView image_button_move_with_buttons;
    private ImageView image_button_move_with_voice_recognition;
    private ImageView image_button_move_with_finger_detection;
    private ImageView image_button_move_with_autopilot;
    private ImageView image_button_run_tests;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_screen);

        image_button_move_with_buttons = (ImageView) findViewById(R.id.pic_move_with_buttons);
        image_button_move_with_voice_recognition = (ImageView) findViewById(R.id.pic_move_with_voice_recognition);
        image_button_move_with_finger_detection = (ImageView) findViewById(R.id.pic_move_with_finger_detection);
        image_button_move_with_autopilot = (ImageView) findViewById(R.id.pic_move_with_autopilot);
        image_button_run_tests = (ImageView) findViewById(R.id.pic_run_tests);

        image_button_move_with_buttons.setClickable(true);

        image_button_move_with_buttons.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MoveCarButtons.class);
                startActivity(intent);
            }
        });

        image_button_move_with_voice_recognition.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MoveCarVoiceRecognition.class);
                startActivity(intent);
            }
        });

        image_button_move_with_finger_detection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MoveCarFingerDetection.class);
                startActivity(intent);
            }
        });

        image_button_move_with_autopilot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MoveCarWithAutopilot.class);
                startActivity(intent);
            }
        });

        image_button_run_tests.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), MoveCarWithTests.class);
                startActivity(intent);
            }
        });
    }
}