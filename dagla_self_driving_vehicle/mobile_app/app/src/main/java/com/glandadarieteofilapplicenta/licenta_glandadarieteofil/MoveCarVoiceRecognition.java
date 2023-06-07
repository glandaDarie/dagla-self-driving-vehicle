package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class MoveCarVoiceRecognition extends AppCompatActivity
{
    private static final String MAC_ADDRESS = "00:19:10:08:7F:77";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    private static OutputStream outputStreamToArduino;

    private TextView translatedText;
    private static String dir = "";

    private CardView cardViewEnglish;
    private LinearLayout linearLayoutEnglish;
    private ImageView imageViewEnglish;
    private TextView textViewEnglish;

    private CardView cardViewGerman;
    private LinearLayout linearLayoutGerman;
    private ImageView imageViewGerman;
    private TextView textViewGerman;

    private CardView cardViewFrench;
    private LinearLayout linearLayoutFrench;
    private ImageView imageViewFrench;
    private TextView textViewFrench;

    private CardView cardViewRomanian;
    private LinearLayout linearLayoutRomanian;
    private ImageView imageViewRomanian;
    private TextView textViewRomanian;

    private CardView cardViewSerbian;
    private LinearLayout linearLayoutSerbian;
    private ImageView imageViewSerbian;
    private TextView textViewSerbian;

    private static String languageSelected = "";

    private ImageView imageVoiceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_car_voice_recognition);

        translatedText = (TextView) findViewById(R.id.textViewTranslated);

        imageVoiceRecognition = (ImageView) findViewById(R.id.buttonSpeak);
        makeVoiceRecognitionInvisible();

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

        initEnglish();
        initGerman();
        initFrench();
        initRomanian();
        initSerbian();

        Language language = new Language(); //class that implements the interface onClickListener
        //set an action for each listener
        cardViewEnglish.setOnClickListener(language);
        cardViewGerman.setOnClickListener(language);
        cardViewFrench.setOnClickListener(language);
        cardViewRomanian.setOnClickListener(language);
        cardViewSerbian.setOnClickListener(language);
    }

    class Language implements View.OnClickListener
    {
        public void onClick(View v)
        {
            switch((String) v.getTag()) //get the tag after the respective button is pressed
            {
                case "english": //check if the language choosen is english
                {
                    //disable all buttons so we create only the voice recognition image
                    disableEnglish();
                    disableGerman();
                    disableFrench();
                    disableRomanian();
                    disableSerbian();
                    makeVoiceRecognitionVisible();
                    languageSelected = "ENGLISH"; //set language to english
                }
                break;

                case "german": //check if the language choosen is german
                {
                    //disable all buttons so we create only the voice recognition image
                    disableEnglish();
                    disableGerman();
                    disableFrench();
                    disableRomanian();
                    disableSerbian();
                    makeVoiceRecognitionVisible();
                    languageSelected = "GERMAN"; //set language to german
                }
                break;

                case "french": //check if the language choosen is french
                {
                    //disable all buttons so we create only the voice recognition image
                    disableEnglish();
                    disableGerman();
                    disableFrench();
                    disableRomanian();
                    disableSerbian();
                    makeVoiceRecognitionVisible();
                    languageSelected = "FRENCH"; //set language to french
                }
                break;

                case "romanian": //check if the language choosen is romanian
                {
                    //disable all buttons so we create only the voice recognition image
                    disableEnglish();
                    disableGerman();
                    disableFrench();
                    disableRomanian();
                    disableSerbian();
                    makeVoiceRecognitionVisible();
                    languageSelected = "ROMANIAN"; //set language to romanian
                }
                break;

                case "serbian": //check if the language choosen is serbian
                {
                    //disable all buttons so we create only the voice recognition image
                    disableEnglish();
                    disableGerman();
                    disableFrench();
                    disableRomanian();
                    disableSerbian();
                    makeVoiceRecognitionVisible();
                    languageSelected = "SERBIAN"; //set language to serbian
                }
                break;

                default:
                {
                    Toast.makeText(MoveCarVoiceRecognition.this, "Does not exist", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //make button for voice recognition invisible
    public void makeVoiceRecognitionInvisible()
    {
        imageVoiceRecognition.setVisibility(View.GONE);
    }

    //make button for voice recognition visible
    public void makeVoiceRecognitionVisible()
    {
        imageVoiceRecognition.setVisibility(View.VISIBLE);
    }

    //properties for english flag
    public void initEnglish()
    {
        cardViewEnglish = (CardView) findViewById(R.id.cardViewEnglish);
        linearLayoutEnglish = (LinearLayout) findViewById(R.id.linearLayoutEnglish);
        imageViewEnglish = (ImageView) findViewById(R.id.imageViewEnglish);
        textViewEnglish = (TextView) findViewById(R.id.textViewEnglish);
    }

    //properties for german flag
    public void initGerman()
    {
        cardViewGerman = (CardView) findViewById(R.id.cardViewGerman);
        linearLayoutGerman = (LinearLayout) findViewById(R.id.linearLayoutGerman);
        imageViewGerman = (ImageView) findViewById(R.id.imageViewGerman);
        textViewGerman = (TextView) findViewById(R.id.textViewGerman);
    }

    //properties for french flag
    public void initFrench()
    {
        cardViewFrench = (CardView) findViewById(R.id.cardViewFrench);
        linearLayoutFrench = (LinearLayout) findViewById(R.id.linearLayoutFrench);
        imageViewFrench = (ImageView) findViewById(R.id.imageViewFrench);
        textViewFrench = (TextView) findViewById(R.id.textViewFrench);
    }

    //properties for romanian flag
    public void initRomanian()
    {
        cardViewRomanian = (CardView) findViewById(R.id.cardViewRomanian);
        linearLayoutRomanian = (LinearLayout) findViewById(R.id.linearLayoutRomanian);
        imageViewRomanian = (ImageView) findViewById(R.id.imageViewRomanian);
        textViewRomanian = (TextView) findViewById(R.id.textViewRomanian);
    }

    //properties for serbian flag
    public void initSerbian()
    {
        cardViewSerbian = (CardView) findViewById(R.id.cardViewSerbian);
        linearLayoutSerbian = (LinearLayout) findViewById(R.id.linearLayoutSerbian);
        imageViewSerbian = (ImageView) findViewById(R.id.imageViewSerbian);
        textViewSerbian = (TextView) findViewById(R.id.textViewSerbian);
    }


    //disable all english properties
    public void disableEnglish()
    {
        cardViewEnglish.setVisibility(View.GONE);
        linearLayoutEnglish.setVisibility(View.GONE);
        imageViewEnglish.setVisibility(View.GONE);
        textViewEnglish.setVisibility(View.GONE);
    }

    //disable all german properties
    public void disableGerman()
    {
        cardViewGerman.setVisibility(View.GONE);
        linearLayoutGerman.setVisibility(View.GONE);
        imageViewGerman.setVisibility(View.GONE);
        textViewGerman.setVisibility(View.GONE);
    }

    //disable all french properties
    public void disableFrench()
    {
        cardViewFrench.setVisibility(View.GONE);
        linearLayoutFrench.setVisibility(View.GONE);
        imageViewFrench.setVisibility(View.GONE);
        textViewFrench.setVisibility(View.GONE);
    }

    //disable all romanian properties
    public void disableRomanian()
    {
        cardViewRomanian.setVisibility(View.GONE);
        linearLayoutRomanian.setVisibility(View.GONE);
        imageViewRomanian.setVisibility(View.GONE);
        textViewRomanian.setVisibility(View.GONE);
    }

    //disable all serbian properties
    public void disableSerbian()
    {
        cardViewSerbian.setVisibility(View.GONE);
        linearLayoutSerbian.setVisibility(View.GONE);
        imageViewSerbian.setVisibility(View.GONE);
        textViewSerbian.setVisibility(View.GONE);
    }

    public void getSpeechInput(View view)
    {
        //initialize speech recognizer and set the language for it
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if(languageSelected.equals("ENGLISH")) //check if language is english
        {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()); //set translation for english language
        }

        else if(languageSelected.equals("GERMAN")) //check if language is german
        {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de");
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"de"}); //set translation for german language
        }

        else if(languageSelected.equals("FRENCH")) //check if language is french
        {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr");
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"fr"}); //set translation for french language
        }

        else if(languageSelected.equals("ROMANIAN")) //check if language is romanian
        {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro");
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"ro"}); //set translation for romanian language
        }

        else if(languageSelected.equals("SERBIAN")) //check if language is serbian
        {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sr");
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"sr"}); //set translation for serbian language
        }

        else
        {
            Toast.makeText(this, "The selected language does not exist!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, 1);
        }
        else
        {
            Toast.makeText(this, "Your Device Does Not Support Speech Input!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) //check if there is a voice command demand
        {
            if (resultCode == RESULT_OK && data != null) //check if requestCode is okay and if a voice command is sent
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                dir = result.get(0); //get the text from the voice
                translatedText.setText(dir);
                dir += " voice"; //add voice so we can use the custom pwm in arduino

                if(outputStreamToArduino == null) //check if stream is not initialized
                {
                    try
                    {
                        //initialize stream and send the voice commands to Arduino
                        outputStreamToArduino = socket.getOutputStream();
                        outputStreamToArduino.write(dir.getBytes());
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(this, "Can't get message!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else if(outputStreamToArduino != null) //stream is initialized
                {
                    try
                    {
                        outputStreamToArduino.write(dir.getBytes()); //send the commands to Arduino
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(this, "Can't get message!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() //when inbuild back button is pressed, we close the stream each time
    {
        try
        {
            outputStreamToArduino.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    public boolean hasConnection()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                return false;
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
                if (current.getAddress().equals(MAC_ADDRESS))
                {
                    device = current;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean connectToBluetooth() throws IOException
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {}

        socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
        socket.connect();

        try
        {
            outputStreamToArduino = socket.getOutputStream(); //gets the output stream of the socket
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}



