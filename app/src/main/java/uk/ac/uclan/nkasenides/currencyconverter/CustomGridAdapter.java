package uk.ac.uclan.nkasenides.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
    Created by Nicos

 * The CustomGridAdapter is linked to the ConvertFragment gridview and allows custom display of all
 * the converted currencies and their amounts.
 */

public class CustomGridAdapter extends BaseAdapter {
    private Context context;
    private int[] flags;
    private String[] convertedAmount;
    private String[] currencyName;
    private LayoutInflater inflater;

    public CustomGridAdapter(Context applicationContext, int[] flags, String[] convertedAmount, String[] currencyName) {
        this.context = applicationContext;
        this.flags = flags;
        this.convertedAmount = convertedAmount;
        this.currencyName = currencyName;
        inflater = (LayoutInflater.from(applicationContext));
    }//end CustomGridAdapter()

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
        view = inflater.inflate(R.layout.customgridview_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.grid_image);
        TextView amount = (TextView) view.findViewById(R.id.grid_amount);
        TextView name = (TextView) view.findViewById(R.id.grid_name);
        ImageView favorite = (ImageView) view.findViewById(R.id.item_favorite);
        ImageView base = (ImageView) view.findViewById(R.id.item_base);
        icon.setImageResource(flags[i]);
        amount.setText(convertedAmount[i]);
        name.setText(currencyName[i]);

        //If Favorite - display a star:
        if (Currency.isFavorite(Currency.getIDFromName(currencyName[i]), view)) {
            favorite.setVisibility(View.VISIBLE);
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false))
                favorite.setImageResource(R.drawable.light_star);
            else favorite.setImageResource(R.drawable.dark_star);
        }//end if favorite

        //If Base - display a house:
        if (Currency.getIDFromName(currencyName[i]) == MainActivity.preferences.getInt("pref_BaseCurrency", 0))
            base.setVisibility(View.VISIBLE);
            if (MainActivity.preferences.getBoolean("pref_DarkMode", false)) base.setImageResource(R.drawable.base_white);
            else base.setImageResource(R.drawable.base_black);

        return view;
    }//end getView()

}//end class CustomGridAdapter
