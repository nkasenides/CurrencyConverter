package uk.ac.uclan.nkasenides.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nicos
 *
 * The CustomSpinnerAdapter is linked to all the spinners in the application and allows custom
 * display of the options (currencies).
 */

public class CustomSpinnerAdapter extends BaseAdapter {
    private Context context;
    private int[] flags;
    private String[] currencyNames;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context applicationContext, int[] flags, String[] currencyNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.currencyNames = currencyNames;
        inflater = (LayoutInflater.from(applicationContext));
    }//end CustomSpinnerAdapter()

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
        view = inflater.inflate(R.layout.customcurrencyspinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.currencyFlag);
        TextView names = (TextView) view.findViewById(R.id.currencyName);
        icon.setImageResource(flags[i]);
        names.setText(currencyNames[i]);
        return view;
    }//end getView()

}//end class CustomSpinnerAdapter()
