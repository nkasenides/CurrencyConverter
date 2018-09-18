package uk.ac.uclan.nkasenides.currencyconverter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
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

/**
 * Created by Nicos on 20-Jan-17.
 *
 * The CheckDataHelper class checks whether or not updates for the rates exist. (does not update)
 */

public class CheckDataHelper extends AsyncTask<String, Void, String> {

    public static final String CHECK_LINK = "http://api.fixer.io/latest?base=USD";
    private Context ctx;

    CheckDataHelper(Context context) {
        ctx = context;
    }

    @Override protected String doInBackground(String... urls) {
        try { return downloadUrl(urls[0]); }
        catch (IOException ioe) { return "Error: " + ioe.getMessage(); }
    }//end doInBackground()

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String stringDate = jsonObject.getString("date");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try { date = dateFormat.parse(stringDate); }
            catch (ParseException e) { e.printStackTrace(); }

            Date today = new Date();

            if (date != today) {
                final NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                final Notification notification =
                        new Notification.Builder(ctx)
                                .setSmallIcon(R.drawable.light_star)
                                .setContentTitle("Currency Converter")
                                .setContentText("Your currency rates are outdated.")
                                .setSubText("Rates last updated on: " + MainActivity.preferences.getString("data_Date", Currency.baseDataDate))
                                .build();
                notification.vibrate = new long[]{0,100,200,300};
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(1, notification);
                Toast.makeText(ctx, "Updates are available", Toast.LENGTH_LONG).show();
            }//end if date not equal
        } catch (JSONException e) { Toast.makeText(ctx, "Error with connection. Could not check for update.", Toast.LENGTH_LONG).show(); }
    }//end onPostExecute()

    private String downloadUrl(final String urlAddress) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode(); //--unused, left as a stub
            inputStream = conn.getInputStream();
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) stringBuilder.append(line).append("\n");
            return stringBuilder.toString();
        } finally { if (inputStream != null) { inputStream.close(); } }
    }//end downloadUrl()

}//end class CheckDataHelper