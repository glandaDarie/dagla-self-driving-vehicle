package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MoveCarWithAutopilot extends AppCompatActivity
{
    private final String url = "http://192.168.1.106:5000/app";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_car_with_autopilot);;

        textView = (TextView) findViewById(R.id.textView);

        new Thread(()->
        {
            OkHttpClient client = new OkHttpClient(); 
            Request request = new Request.Builder().url(url).build(); 
            String result = null;

            try
            {
                Response response = client.newCall(request).execute(); 
                try
                {
                    Thread.sleep(26000); 
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }
                result = response.body().string(); 
                textView.setText(result); 
            }
            catch(IOException exception)
            {
                exception.printStackTrace();
                return;
            }

            if(textView.getText().equals("CAMERA IS OFF!")) 
            {
                Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
                startActivity(intent);
            }
        }).start();
    }
}