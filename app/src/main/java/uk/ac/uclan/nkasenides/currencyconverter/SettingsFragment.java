package uk.ac.uclan.nkasenides.currencyconverter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 * Created by Nicos
 *
 * The Settings fragment allows the user to customize the application's settings.
 */
public class SettingsFragment extends Fragment {

    Switch autoUpdateSwitch;
    Switch darkModeSwitch;
    Spinner baseCurrencySpinner;
    Spinner dpSpinner;

    public SettingsFragment() {
        // Required empty public constructor
    }//end SettingsFragment()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings,container, false);
        MainActivity.hideKeyboard(getContext());

        autoUpdateSwitch = (Switch) view.findViewById(R.id.updatesSwitch);
        darkModeSwitch = (Switch) view.findViewById(R.id.darkModeSwitch);
        dpSpinner = (Spinner) view.findViewById(R.id.dpSpinner);

        //Initialize the base-currency selection spinner:
        baseCurrencySpinner = (Spinner) view.findViewById(R.id.baseCurrencySpinner);
        CustomSpinnerAdapter customAdapter= new CustomSpinnerAdapter(this.getActivity(),ConvertFragment.currencyFlags,ConvertFragment.currencyNames);
        baseCurrencySpinner.setAdapter(customAdapter);

        //Initialize UI
        autoUpdateSwitch.setChecked(MainActivity.preferences.getBoolean("pref_AutoUpdate", true));
        darkModeSwitch.setChecked(MainActivity.preferences.getBoolean("pref_DarkMode", false));
        baseCurrencySpinner.setSelection(MainActivity.preferences.getInt("pref_BaseCurrency", 0));
        dpSpinner.setSelection(MainActivity.preferences.getInt("pref_DecimalPlaces", 2) - 1 );

        //Set the auto-update preference:
        autoUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putBoolean("pref_AutoUpdate", isChecked);
                editor.apply();
            }
        });

        //Set the DarkMode preference:
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putBoolean("pref_DarkMode", isChecked);
                editor.apply();

                if (MainActivity.preferences.getBoolean("pref_DarkMode", false)) MainActivity.setDarkTheme(getActivity().findViewById(R.id.drawer_layout));
                else MainActivity.setLightTheme(getActivity().findViewById(R.id.drawer_layout));

                android.support.v4.app.Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                android.support.v4.app.FragmentTransaction settingsFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                settingsFragmentTransaction.remove(currentFragment).commit();

                //Restart activity to use new theme:
                Intent intent = new Intent();
                intent.setClass(getActivity(), getActivity().getClass());
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        //Set the Base Currency preference:
        baseCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putInt("pref_BaseCurrency", position);
                editor.apply();
            }//end onItemSelected()

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }

        });

        //Set the DP preferences:
        dpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences.Editor editor = MainActivity.preferences.edit();
                editor.putInt("pref_DecimalPlaces", position + 1);
                editor.apply();
            }//end onItemSelected()

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        return view;
    }//end onCreateView()

}//end class SettingsFragment
