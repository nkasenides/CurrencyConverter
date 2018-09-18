package uk.ac.uclan.nkasenides.currencyconverter;

/**
 * Created by Nicos on 21-Jan-17.
 *
 * The CurrenciesListAdapter is linked to the adapter of the currencies listview in the
 * CurrenciesFragment
 */

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CurrenciesListAdapter extends BaseAdapter {
    private Context context;
    private int[] flags;
    private String[] currencyNames;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    public CurrenciesListAdapter(Context applicationContext, FragmentActivity activity, int[] flags, String[] currencyNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.currencyNames = currencyNames;
        inflater = (LayoutInflater.from(applicationContext));
        this.activity = activity;
    }//end CurrenciesListAdapter()

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.currencieslistadapter, null);
        ImageView icon = (ImageView) view.findViewById(R.id.currencyFlag);
        TextView names = (TextView) view.findViewById(R.id.currencyName);
        ImageView base = (ImageView) view.findViewById(R.id.currencies_isBase);
        icon.setImageResource(flags[i]);
        names.setText(currencyNames[i]);

        if (Currency.getIDFromName(currencyNames[i]) == MainActivity.preferences.getInt("pref_BaseCurrency", 0)) {
            base.setVisibility(View.VISIBLE);
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false))
                base.setImageResource(R.drawable.base_white);
            else base.setImageResource(R.drawable.base_black);
        }//end if base

        updateFavBtn(view, i);
        final int pos = i;

        ImageButton convertCurrency = (ImageButton) view.findViewById(R.id.currencyConvert);
        convertCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConvertFragment.selectedCurrency = Currency.getIDFromName(currencyNames[pos]);
                ConvertFragment convertFragment = new ConvertFragment();
                android.support.v4.app.FragmentTransaction convertFragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                convertFragmentTransaction.replace(R.id.fragment_container, convertFragment);
                convertFragmentTransaction.addToBackStack(null);
                convertFragmentTransaction.commit();
                MainActivity.navigationView.getMenu().getItem(0).setChecked(true);
            }
        });

        return view;
    }//end getView()

    public void updateFavBtn(View view, int i) {
        ImageButton addFavoriteBtn = (ImageButton) view.findViewById(R.id.currencyAddFavorite);

        //Not favorite:
        if (!Currency.isFavorite(Currency.getIDFromName(currencyNames[i]), view)) {
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false))
                addFavoriteBtn.setImageResource(R.drawable.light_star_hollow);
            else addFavoriteBtn.setImageResource(R.drawable.dark_star_hollow);
        }//end if not favorite
        //Favorite:
        else {
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false))
                addFavoriteBtn.setImageResource(R.drawable.light_star);
            else addFavoriteBtn.setImageResource(R.drawable.dark_star);
            addFavoriteBtn.setVisibility(View.VISIBLE);
        }//end if favorite
        addFavoriteBtn.setVisibility(View.VISIBLE);

        final int pos = i;
        final int CPOS = Currency.getIDFromName(currencyNames[pos]);
        addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Currency.isFavorite(CPOS, view)) {
                    CurrenciesFragment.add(CPOS, activity);
                    Toast.makeText(activity, "Added " + currencyNames[pos] + " as a favorite.", Toast.LENGTH_SHORT).show();
                    updateFavBtn(view, pos);
                }//end if not favorite (add it)
                else {
                    final View tView = view;
                    FavoritesFragment.deleteFavorite(CPOS, context);
                    Toast.makeText(activity, "Deleted " + currencyNames[pos] + " from favorites.", Toast.LENGTH_SHORT).show();
                    updateFavBtn(tView, pos);
                }//end else (favorite - remove it)
            }
        });
    }//end updateFavBtn()

}//end class CurrenciesListAdapter
