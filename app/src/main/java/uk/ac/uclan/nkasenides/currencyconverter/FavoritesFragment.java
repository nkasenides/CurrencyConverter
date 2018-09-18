package uk.ac.uclan.nkasenides.currencyconverter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
 *
 * The favorites fragment displays all the favorited currencies and allows the user to delete them
 * from favorites.
 */
public class FavoritesFragment extends Fragment {

    int numOfResults = 0;

    ListView favoritesListview;
    static FavoritesListAdapter favListAdapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }//end FavoritesFragment()


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites,container, false);
        favoritesListview = (ListView) view.findViewById(R.id.favorites_listview);
        MainActivity.hideKeyboard(getContext());

        return view;
    }//end onCreateView()

    @Override
    public void onResume() {
        super.onResume();
        final int[] favIdsInt = getFavoritesIds();
        final int[] favFlags = getFavoritesFlags();
        final String[] favIdsStr = new String[numOfResults];
        for (int i = 0;  i < numOfResults; i++) favIdsStr[i] = MainActivity.currencies[favIdsInt[i]].getName();
        favListAdapter = new FavoritesListAdapter(this.getContext(), this.getActivity(),favFlags,favIdsStr);
        favoritesListview.setAdapter(favListAdapter);

        //On item click move to conversion screen, with this currency selected:
        favoritesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                ConvertFragment.selectedCurrency = favIdsInt[pos];
                ConvertFragment convertFragment = new ConvertFragment();
                android.support.v4.app.FragmentTransaction convertFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                convertFragmentTransaction.replace(R.id.fragment_container, convertFragment);
                convertFragmentTransaction.addToBackStack(null);
                convertFragmentTransaction.commit();
                MainActivity.navigationView.getMenu().getItem(0).setChecked(true);
            }
        });

    }//end onResume()

    int[] getFavoritesIds() {
        DatabaseOpenHelper doh = new DatabaseOpenHelper(this.getActivity());
        SQLiteDatabase db = doh.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT currencyID FROM favorites", null);
        numOfResults = cursor.getCount();
        final int [] favoritesIDs = new int[numOfResults];
        cursor.moveToFirst();

        for(int i = 0; i < numOfResults; i++) {
            favoritesIDs[i] = cursor.getInt(cursor.getColumnIndex("currencyID"));
            cursor.moveToNext();
        }//end getFavoritesIds()
        cursor.close();
        return favoritesIDs;
    }//end getFavoritesIds()

    int[] getFavoritesFlags() {
        DatabaseOpenHelper doh = new DatabaseOpenHelper(this.getActivity());
        SQLiteDatabase db = doh.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT currencyID FROM favorites", null);
        numOfResults = cursor.getCount();
        final int [] favoritesIDs = new int[numOfResults];
        cursor.moveToFirst();

        for(int i = 0; i < numOfResults; i++) {
            favoritesIDs[i] = cursor.getInt(cursor.getColumnIndex("currencyID"));
            cursor.moveToNext();
        }//end for

        final int [] flags = new int[numOfResults];
        for (int i = 0; i < numOfResults; i++)
            flags[i] = MainActivity.currencies[favoritesIDs[i]].getFlag();

        cursor.close();
        return flags;
    }//end getFavoritesFlags()

    public static void deleteFavorite(int id, Context ctx) {
        DatabaseOpenHelper doh = new DatabaseOpenHelper(ctx);
        SQLiteDatabase db = doh.getWritableDatabase();
        db.delete("favorites", "currencyID=" + DatabaseUtils.sqlEscapeString(Integer.toString(id)) + "", null);
    }//end deleteFavorite()

}//end class FavoritesFragment