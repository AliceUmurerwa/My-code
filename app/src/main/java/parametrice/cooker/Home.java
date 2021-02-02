package parametrice.cooker;

/**
 * Created by benji on 5/19/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class Home extends Fragment {


    public Home() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View hm= inflater.inflate(R.layout.home, container, false);

        return hm;}
}
