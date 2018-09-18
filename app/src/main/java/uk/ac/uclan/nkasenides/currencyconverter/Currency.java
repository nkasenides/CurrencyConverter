package uk.ac.uclan.nkasenides.currencyconverter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import uk.ac.uclan.nkasenides.currencyconverter.DBUtils.DatabaseOpenHelper;
import static uk.ac.uclan.nkasenides.currencyconverter.MainActivity.NUM_OF_CURRENCIES;
import static uk.ac.uclan.nkasenides.currencyconverter.MainActivity.currencies;

/**
 * Created by Nicos on 03-Jan-17.
 *
 * Holds information about the Currency class and the initial currency rates data.
 */

public class Currency {
    private final int id;
    private final String code;
    private final String name;
    private int flag = R.drawable.no_flag;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public int getFlag() { return flag; }

    //Unused - left as stub:
    Currency(int sID, String sCode, String sName) {
        id = sID;
        code = sCode;
        name = sName;
        flag = R.drawable.no_flag;
    }//end Currency()

    Currency(int sID, String sCode, String sName, int sFlag) {
        id = sID;
        code = sCode;
        name = sName;
        flag = sFlag;
    }//end Currency()

    //Hard-coded base data:
    public static final String baseDataDate = "06-01-2017";
    public static final String baseData = "{\"base\":\"USD\",\"date\":\"2017-01-06\",\"rates\":{\"AUD\":1.363,\"BGN\":1.847,\"BRL\":3.2019,\"CAD\":1.3258,\"CHF\":1.0128,\"CNY\":6.9178,\"CZK\":25.518,\"DKK\":7.0208,\"GBP\":0.80884,\"HKD\":7.7553,\"HRK\":7.1565,\"HUF\":290.15,\"IDR\":13365.0,\"ILS\":3.8359,\"INR\":67.989,\"JPY\":116.0,\"KRW\":1191.2,\"MXN\":21.276,\"MYR\":4.4725,\"NOK\":8.4869,\"NZD\":1.4235,\"PHP\":49.35,\"PLN\":4.1129,\"RON\":4.2509,\"RUB\":59.258,\"SEK\":9.0202,\"SGD\":1.4325,\"THB\":35.66,\"TRY\":3.6174,\"ZAR\":13.616,\"EUR\":0.94438}}";

    //Unused - left as stub:
    static String getNameFromID(int id) { return currencies[id].getName(); }

    static int getIDFromName(String name) {
        for (int i = 0; i < NUM_OF_CURRENCIES; i++)
            if (currencies[i].getName() == name) return i;
        return -1;
    }//end getIDFromName()

    static int getIDFromCode(String code) {
        for (int i = 0; i < NUM_OF_CURRENCIES; i++)
            if (currencies[i].getCode() == code) return i;
        return -1;
    }//end getIDFromCode()

    static boolean isFavorite(int currencyID, View view) {
        DatabaseOpenHelper doh = new DatabaseOpenHelper(view.getContext());
        SQLiteDatabase db = doh.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT currencyID FROM favorites WHERE currencyID=" + currencyID, null);
        int numOfResults = cursor.getCount();
        cursor.close();
        db.close();
        return (numOfResults > 0);
    }//end isFavorite()

    static void initializeCurrencies() {
        currencies[0] = new Currency(0, "USD", "U.S Dollar", R.drawable.usd_flag);
        currencies[1] = new Currency(1, "AUD", "Australian Dollar", R.drawable.aud_flag);
        currencies[2] = new Currency(2, "BGN", "Bulgarian Lev", R.drawable.bgn_flag);
        currencies[3] = new Currency(3, "BRL", "Brazilian Real", R.drawable.brl_flag);
        currencies[4] = new Currency(4, "CAD", "Canadian Dollar", R.drawable.cad_flag);
        currencies[5] = new Currency(5, "CHF", "Swiss Franc", R.drawable.chf_flag);
        currencies[6] = new Currency(6, "CNY", "Chinese Yuan", R.drawable.cny_flag);
        currencies[7] = new Currency(7, "CZK", "Czech Koruna", R.drawable.czk_flag);
        currencies[8] = new Currency(8, "DKK", "Danish Krone", R.drawable.dkk_flag);
        currencies[9] = new Currency(9, "GBP", "British Pound", R.drawable.gbp_flag);
        currencies[10] = new Currency(10, "HKD", "Hong Kong Dollar", R.drawable.hkd_flag);
        currencies[11] = new Currency(11, "HRK", "Croatian Kuna", R.drawable.hrk_flag);
        currencies[12] = new Currency(12, "HUF", "Hungarian Forint", R.drawable.huf_flag);
        currencies[13] = new Currency(13, "IDR", "Indonesian Rupiah",R.drawable.idr_flag);
        currencies[14] = new Currency(14, "ILS", "Israeli Shekel", R.drawable.ils_flag);
        currencies[15] = new Currency(15, "INR", "Indian Rupee", R.drawable.inr_flag);
        currencies[16] = new Currency(16, "JPY", "Japanese Yen", R.drawable.jpy_flag);
        currencies[17] = new Currency(17, "KRW", "South Korean Won", R.drawable.krw_flag);
        currencies[18] = new Currency(18, "MXN", "Mexican Peso", R.drawable.mxn_flag);
        currencies[19] = new Currency(19, "MYR", "Malaysian Ringgit", R.drawable.myr_flag);
        currencies[20] = new Currency(20, "NOK", "Norwegian Krone", R.drawable.nok_flag);
        currencies[21] = new Currency(21, "NZD", "New Zealand Dollar", R.drawable.nzd_flag);
        currencies[22] = new Currency(22, "PHP", "Philippine Peso", R.drawable.php_flag);
        currencies[23] = new Currency(23, "PLN", "Polish Zloty", R.drawable.pln_flag);
        currencies[24] = new Currency(24, "RON", "Romanian Leu", R.drawable.ron_flag);
        currencies[25] = new Currency(25, "RUB", "Russian Rouble", R.drawable.rub_flag);
        currencies[26] = new Currency(26, "SEK", "Swedish Krona", R.drawable.sek_flag);
        currencies[27] = new Currency(27, "SGD", "Singapore Dollar", R.drawable.sgd_dollar);
        currencies[28] = new Currency(28, "THB", "Thai Baht", R.drawable.thb_flag);
        currencies[29] = new Currency(29, "TRY", "Turkish Lira", R.drawable.try_flag);
        currencies[30] = new Currency(30, "ZAR", "South African Rand", R.drawable.zar_flag);
        currencies[31] = new Currency(31, "EUR", "Euro", R.drawable.eur_flag);
    }//end initializeCurrencies()

}//end class Currency


