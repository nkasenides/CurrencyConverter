package uk.ac.uclan.nkasenides.currencyconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Nicos on 20-Jan-17.
 *
 * The RetrieveDataHelper downloads the latest currency rates from fixer.io
 * Code partially written by Dr. Nearchos Paspallis, UCLan Cyprus.
 */

public class RetrieveDataHelper extends AsyncTask<String, Void, String> {

    public static final String DATA_LINK = "http://api.fixer.io/latest?base=USD";
    private Context ctx;
    private View view;


    RetrieveDataHelper(Context context, View v) {
        ctx = context;
        view = v;
    }//end RetrieveDataHelper

    @Override protected String doInBackground(String... urls) {
        // execute in background, in separate thread – cannot edit the UI
        try { return downloadUrl(urls[0]); }
        catch (IOException ioe) { return "Error: " + ioe.getMessage(); }
    }//end doInBackground()

    @Override
    protected void onPostExecute(String result) {
        ConvertFragment.json = result;
        SharedPreferences.Editor editor = MainActivity.preferences.edit();
        editor.putString("data_Rates", result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String stringDate = jsonObject.getString("date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try { date = dateFormat.parse(stringDate); }
            catch (ParseException e) { e.printStackTrace(); }

            SimpleDateFormat humanFormat = new SimpleDateFormat("dd-MM-yyyy");
            String newFormatString = humanFormat.format(date);

            editor.putString("data_Date", newFormatString);
            editor.apply();
            MainActivity.updateLastUpdateLabel();
            MainActivity.convertFragment.updateConvertedCurrenciesList(view);
            Toast.makeText(ctx, "Rates updated", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(ctx, "Error with connection. Could not update.", Toast.LENGTH_LONG).show();
        }//end catch
    }//end onPostExecute()

    private String downloadUrl(final String urlAddress) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            Log.d(TAG, "The response code is: " + response);
            inputStream = conn.getInputStream();

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);

            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) stringBuilder.append(line).append("\n");

            return stringBuilder.toString();
        } finally { if (inputStream != null) { inputStream.close(); } }
    }//end downloadUrl()

}//end class RetrieveDataHelper