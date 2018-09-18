package uk.ac.uclan.nkasenides.currencyconverter;

/**
 * Created by Nicos on 21-Jan-17.
 *
 * The FavoritesListAdapter allows custom display of the favorites list.
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

public class FavoritesListAdapter extends BaseAdapter {
    private Context context;
    private int[] flags;
    private String[] currencyNames;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    public FavoritesListAdapter(Context applicationContext, FragmentActivity activity, int[] flags, String[] currencyNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.currencyNames = currencyNames;
        inflater = (LayoutInflater.from(applicationContext));
        this.activity = activity;
    }

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
        view = inflater.inflate(R.layout.favoriteslistadapter, null);
        ImageView icon = (ImageView) view.findViewById(R.id.currencyFlag);
        TextView names = (TextView) view.findViewById(R.id.currencyName);
        ImageView base = (ImageView) view.findViewById(R.id.favorites_isBase);
        icon.setImageResource(flags[i]);
        names.setText(currencyNames[i]);

        //if base - display a house:
        if (Currency.getIDFromName(currencyNames[i]) == MainActivity.preferences.getInt("pref_BaseCurrency", 0)) {
            base.setVisibility(View.VISIBLE);
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false))
                base.setImageResource(R.drawable.base_white);
            else base.setImageResource(R.drawable.base_black);
        }//end if

        //Remove on click:
        final int pos = i;
        ImageButton deleteFromFavoritesBtn = (ImageButton) view.findViewById(R.id.currencyRemove);
        deleteFromFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View tView = view;
                FavoritesFragment.deleteFavorite(Currency.getIDFromName(currencyNames[pos]), tView.getContext());
                FavoritesFragment.favListAdapter.notifyDataSetChanged();
                android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
                android.support.v4.app.Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
                currentFragment.onResume();
                Toast.makeText(activity, "Deleted " + currencyNames[pos] + " from favorites." , Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }//end getView()

}//end class FavoritesListAdapter
