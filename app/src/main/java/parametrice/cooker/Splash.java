package parametrice.cooker;

/**
 * Created by benji on 5/19/2017.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by benji on 7/29/2016.
 */
public class Splash extends AppCompatActivity {
    ListView devicelist;
    private static int SPLASH_TIME_OUT = 7000;
    public static String EXTRA_ADDRESS = "data";
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter myBluetooth = null;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView splashImageView = (ImageView) findViewById(R.id.logo);
        splashImageView.setImageResource(R.drawable.stove);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                myBluetooth = BluetoothAdapter.getDefaultAdapter();
                if(myBluetooth == null)
                {
                    //Show a mensag. that the device has no bluetooth adapter
                    Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                    //finish apk
                    finish();
                }else if(!myBluetooth.isEnabled())
                {
                    //Ask to the user turn the bluetooth on
                    Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnBTon,1);
                    try{Thread.sleep(500);}catch (Exception e){}
                }
                setContentView(R.layout.pairedlist);
                devicelist = (ListView)findViewById(R.id.pairedlist);
                pairedDevicesList();
            }
        },SPLASH_TIME_OUT);

    }
    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setSelector(new ColorDrawable(Color.parseColor("#0b9fc1")));
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(Splash.this, Main.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at main (class) Activity
            startActivity(i);
        }
    };


}