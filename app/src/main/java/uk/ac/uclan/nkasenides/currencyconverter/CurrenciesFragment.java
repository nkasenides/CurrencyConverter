package uk.ac.uclan.nkasenides.currencyconverter;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import uk.ac.uclan.nkasenides.currencyconverter.DBUtils.DatabaseOpenHelper;


/**
 * A simple {@link Fragment} subclass.
 * Created by Nicos
 *
 * The currencies fragment displays a list of all the currencies and allows the user to add them
 * as a favorite or convert from them.
 */
public class CurrenciesFragment extends Fragment {

    private int[] currencyListFlags = new int[MainActivity.NUM_OF_CURRENCIES];
    private String[] currencyListNames = new String[MainActivity.NUM_OF_CURRENCIES];
    private int[] currencyListIDs = new int[MainActivity.NUM_OF_CURRENCIES];

    public CurrenciesFragment() {
        // Required empty public constructor
    }//end CurrenciesFragment()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currencies,container, false);
        MainActivity.hideKeyboard(getContext());

        //Update the list items:
        updateCurrenciesListItems(view);
        ListView currenciesListview = (ListView) view.findViewById(R.id.currencies_listview);
        CurrenciesListAdapter customAdapter= new CurrenciesListAdapter(getActivity(), this.getActivity(),currencyListFlags,currencyListNames);
        currenciesListview.setAdapter(customAdapter);

        //On currency item click:
        currenciesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ConvertFragment.selectedCurrency = currencyListIDs[pos];
                ConvertFragment convertFragment = new ConvertFragment();
                android.support.v4.app.FragmentTransaction convertFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                convertFragmentTransaction.replace(R.id.fragment_container, convertFragment);
                convertFragmentTransaction.addToBackStack(null);
                convertFragmentTransaction.commit();
                MainActivity.navigationView.getMenu().getItem(0).setChecked(true);
            }
        });
        return view;
    }//end onCreateView()

    public static void add(int id, Activity a) {
        DatabaseOpenHelper doh = new DatabaseOpenHelper(a);
        SQLiteDatabase db = doh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("currencyID", id);
        db.insert("favorites", null, contentValues);
        db.close();
    }//end add()

    private void updateCurrenciesListItems(View view) {
        int index = 0;

        //---- Base first
        int base = MainActivity.preferences.getInt("pref_BaseCurrency", 0);
        currencyListNames[index] = MainActivity.currencies[base].getName();
        currencyListFlags[index] = MainActivity.currencies[base].getFlag();
        currencyListIDs[index] = MainActivity.currencies[base].getId();
        index++;

        //---- Favorites next
        for (int i = 0; i < MainActivity.NUM_OF_CURRENCIES; i++) {
            if (Currency.isFavorite(MainActivity.currencies[i].getId(), view) &&
                    MainActivity.currencies[i].getId() != base) {
                currencyListNames[index] = MainActivity.currencies[i].getName();
                currencyListFlags[index] = MainActivity.currencies[i].getFlag();
                currencyListIDs[index] = MainActivity.currencies[i].getId();
                index++;
            }//end if favorite but not base
        }//end for

        //---- Non-Favorites last
        for (int i = 0; i < MainActivity.NUM_OF_CURRENCIES; i++) {
            if (!Currency.isFavorite(MainActivity.currencies[i].getId(), view) &&
                    MainActivity.currencies[i].getId() != base) {
                currencyListNames[index] = MainActivity.currencies[i].getName();
                currencyListFlags[index] = MainActivity.currencies[i].getFlag();
                currencyListIDs[index] = MainActivity.currencies[i].getId();
                index++;
            }//end if
        }//end for

    }//end updateCurrenciesListItems()

}//end class CurrenciesFragment
