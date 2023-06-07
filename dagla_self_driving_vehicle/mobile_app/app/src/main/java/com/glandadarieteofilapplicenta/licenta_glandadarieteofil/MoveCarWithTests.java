package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.regex.*;

public class MoveCarWithTests extends AppCompatActivity
{
    private static final String MAC_ADDRESS = "00:19:10:08:7F:77";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    private static OutputStream outputStreamToArduino;
    private static InputStream inputStreamFromArduino;

    private ImageButton imageButton_up_and_down;
    private ImageButton imageButton_down_and_up;
    private ImageButton imageButton_left_and_right;
    private ImageButton imageButton_right_and_left;
    private ImageButton imageButton_spin_fast_to_slow;
    private ImageButton imageButton_spin_slow_to_fast;
    private ImageButton imageButton_random_command;
    private ImageButton imageButton_choose_command;

    private static final int TRANSLATION_X_UP_ANIMATION = 500;
    private static final int TRANSLATION_X_DOWN_ANIMATION = -500;
    private static final int DURATION_TIME_ANIMATION = 1000;
    private static final int START_DELAY_ANIMATION = 7000;

    private static String command = "";
    private static String commandForChoosing = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_car_with_tests);

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

        imageButton_up_and_down = (ImageButton) findViewById (R.id.buttonTestOne);
        imageButton_down_and_up = (ImageButton) findViewById (R.id.buttonTestTwo);
        imageButton_left_and_right = (ImageButton) findViewById (R.id.buttonTestThree);
        imageButton_right_and_left = (ImageButton) findViewById (R.id.buttonTestFour);
        imageButton_spin_fast_to_slow = (ImageButton) findViewById (R.id.buttonTestFive);
        imageButton_spin_slow_to_fast = (ImageButton) findViewById (R.id.buttonTestSix);
        imageButton_random_command = (ImageButton) findViewById (R.id.buttonTestSeven);
        imageButton_choose_command = (ImageButton) findViewById (R.id.buttonTestEight);

        createAnimations(imageButton_up_and_down, imageButton_down_and_up, imageButton_left_and_right, imageButton_right_and_left,
                        imageButton_spin_fast_to_slow, imageButton_spin_slow_to_fast, imageButton_random_command, imageButton_choose_command);

        OptionTest optionTest = new OptionTest();
        imageButton_up_and_down.setOnClickListener(optionTest);
        imageButton_down_and_up.setOnClickListener(optionTest);
        imageButton_left_and_right.setOnClickListener(optionTest);
        imageButton_right_and_left.setOnClickListener(optionTest);
        imageButton_spin_fast_to_slow.setOnClickListener(optionTest);
        imageButton_spin_slow_to_fast.setOnClickListener(optionTest);
        imageButton_random_command.setOnClickListener(optionTest);
        imageButton_choose_command.setOnClickListener(optionTest);
    }

    class OptionTest implements View.OnClickListener
    {
        public OptionTest() {} // constructor

        public void onClick(View view)
        {
            switch((String) view.getTag())
            {
                case "test up and down":
                {
                    command = "test up and down";
                    dialog();
                }
                break;

                case "test down and up":
                {
                    command = "test down and up";
                    dialog();
                }
                break;

                case "test left and right":
                {
                    command = "test left and right";
                    dialog();
                }
                break;

                case "test right and left":
                {
                    command = "test right and left";
                    dialog();
                }
                break;

                case "test spin fast to slow":
                {
                    command = "test spin fast to slow";
                    dialog();
                }
                break;

                case "test spin slow to fast":
                {
                    command = "test spin slow to fast";
                    dialog();
                }
                break;

                case "test random command":
                {
                    command = "test random command";
                    dialog();
                }
                break;

                case "test choose command":
                {
                    command = "test choose command";
                    dialogChooser();
                }
                break;

                default:
                {
                    Toast.makeText(MoveCarWithTests.this, "Option does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //name of the text dialog
    public String comprimedText()
    {
        String output = "";
        for(int i = 0; i < command.length(); ++i)
        {
            if(Character.isLowerCase(command.charAt(i)) && i == 0)
            {
                output += Character.toUpperCase(command.charAt(0));
                continue; //skip after we added the character
            }
            output += command.charAt(i);
        }
        return output;
    }

    //create a dialog with the miliseconds or seconds
    public void dialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(comprimedText());

        final EditText editText_input = new EditText(this);
        editText_input.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_input.setHint("In seconds/miliseconds");
        editText_input.setTextColor(ContextCompat.getColor(this, R.color.black));
        editText_input.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        builder.setView(editText_input);

        editText_input.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editText_input.setHint("");
            }
        });


        builder.setPositiveButton("Send", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String inputText = editText_input.getText().toString().trim();
                if(checkIfStartWithNumbersAndRestAreLetters(inputText))
                {
                    if(checkIfContainsExpectedString(inputText))
                    {
                        int delay = getTextAndIgnoreNumbers(inputText);

                        command += "-"+ String.valueOf(delay);

                        try
                        {
                            outputStreamToArduino.write(command.getBytes()); //send command to arduino
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(MoveCarWithTests.this, "Error when typing the input for seconds/miliseconds!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(MoveCarWithTests.this, "Error when typing the input for seconds/miliseconds!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button positiveButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.parseColor("#17202A"));
        positiveButton.setBackgroundColor(Color.WHITE);

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        negativeButton.setTextColor(Color.parseColor("#17202A"));
        negativeButton.setBackgroundColor(Color.WHITE);
    }

    //create a dialog with the miliseconds or seconds
    public void dialogChooser()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(comprimedText());

        final EditText editText_input = new EditText(this);
        editText_input.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_input.setHint("In seconds/miliseconds");
        editText_input.setTextColor(ContextCompat.getColor(this, R.color.black));
        editText_input.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);

        final EditText editText_choose = new EditText(this);
        editText_choose.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_choose.setHint("Command?");
        editText_choose.setTextColor(ContextCompat.getColor(this, R.color.black));
        editText_choose.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);

        LinearLayout linearLayout = new LinearLayout(this); //add a vertical layout so we can add both editText's to it
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText_input);
        linearLayout.addView(editText_choose);
        builder.setView(linearLayout);

        editText_input.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editText_input.setHint("");
            }
        });

        editText_choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editText_choose.setHint("");
            }
        });

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String inputText = editText_input.getText().toString().trim();
                String chooseText = editText_choose.getText().toString().trim();
                if(checkIfStartWithNumbersAndRestAreLetters(inputText))
                {
                    if(checkIfContainsExpectedString(inputText))
                    {
                        if(checkCommandsIfCorrect(chooseText))
                        {
                            int delay = getTextAndIgnoreNumbers(inputText);

                            command += "-"+ String.valueOf(delay) +"-"+ chooseText;

                            try
                            {
                                outputStreamToArduino.write(command.getBytes()); //send command to arduino
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(MoveCarWithTests.this, "Error when typing the input for command!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else
                    {
                        Toast.makeText(MoveCarWithTests.this, "Error when typing the input for seconds/miliseconds!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(MoveCarWithTests.this, "Error when typing the input for seconds/miliseconds!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button positiveButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.parseColor("#17202A"));
        positiveButton.setBackgroundColor(Color.WHITE);

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        negativeButton.setTextColor(Color.parseColor("#17202A"));
        negativeButton.setBackgroundColor(Color.WHITE);
    }

    //method that checks if one of the inputs exist and ignore if it has whitespaces
    public boolean checkCommandsIfCorrect(String text)
    {
        if(Pattern.matches("(\\s*)(forward|backwards|left|right|spin|Forward|Backwards|Left|Right|Spin)(\\s*)", text))
        {
            return true;
        }
        return false;
    }

    //method that checks if the text start with a number and can have whitespace and characters
    public boolean checkIfStartWithNumbersAndRestAreLetters(String text)
    {
        if(Pattern.matches("([1-9][0-9]*)(\\s*)(([a-zA-Z]){11}|([a-zA-Z]){10}|([a-zA-Z]){7}|([a-zA-Z]){6})", text))
        {
            return true;
        }
        return false;
    }

    public boolean checkIfContainsExpectedString(String text)
    {
        if(text.contains("miliseconds") || text.contains("milisecond") || text.contains(("seconds")) || text.contains("second"))
        {
            return true;
        }
        return false;
    }

    public int getTextAndIgnoreNumbers(String text)
    {
        String match_output = "";
        String numberOfSecondsOrMiliseonds = "";
        boolean flag = false;

        for(int i = 0; i < text.length(); ++i)
        {
            if(Character.isDigit(text.charAt(i)) && !flag)
            {
                numberOfSecondsOrMiliseonds += text.charAt(i);
            }

            if((Character.isDigit(text.charAt(i))  ||  text.charAt(i) == ' ') && !flag)
            {
                continue; //skip if character is number or whitespace
            }
            flag = true;
            match_output += text.charAt(i);
        }

        if(match_output.equals("miliseconds"))
        {
            return Integer.parseInt(numberOfSecondsOrMiliseonds);
        }

        //it's seconds and we do the conversion
        return Integer.parseInt(numberOfSecondsOrMiliseonds)*1000;
    }

    @Override
    //override the onBackPressed function so the streams are also closed at the end
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

    public void createAnimations(ImageButton imageButton_up_and_down, ImageButton imageButton_down_and_up, ImageButton imageButton_left_and_right, ImageButton imageButton_right_and_left,
                                 ImageButton imageButton_spin_fast_to_slow, ImageButton imageButton_spin_slow_to_fast, ImageButton imageButton_random_command, ImageButton imageButton_choose_command)
    {
        imageButton_up_and_down.animate().translationX(TRANSLATION_X_UP_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);
        imageButton_down_and_up.animate().translationX(TRANSLATION_X_DOWN_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);

        imageButton_left_and_right.animate().translationX(TRANSLATION_X_UP_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);
        imageButton_right_and_left.animate().translationX(TRANSLATION_X_DOWN_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);

        imageButton_spin_fast_to_slow.animate().translationX(TRANSLATION_X_UP_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);
        imageButton_spin_slow_to_fast.animate().translationX(TRANSLATION_X_DOWN_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);

        imageButton_random_command.animate().translationX(TRANSLATION_X_UP_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);
        imageButton_choose_command.animate().translationX(TRANSLATION_X_DOWN_ANIMATION).setDuration(DURATION_TIME_ANIMATION).setStartDelay(START_DELAY_ANIMATION);
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
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_LONG).show();
        }
        else //if we have devices active
        {
            //linear search to check if it returns the MAC address correctly
            for (BluetoothDevice current : bondedDevices)
            {
                if (current.getAddress().equals(MAC_ADDRESS))
                {
                    device = current; //set MAC address in a global variable
                    return true; //return that we have a connection on the PC
                }
            }
        }
        return false; //return that we do not have a connection on the PC
    }

    public boolean connectToBluetooth() throws IOException
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {}

        socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
        socket.connect();

        try
        {
            inputStreamFromArduino = socket.getInputStream(); //gets the input stream of the socket
            outputStreamToArduino = socket.getOutputStream(); //gets the output stream of the socket
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}