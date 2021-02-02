package parametrice.cooker;

/**
 * Created by benji on 5/19/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;


/**
 * Created by benji on 7/29/2016.
 */
public class Control extends Fragment implements View.OnClickListener {
    public String device= null;
    Main mn;
    private Button heat;
    private EditText mss;
    private TextView desc;
    private  static double index;//temperature index constant
    ImageView foodimg;

    public Control(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.control, container, false);
        foodimg=(ImageView)view.findViewById(R.id.food);
        heat=(Button)view.findViewById(R.id.next);
        mss=(EditText)view.findViewById(R.id.mass);
        desc=(TextView)view.findViewById(R.id.instr);
        mn= (Main) getArguments().getSerializable("connect");
        device=getArguments().getString("lmp");
        msg(device);
        imf();
        heat.setOnClickListener(this);
        return view;
    }
    public void onClick(View v){
        if(!mss.getText().toString().equals(null)){
        Double massc=Double.parseDouble(mss.getText().toString());

        switch(v.getId()){//horn
            case R.id.next: try{
                if(device.equals("Rice")){
                        mn.mmOutputStream.write(time(massc).getBytes());
                } else if(device.equals("Potato")){
                        mn.mmOutputStream.write(time(massc).getBytes());
                }
                else if(device.equals("Meat")){

                        mn.mmOutputStream.write(time(massc).getBytes());
                    } else if(device.equals("Tea")){

                        mn.mmOutputStream.write(time(massc).getBytes());
                   }
                else if(device.equals("Custom")) {
                        mn.mmOutputStream.write(time(massc).getBytes());
                    }

            }catch(IOException ef){}
        }}else{msg("You should enter a number");}
    }


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(this.getActivity(),s, Toast.LENGTH_LONG).show();
    }
    void imf(){
        if(device.equals("Rice")){
            foodimg.setImageResource(R.drawable.friedrice);
            desc.setText("To cook rice, you have to follow these principles, for 1 kg of rice you should have to add 3liter of water for efficient cook.");
            index=3;
        }else if(device.equals("Potato")){
            foodimg.setImageResource(R.drawable.ikirayi);
            desc.setText("To cook Sweet potato,Potato and banana it requires put a third of water.");
            index=1;
        }else if(device.equals("Meat")){
            foodimg.setImageResource(R.drawable.meat);
            desc.setText("To cook Meat like cow meat it requires, high temperature and more time,you may add vinegar or lemon juice before cooking to tender them, then it will be cooked quickly. \n\n for 1kg add 0.4liter of water and press next to cook.");
            index=5;
        }else if(device.equals("Tea")){
            foodimg.setImageResource(R.drawable.tea);
            desc.setText("To cook Tea it require temperature of 100°C celsius when water is boiling you are asked to put tea, coffee, any drink that need to be boiled! \n \n \n you should add amount of woter in kg because 1kg of water equal 1 liter. click next to heat.");
            index=1;
        }else if(device.equals("Custom")){
            foodimg.setImageResource(R.drawable.cust);
            desc.setText("To cook liquids you have to know that basing on our stove of 200W ,1kg of water boils when 12minuts elapsed!");
            index=1;
        }}
   private  String time(double mass){double time;time=mass*index; return ""+time;}
}
