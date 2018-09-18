package uk.ac.uclan.nkasenides.currencyconverter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Created by Nicos
 *
 * The convert fragment allows the user to convert from one currency to all the other currencies.
 */
public class ConvertFragment extends Fragment {

    public static final String[] currencyNames = new String[MainActivity.NUM_OF_CURRENCIES];
    public static final int[] currencyFlags = new int[MainActivity.NUM_OF_CURRENCIES];

    static Spinner convertFromSpinner;
    static int selectedCurrency = 0;
    GridView convertedList;
    EditText inputEditText;
    public static String json = "";
    String[] gridviewValues;
    String[] gridviewCodes;
    int[] gridviewFlags;
    String[] gridviewNames;

    public ConvertFragment() {
        // Required empty public constructor
    }//end ConvertFragment()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_convert,container, false);
        MainActivity.hideKeyboard(getContext());

        inputEditText = (EditText) view.findViewById(R.id.convert_EditText);

        //Initialize the currency selection spinner:
        convertFromSpinner = (Spinner) view.findViewById(R.id.spn_convert_From);
        for (int i = 0; i < MainActivity.NUM_OF_CURRENCIES; i++) {
            currencyNames[i] = MainActivity.currencies[i].getName();
            currencyFlags[i] = MainActivity.currencies[i].getFlag();
        }//end for

        CustomSpinnerAdapter customAdapter= new CustomSpinnerAdapter(this.getActivity(),currencyFlags,currencyNames);
        convertFromSpinner.setAdapter(customAdapter);

        //On Converted Currency spinner selection (set selected currency):
        convertFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCurrency = position;
                updateConvertedCurrenciesList(view);
            }//end onItemSelected()

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        convertFromSpinner.setSelection(selectedCurrency);

        //On conversion list item click (display more info):
        convertedList = (GridView) view.findViewById(R.id.convertedList);
        updateConvertedCurrenciesList(view);
        convertedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                String selectedName = MainActivity.currencies[selectedCurrency].getName();
                String selectedAmount;
                String s = inputEditText.getText().toString();
                if (s.isEmpty()) s = "1"; selectedAmount = s;
                String convertedAmount = gridviewValues[position];
                String convertedName = gridviewNames[position];

                String messageString;
                String pluralFormSelected = "";
                String pluralFormConverted = "";
                double amountSelected = Double.parseDouble(selectedAmount);
                double amountConverted = Double.parseDouble(convertedAmount);
                if (amountSelected > 1) pluralFormSelected = "s";
                if (amountConverted > 1) pluralFormConverted = "s";

                messageString = selectedAmount + " " + selectedName + pluralFormSelected + "\n\n\t\tconverts to:\n\n" + convertedAmount + " " + convertedName + pluralFormConverted;
                builder.setMessage(messageString)
                        .setCancelable(true)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }//end onItemClick()
        });

        //On long click make the clicked currency the selected currency
        convertedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedCurrencyCode = gridviewCodes[i];
                for (int j = 0; j < MainActivity.currencies.length; j++) {
                    if (clickedCurrencyCode.equals(MainActivity.currencies[j].getCode())) {
                        selectedCurrency = j;
                        updateConvertedCurrenciesList(view);
                    }//end if
                }//end for
                return true;
            }//end onItemLongClick()
        });

        //On conversion number input change:
        inputEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

            public void onTextChanged(CharSequence s, int start,int before, int count) {
                String cText = inputEditText.getText().toString();
                if (cText == ".") inputEditText.setText("0.");
                updateConvertedCurrenciesList(view);
            }//end onTextChanged()
        });
        updateConvertedCurrenciesList(view);
        return view;
    }//end onCreateView()

    public void updateConvertedCurrenciesList(View view) {

        //Set up the decimal places:
        String decimalPlacesFormat;
        int decimalPlaces = MainActivity.preferences.getInt("pref_DecimalPlaces", 2);
        if (decimalPlaces == 1) decimalPlacesFormat = "%.1f";
        else if (decimalPlaces == 2) decimalPlacesFormat = "%.2f";
        else if (decimalPlaces == 3) decimalPlacesFormat = "%.3f";
        else if (decimalPlaces == 4) decimalPlacesFormat = "%.4f";
        else if (decimalPlaces == 5) decimalPlacesFormat = "%.5f";
        else decimalPlacesFormat = "%.2f";

        //Parse the input data:
        String text = inputEditText.getText().toString().trim();
        double parsedAmount;
        try { parsedAmount = Double.parseDouble(text); }
        catch (NumberFormatException e) { parsedAmount = 1.0; }

        //Convert:
        json = MainActivity.preferences.getString("data_Rates", Currency.baseData);
        Map rates = parseJSON(json);
        String currentCurrencyCode = MainActivity.currencies[selectedCurrency].getCode();
        String tEditText = inputEditText.getText().toString();
        Map<String, Double> convertedAmounts;
        if (text.isEmpty()) convertedAmounts = convertFrom(currentCurrencyCode, rates, 1.0);
        else convertedAmounts = convertFrom(currentCurrencyCode, rates, parsedAmount);

        final int SIZE_OF_GRIDVIEW = convertedAmounts.size();
        gridviewValues = new String[SIZE_OF_GRIDVIEW]; //CONVERTED VALUES
        gridviewCodes = new String[SIZE_OF_GRIDVIEW]; //CODES
        gridviewFlags = new int[SIZE_OF_GRIDVIEW]; //FLAGS
        gridviewNames = new String[SIZE_OF_GRIDVIEW]; //NAMES

        int base = MainActivity.preferences.getInt("pref_BaseCurrency", 0);

        //Values and codes
        {
            int c = 0;

            //Base currency first:
            for (String key : convertedAmounts.keySet()) {
                if (MainActivity.currencies[Currency.getIDFromCode(key)].getId() == base) {
                    gridviewValues[c] = String.format(decimalPlacesFormat, convertedAmounts.get(key));
                    gridviewCodes[c] = key;
                    c++;
                }//end if base
            }//end for

            //Then favorites (but not base):
            for (String key : convertedAmounts.keySet()) {
                if (Currency.isFavorite(Currency.getIDFromCode(key), view) &&
                        MainActivity.currencies[Currency.getIDFromCode(key)].getId() != base) {
                    gridviewValues[c] = String.format(decimalPlacesFormat, convertedAmounts.get(key));
                    gridviewCodes[c] = key;
                    c++;
                }//end if favorite but not base
            }//end for

            //Non-Favorites
            for (String key : convertedAmounts.keySet()) {
                if (!Currency.isFavorite(Currency.getIDFromCode(key), view) &&
                        MainActivity.currencies[Currency.getIDFromCode(key)].getId() != base) {
                    gridviewValues[c] = String.format(decimalPlacesFormat, convertedAmounts.get(key));
                    gridviewCodes[c] = key;
                    c++;
                }//end if
            }//end for
        }//end Values and Codes


        //Set Flags and Names
        for (int i = 0; i < SIZE_OF_GRIDVIEW; i++) {
            for (int j = 0; j < MainActivity.NUM_OF_CURRENCIES; j++) {
                if (MainActivity.currencies[j].getCode() == gridviewCodes[i]) {
                    gridviewFlags[i] = MainActivity.currencies[j].getFlag();
                    gridviewNames[i] = MainActivity.currencies[j].getName();
                }//end if
            }//end for2
        }//end for

        //Update the converted list:
        CustomGridAdapter customAdapter= new CustomGridAdapter(this.getActivity(),gridviewFlags, gridviewValues, gridviewNames);
        convertedList.setAdapter(customAdapter);
        convertFromSpinner.setSelection(selectedCurrency);
    }//end updateConvertedCurrenciesList()

    private HashMap parseJSON(String json) {
        try {
            HashMap<String, Double> retVal = new HashMap();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject rates = jsonObject.getJSONObject("rates");
            Iterator<String> keys = rates.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                double value = (double) rates.get(key);
                retVal.put(key, value);
            }//end while
            return retVal;
        }//end try
        catch (JSONException jsonE) { throw new RuntimeException(jsonE); }
    }//end parseJSON()

    private Map convertFrom(String currentCode, Map rates, double amount) {
        HashMap<String,Double> retVal = new HashMap<>();
        for (int i = 0; i < MainActivity.NUM_OF_CURRENCIES; i++) {
            if (MainActivity.currencies[i].getCode() != currentCode) {
                double rate_USD_to_X;
                if (currentCode == "USD") rate_USD_to_X = 1.0;
                else rate_USD_to_X = (double) rates.get(currentCode);

                double rate_X_to_USD = 1*amount/rate_USD_to_X;
                double rate_USD_to_Target;

                if (MainActivity.currencies[i].getCode() == "USD") rate_USD_to_Target = 1.0;
                else rate_USD_to_Target = (double) rates.get(MainActivity.currencies[i].getCode());

                double result = rate_X_to_USD * rate_USD_to_Target;
                retVal.put(MainActivity.currencies[i].getCode(), result);
            }//end if
        }//end for
        return retVal;
    }//end convertFrom()

}//end class ConvertFragment
