package parametrice.cooker;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by benji on 5/22/2017.
 */
public class Temptime extends Fragment implements View.OnClickListener{
    Button stop;
    Main mn;
    public String timee= null,tempee;
    TextView time,temp;
    Stop stope;
    public Temptime(){}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.temperature, container, false);
        stop=(Button)view.findViewById(R.id.stop);
        time=(TextView)view.findViewById(R.id.time);
        temp=(TextView)view.findViewById(R.id.temp);
        stop.setOnClickListener(this);
        timee=getArguments().getString("time");
        tempee=getArguments().getString("temp");
        time.setText(timee);temp.setText(tempee);
        return view;
    }
    
    public void onClick(View v){
     if(v.getId()==R.id.stop){
   try{ mn.mmOutputStream.write("0".getBytes()); }catch(Exception e){}
     }}
    public interface Stop{
      void endi();
    }
    public void onAttach(Activity act){
     super.onAttach(act);
        stope=(Stop)act;
    }

}
