package uk.ac.uclan.nkasenides.currencyconverter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Created by Nicos
 *
 * The about fragment display information about the application.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }//end AboutFragment()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.hideKeyboard(getContext());
        return inflater.inflate(R.layout.fragment_about, container, false);
    }//end onCreateView()

}//end class AboutFragment
