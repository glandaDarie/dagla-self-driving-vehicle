package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.io.OutputStream;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.MotionEvent;
import java.io.IOException;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Set;

public class MoveCarButtons extends AppCompatActivity
{
    private static final int SIZE_BUFFER = 4096;

    private static final String MAC_ADDRESS = "00:19:10:08:7F:77";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    private static OutputStream outputStreamToArduino;
    private static InputStream inputStreamFromArduino;

    private ImageButton button_up; //moves car up while is on hold
    private ImageButton button_down; //moves car down while is on hold
    private ImageButton button_left; //moves car left while is on hold
    private ImageButton button_right; //moves car right while is on hold
    private Button button_middle; //add am effect when pressed

    private TextView textView_pwmValue;
    private Button button_incrementPwm;
    private Button button_decrementPwm;
    private String dir;

    private static String currentPwm = "";
    private static String previousPwm = "";

    private LottieAnimationView lottieJoystick;

    private LottieAnimationView lottieFacebook;
    private LottieAnimationView lottieInstagram;
    private LottieAnimationView lottieTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_car_buttons);

        button_up = (ImageButton) findViewById(R.id.imageButton_goUp);
        button_down = (ImageButton) findViewById(R.id.imageButton_goDown);
        button_left = (ImageButton) findViewById(R.id.imageButton_goLeft);
        button_right = (ImageButton) findViewById(R.id.imageButton_goRight);
        button_middle = (Button) findViewById(R.id.buttonMiddle);
        textView_pwmValue = (TextView) findViewById(R.id.textViewPWM);
        button_incrementPwm = (Button) findViewById(R.id.buttonIncrementPwm);
        button_decrementPwm = (Button) findViewById(R.id.buttonDecrementPwm);

        lottieJoystick = (LottieAnimationView) findViewById(R.id.lottie_joystick);

        lottieFacebook = (LottieAnimationView) findViewById(R.id.lottie_facebook);
        lottieInstagram = (LottieAnimationView) findViewById(R.id.lottie_instagram);
        lottieTwitter = (LottieAnimationView) findViewById(R.id.lottie_twitter);

        setColorForObjects(button_incrementPwm, button_decrementPwm, textView_pwmValue); //set the color from java of the elements

        createAnimationLeft(lottieJoystick); //move the joystick icon to the left side

        if(hasConnection())
        {
            try
            {
                connectToBluetooth();
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Connection to bluetooth failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                //wait a little bit
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e2)
                {
                    e2.printStackTrace();
                }
                //redirect to OptionScreen activity
                Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
                startActivity(intent);
                return;
            }
        }
        else
        {
            //display that we don't have connection to bluetooth device
            Toast.makeText(this, "No connection to bluetooth!", Toast.LENGTH_SHORT).show();
            //wait a little bit
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            //redirect to OptionScreen activity
            Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
            startActivity(intent);
            return;
        }

        button_up.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)  //finger is pressed on button and will send commands
                {
                    dir = "forward";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) // check if we release our finger from the button
                {
                    dir = "stop";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                }
                return false;
            }
        });

        button_down.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) //if the button is actually pressed
                {
                    dir = "backwards";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    dir = "stop";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        button_left.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) //if the button is actually pressed
                {
                    dir = "left";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    dir = "stop";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        button_right.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) //if the button is actually pressed
                {
                    dir = "right";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    dir = "stop";
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //get the input from android studio to arduino to read the key press
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        button_incrementPwm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    int integerCurrentPwm = Integer.parseInt(currentPwm); //convert to integer
                    integerCurrentPwm += 1; //make the increment
                    currentPwm = String.valueOf(integerCurrentPwm); //convert to string
                    outputStreamToArduino.write(currentPwm.getBytes()); //send the input from android studio to arduino to read the pwm value
                    textView_pwmValue.setText(currentPwm); //set the value in the textView

                    if(Integer.parseInt(currentPwm) == 0) //if value from textView is 0
                    {
                        button_decrementPwm.setEnabled(false); //disable decrement button
                    }
                    else if(Integer.parseInt(currentPwm) > 0) //if value from textView is not 0
                    {
                        button_decrementPwm.setEnabled(true); //enable decrement button
                    }

                    if(Integer.parseInt(currentPwm) == 100) //if value from textView is 100
                    {
                        button_incrementPwm.setEnabled(false); //disable increment button
                    }
                    else if(Integer.parseInt(currentPwm) < 100) //if value from textView is not 100
                    {
                        button_incrementPwm.setEnabled(true); //enable increment button
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        button_decrementPwm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    int integerCurrentPwm = Integer.parseInt(currentPwm); //convert to integer
                    integerCurrentPwm -= 1; //make the decrement
                    currentPwm = String.valueOf(integerCurrentPwm); //convert to string
                    outputStreamToArduino.write(currentPwm.getBytes()); //send the input from android studio to arduino to read the pwm value
                    textView_pwmValue.setText(currentPwm); //set the value in the textView

                    if(Integer.parseInt(currentPwm) == 0) //if value from textView is 0
                    {
                        button_decrementPwm.setEnabled(false); //disable decrement button
                    }
                    else if(Integer.parseInt(currentPwm) > 0) //if value from textView is not 0
                    {
                        button_decrementPwm.setEnabled(true); //enable decrement button
                    }

                    if(Integer.parseInt(currentPwm) == 100) //if value from textView is 100
                    {
                        button_incrementPwm.setEnabled(false); //disable increment button
                    }
                    else if(Integer.parseInt(currentPwm) < 100) //if value from textView is not 100
                    {
                        button_incrementPwm.setEnabled(true); //enable increment button
                    }
                }
                catch  (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        Url url = new Url();
        lottieFacebook.setOnClickListener(url);
        lottieInstagram.setOnClickListener(url);
        lottieTwitter.setOnClickListener(url);

        readPWMValueFromArduino();
    }

    //cause android studio does not have setActionCommand and getActionCommand we build our custom setActionCommand and getActionCommand
    class Url implements View.OnClickListener
    {
        public Url() {}

        public void onClick(View v)
        {
            switch((String) v.getTag()) //get the tag after the respective button is pressed
            {
                case "facebook":
                {
                    goToUrl("https://www.facebook.com/darie.glanda");
                }
                break;

                case "instagram":
                {
                    goToUrl("https://www.instagram.com/darie.glanda/?hl=en");
                }
                break;

                case "twitter":
                {
                    goToUrl("https://twitter.com/PeterSt33397883");
                }
                break;

                default:
                {
                    Toast.makeText(MoveCarButtons.this, "Option does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        public void goToUrl(String url)  //function that will redirect you to the coresponding URL
        {
            Uri uri = Uri.parse(url);
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
        }
    }

    public void createAnimationLeft(LottieAnimationView lottie)
    {
        lottie.animate().translationX(-300).setDuration(350).setStartDelay(5000); //move left
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            inputStreamFromArduino.close();
            outputStreamToArduino.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    public void readPWMValueFromArduino()  //receive data from arduino
    {
        byte[] outputByteArray = new byte[SIZE_BUFFER];  //buffer store for the stream
        try
        {
            int start = 0;
            int end = inputStreamFromArduino.read(outputByteArray);
            String pwm = new String(outputByteArray, start, end);

            if(pwm.equals("")) //error handling if we are not connected to bluetooth
            {
                Toast.makeText(this, "Problems with connection to HC-05 (bluetooth)!", Toast.LENGTH_SHORT).show();
                return;
            }

            String [] pwmSplitArray = pwm.split("-");

            if(pwmSplitArray.length == 1)
            {
                currentPwm = pwmSplitArray[0].trim();
            }

            else
            {
                currentPwm = pwmSplitArray[pwmSplitArray.length - 1].trim();

                if(!currentPwm.trim().equals(previousPwm.trim()))
                {
                    currentPwm = pwmSplitArray[pwmSplitArray.length - 1];
                }
                else
                {
                    currentPwm = previousPwm;
                }
            }

            textView_pwmValue.setText(currentPwm);
            previousPwm = currentPwm;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setColorForObjects(Button button_incrementPwm, Button button_decrementPwm, TextView textView_pwmValue)
    {
        button_incrementPwm.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.m3_ref_palette_neutral40));
        button_decrementPwm.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.m3_ref_palette_neutral40));
        textView_pwmValue.setTextColor(Color.parseColor("#6699CC"));
    }

    public boolean hasConnection()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) //checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) //checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) //check the permissions
            {
                return false; // return false if the permissions are not okay
            }
            else
            {
                startActivityForResult(enableAdapter, 0);
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices(); //create a set to check if we have a MAC address

        if (bondedDevices.isEmpty()) //if we don't have any bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first!", Toast.LENGTH_LONG).show();
        }
        else //if we have devices active
        {
            //linear search to check if it returns the MAC address correctly
            for (BluetoothDevice current : bondedDevices)
            {
                if (current.getAddress().equals(MAC_ADDRESS)) //find the MAC address using linear search
                {
                    device = current;
                    return true; //if we found it we return true
                }
            }
        }
        return false; // return false if we did not find the MAC address
    }

    public boolean connectToBluetooth() throws IOException
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {}

        socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
        socket.connect(); //connect to bluetooth device

        try
        {
            inputStreamFromArduino = socket.getInputStream(); //gets the input stream of the socket
            outputStreamToArduino = socket.getOutputStream(); //gets the output stream of the socket
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}