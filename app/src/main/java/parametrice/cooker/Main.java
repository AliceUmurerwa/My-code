package parametrice.cooker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;


public class Main extends AppCompatActivity implements AdapterView.OnItemClickListener,Serializable,Temptime.Stop{
    public FragmentTransaction transaction;
    public FragmentManager manager;
    private DrawerLayout drawerLayout;
    private ListView navilist;
    private ActionBarDrawerToggle drawerToggle;
    //Variables for bluetooth
    BluetoothSocket mmSocket= null;
    BluetoothAdapter mBluetoothAdapter=null;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    volatile boolean stopWorker;

    //reception
    byte ch;
    int bytes;byte[] buffer = new byte[1024];
    String input;

    Handler cook;final int handlerState = 0;        				 //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Links m=new Links(this);
        drawerLayout=(DrawerLayout)findViewById(R.id.body);
        navilist=(ListView)findViewById(R.id.navlist);
        navilist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        navilist.setSelector(new ColorDrawable(Color.parseColor("#098181")));
        navilist.setAdapter(m);
        navilist.setOnItemClickListener(this);
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.opendrawer,R.string.closedrawer);
        drawerLayout.setDrawerListener(drawerToggle);
        ActionBar action=getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#098181")));
        action.setDisplayOptions(action.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams layoutParams=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin=2;
        action.setDisplayShowHomeEnabled(true);
        action.setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, getIntent().getStringExtra("data"), Toast.LENGTH_LONG).show();
        manager=getSupportFragmentManager();
        transaction= manager.beginTransaction();
        transaction.replace(R.id.content, new Home());
        transaction.commit();
        getSupportActionBar().setTitle("Wellcome");
        //handling bluetooth connection

        openBT();
        cook=new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();							//get length of data received

                        if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                        {
                            //String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            //String sensor1 = recDataString.substring(6, 10);            //same again...
                            //String sensor2 = recDataString.substring(11, 15);
                            //String sensor3 = rexxcDataString.substring(16, 20);
                            Temptime ctl=new Temptime();
                            Bundle b=new Bundle();
                            b.putString("time", recDataString.substring(1, recDataString.indexOf("&")));
                            b.putString("temp",  recDataString.substring(recDataString.indexOf("&")+1,endOfLineIndex ));
                            ctl.setArguments(b);
                            transaction= manager.beginTransaction();
                            transaction.replace(R.id.content, ctl);
                            transaction.commit();
                            Toast.makeText(getBaseContext(),"Alarm msg : "+dataInPrint, Toast.LENGTH_LONG).show();
                            notification();

                        }
                        recDataString.delete(0, recDataString.length()); 					//clear all string data

                    }
                }
            }
        };
    }

    void openBT() {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // StandardSerial PortService ID
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            mmDevice =mBluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("data"));
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            Toast.makeText(this, "Bluetooth Opened", Toast.LENGTH_LONG).show();
            beginListenForData();
        }catch (IOException e){
            try{stopWorker = true; finish();}catch (Exception ex){}
            Toast.makeText(this, "Error!!!", Toast.LENGTH_LONG).show();
        }

    }
    void beginListenForData() {

        stopWorker = false;
        workerThread = new Thread(new Runnable() {public void run() {

            while (true) {
                try {
                    bytes=0;
                    while ((ch=(byte)mmInputStream.read())!='\r') {
                        buffer[bytes++]=ch;
                    }
                    if(bytes > 0){
                        input=new String(buffer,"UTF-8");

                        //Toast.makeText(getBaseContext(),"Alarm msg :  "+input, Toast.LENGTH_LONG).show();
                        //make notification sound
                        // Send the obtained bytes to the UI Activity via handler
                        cook.obtainMessage(handlerState, bytes, -1, input).sendToTarget();

                        notification();

                    }


                    // runOnUiThread(new Runnable(){
                    //     @Override
                    //     public void run() {

                    //Temptime ctl=new Temptime();
                    //Bundle b=new Bundle();
                    //b.putString("temp","100");
                    //b.putString("time","156");
                    //ctl.setArguments(b);
                    //transaction= manager.beginTransaction();
                    //transaction.replace(R.id.content, ctl);
                    //transaction.commit();

                    //String []a= data.split("~");
                    //Toast.makeText(getBaseContext(),"Alarm msg :  "+data1, Toast.LENGTH_LONG).show();
                    //make notification sound

                    //notification();
                    // try{ Thread.sleep(1000);}catch(Exception e){}
                    //}});

                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), "Transimission is lost", Toast.LENGTH_LONG).show();
                    finish();
                    stopWorker = true;}}
        }
        });
        workerThread.start();
    }
    public void onConfigurationChanged(Configuration newconf){
        super.onConfigurationChanged(newconf);
        drawerToggle.onConfigurationChanged(newconf);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatementif (id == R.id.action_settings) {

        if(id==android.R.id.home){
            if(drawerLayout.isDrawerOpen(navilist)){
                drawerLayout.closeDrawer(navilist);
            }else {
                drawerLayout.openDrawer(navilist);
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void onItemClick(AdapterView<?> parent,View view,int position, long id){
        loadLinkSelection(position);
        try{
            Thread.sleep(500);
        }catch (Exception e){}
        drawerLayout.closeDrawer(navilist);

    }
    private void loadLinkSelection(int i){
        Links l=new Links(this);
        navilist.setItemChecked(i, true);
        getSupportActionBar().setTitle(l.menustring[i]);
        if(i==0){
            transaction= manager.beginTransaction();
            transaction.replace(R.id.content, new Home());
            transaction.commit();

        }else{
            Control ctl=new Control();
            Bundle b=new Bundle();
            b.putString("lmp", l.menustring[i]);
            b.putSerializable("connect", this);
            ctl.setArguments(b);
            transaction= manager.beginTransaction();
            transaction.replace(R.id.content, ctl);
            transaction.commit();}

    }

    void notification(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }//sound notification

    public void endi(){
        try{ mmOutputStream.write("0".getBytes());
            Toast.makeText(getBaseContext(),"Stopping...", Toast.LENGTH_LONG).show(); }catch(Exception e){Toast.makeText(getBaseContext(),"Network Error!!", Toast.LENGTH_LONG).show();}
    }
}

class Links extends BaseAdapter {
    private Context context;
    String []menustring;
    int []images={R.drawable.home,R.drawable.rice, R.drawable.ibirayijumba,R.drawable.meat,R.drawable.tea,R.drawable.custom};
    //home,lock,engine,horn,lamp,power button
    public Links(Context context){

        menustring=context.getResources().getStringArray(R.array.navlinks);
        this.context=context;
    }
    public int getCount(){return menustring.length;}
    public Object getItem(int position){return menustring[position];}
    public long getItemId(int position){return position;}
    public View getView(int position,View convertView,ViewGroup parent){
        View root=null;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            root=inflater.inflate(R.layout.menu,parent,false);
        }else{root=convertView;}
        TextView txt=(TextView)root.findViewById(R.id.menutxt);
        ImageView Img=(ImageView)root.findViewById(R.id.menuImg);
        txt.setText(menustring[position]);
        Img.setImageResource(images[position]);
        Img.setPadding(12,7,20,7);

        return root;
    }
}
