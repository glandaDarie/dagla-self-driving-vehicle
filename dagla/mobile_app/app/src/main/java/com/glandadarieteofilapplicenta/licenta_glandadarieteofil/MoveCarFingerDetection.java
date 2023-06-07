package com.glandadarieteofilapplicenta.licenta_glandadarieteofil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MoveCarFingerDetection extends AppCompatActivity
{
    private static final String MAC_ADDRESS = "00:19:10:08:7F:77";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); 

    private ImageView imageViewDisplayHandGest;

    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    private static OutputStream outputStreamToArduino;

    private DatabaseReference root;
    private static ArrayList <String> holdPairs;
    private static ArrayList <Integer> photos;
    private static ArrayList <String> listValueFromHashMap;

    private static int v = 0; 
    private static boolean bul = true; 
    private static int countHappened = 0;
    private static boolean happened = false;

    private final String tableName = "Connection";

    private Runnable runnable;

    private String [] allCases =
    {
            "False False False False False",

            "True False False False False", 
            "False True False False False", 
            "False False True False False", 
            "False False False True False", 
            "False False False False True", 

            "True True False False False", 
            "True False True False False", 
            "True False False True False", 
            "True False False False True", 
            "False True True False False", 
            "False True False True False", 
            "False True False False True", 
            "False False True True False", 
            "False False True False True", 
            "False False False True True", 

            "True True True False False", 
            "True True False True False", 
            "True True False False True", 
            "True False True True False", 
            "True False True False True", 
            "True False False True True", 
            "False True True True False", 
            "False True True False True", 
            "False True False True True", 
            "False False True True True", 

            "True True True True False", 
            "True True True False True", 
            "True True False True True", 
            "True False True True True", 
            "False True True True True", 

            "True True True True True", 
    };

    public static int[] photoIds = new int [] {
            R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,
            R.drawable.pic5,R.drawable.pic6,R.drawable.pic7,R.drawable.pic8,
            R.drawable.pic9,R.drawable.pic10,R.drawable.pic11,R.drawable.pic12,
            R.drawable.pic13,R.drawable.pic14,R.drawable.pic15,R.drawable.pic16,
            R.drawable.pic17,R.drawable.pic18,R.drawable.pic19,R.drawable.pic20,
            R.drawable.pic21,R.drawable.pic22,R.drawable.pic23,R.drawable.pic24,
            R.drawable.pic25,R.drawable.pic26,R.drawable.pic27,R.drawable.pic28,
            R.drawable.pic29,R.drawable.pic30,R.drawable.pic31,R.drawable.pic32,
            R.drawable.pic33,R.drawable.pic34,R.drawable.pic35,R.drawable.pic36,
            R.drawable.pic37,R.drawable.pic38,R.drawable.pic39,R.drawable.pic40,
            R.drawable.pic41,R.drawable.pic42,R.drawable.pic43,R.drawable.pic44,
            R.drawable.pic45,R.drawable.pic46,R.drawable.pic47,R.drawable.pic48,
            R.drawable.pic49,R.drawable.pic50,R.drawable.pic51,R.drawable.pic52,
            R.drawable.pic53,R.drawable.pic54,R.drawable.pic55,R.drawable.pic56,
            R.drawable.pic57,R.drawable.pic58,R.drawable.pic59,R.drawable.pic60,
            R.drawable.pic61,R.drawable.pic62,R.drawable.pic63,R.drawable.pic64,
    };


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_car_finger_detection);

        imageViewDisplayHandGest = (ImageView) findViewById(R.id.imageViewDisplayHandGesture);

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
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e2)
                {
                    e2.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
                startActivity(intent);
                return;
            }
        }
        else
        {
            Toast.makeText(this, "No connection to bluetooth!", Toast.LENGTH_SHORT).show();
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), OptionScreen.class);
            startActivity(intent);
            return;
        }

        final Handler handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if(countHappened >= 2)
                {
                   if (v > photos.size() - 1) 
                   {
                       bul = false;
                       try
                       {
                           imageViewDisplayHandGest.setImageResource(R.drawable.ic_launcher_foreground); 
                           outputStreamToArduino.write("stop".getBytes()); 
                       }
                       catch (IOException e)
                       {
                           e.printStackTrace();
                       }
                   }

                   if (bul) 
                   {
                       imageViewDisplayHandGest.setImageResource(photos.get(v)); 

                       String keyFromHandsUp = "";

                       if (!getKeyFromFingers(listValueFromHashMap.get(v)).equals("NONE")) 
                       {
                           keyFromHandsUp = getKeyFromFingers(listValueFromHashMap.get(v)); 
                       }
                       else
                       {
                           Toast.makeText(MoveCarFingerDetection.this, "No connection!", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       try
                       {
                           outputStreamToArduino.write(keyFromHandsUp.getBytes());
                       }
                       catch (IOException e)
                       {
                           e.printStackTrace();
                       }
                       v += 1;
                   }
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 4000); 

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(tableName);

        database.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (countHappened >= 1) 
                {
                    countHappened += 1;
                    int count = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (count != (snapshot.getChildrenCount() - 1)) 
                        {
                            count += 1; 
                            continue;
                        }

                        holdPairs = new ArrayList<String>(); 
                        listValueFromHashMap = new ArrayList<String>(); 
                        DatabaseReference insideHash = FirebaseDatabase.getInstance().getReference().child(tableName).child(dataSnapshot.getKey()); 

                        if (snapshot.getChildrenCount() > 5) 
                        {
                            deleteFromFirebase(database, tableName, snapshot);
                        }

                        insideHash.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotInside) 
                            {
                                for (DataSnapshot snap : snapshotInside.getChildren())
                                {
                                    listValueFromHashMap.add(snap.getValue().toString());
                                    holdPairs.add(snap.getKey() + " - " + snap.getValue());
                                }
                                photos = addPhotosToList(allCases, holdPairs); 
                                v = 0; 
                                bul = true; 
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError errorInside)
                            {
                                Toast.makeText(MoveCarFingerDetection.this, "Database error!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }
                }
                if(!happened)
                {
                    countHappened += 1; 
                    happened = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(MoveCarFingerDetection.this, "Database error!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void deleteFromFirebase(DatabaseReference database, String tableName, DataSnapshot snapshot)
    {
        int count = 0;
        for(DataSnapshot dataSnapshot : snapshot.getChildren())
        {
            if(count == (snapshot.getChildrenCount() - 1))
            {
                return;
            }
            dataSnapshot.getRef().removeValue(); 
            count += 1;
        }
    }

    @Override
    public void onBackPressed() 
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

    public String getKeyFromFingers(String value)
    {
        int count = 0;

        String [] splitter = value.split(" "); 

        for(int i = 0; i < splitter.length; ++i) 
        {
            if(splitter[i].equals("True"))
            {
                count += 1; 
            }
        }

        if(count == 0) { return "STOP"; }
        else if(count == 1) { return "UP"; }
        else if(count == 2) { return "DOWN"; }
        else if(count == 3) { return "LEFT"; }
        else if(count == 4) { return "RIGHT"; }
        else if(count == 5) { return "SPIN"; }
        return "NONE"; 
    }

    public ArrayList <Integer> addPhotosToList(String [] allCases, ArrayList <String> holdPairs)
    {
        ArrayList <Integer> photos = new ArrayList <Integer>();
        boolean flag;

        for(int i = 0; i < holdPairs.size(); ++i)
        {
            flag = false; 

            String key = holdPairs.get(i).split(" - ")[0];
            String value = holdPairs.get(i).split(" - ")[1];

            for(int j = 0; j < allCases.length && flag == false; ++j)
            {
                if(allCases[j].equals(value))
                {
                    flag = true; 
                    String onlyKey = key.split(" ")[1]; 
                    if(onlyKey.equals("leftHand"))
                    {
                        photos.add(photoIds[j]);
                    }
                    else if(onlyKey.equals("rightHand"))
                    {
                        photos.add(photoIds[j]);
                    }
                }
            }
        }
        return photos;
    }

    public boolean hasConnection()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) 
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) 
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

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices(); 
        if (bondedDevices.isEmpty()) 
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first!", Toast.LENGTH_LONG).show();
        }
        else 
        {
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
            outputStreamToArduino = socket.getOutputStream();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
