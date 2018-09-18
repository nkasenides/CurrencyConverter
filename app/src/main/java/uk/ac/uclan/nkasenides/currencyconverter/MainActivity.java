package uk.ac.uclan.nkasenides.currencyconverter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by Nicos
 *
 * The main activity of the application is what links all the fragments, navigation menu and
 * settings toolbar together. Contains the fragment container which contains the fragment to display.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static NavigationView navigationView = null;
    Toolbar toolbar = null;
    static SharedPreferences preferences;
    static TextView dateView;

    public static final int NUM_OF_CURRENCIES = 32;
    public static final Currency[] currencies = new Currency[NUM_OF_CURRENCIES];

    //THE FRAGMENTS:
    static ConvertFragment convertFragment;
    CurrenciesFragment currenciesFragment;
    FavoritesFragment favoritesFragment;
    static SettingsFragment settingsFragment;
    AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization of non-UI elements
        Currency.initializeCurrencies();

        //Initialize Fragments:
        convertFragment = new ConvertFragment();
        currenciesFragment = new CurrenciesFragment();
        favoritesFragment = new FavoritesFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();

        //The Convert fragment - Open this on start:
        android.support.v4.app.FragmentTransaction convertFragmentTransaction = getSupportFragmentManager().beginTransaction();
        convertFragmentTransaction.replace(R.id.fragment_container,convertFragment);
        convertFragmentTransaction.commit();

        //Toolbar:
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer:
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle  actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                boolean drawerIsOpen = drawer.isDrawerOpen(GravityCompat.START);
                if (drawerIsOpen) drawer.closeDrawer(GravityCompat.START);
                else {
                    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                    if (manager.getBackStackEntryCount() > 0) {
                        android.support.v4.app.Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
                        if (currentFragment instanceof ConvertFragment) showKeyboard();
                    }//end if stack not-empty
                }//end else
            }//end onDrawerClosed()

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }//end onDrawerOpened()

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideKeyboard();
            }//end onDrawerSlide()

        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Navigation View:
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        //Preferences:
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConvertFragment.selectedCurrency = preferences.getInt("pref_BaseCurrency", 0);
        boolean darkTheme = preferences.getBoolean("pref_DarkMode", false);
        if (darkTheme) setDarkTheme(findViewById(R.id.drawer_layout));
        else setLightTheme(findViewById(R.id.drawer_layout));

    }//end onCreate()

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        boolean drawerIsOpen = drawer.isDrawerOpen(GravityCompat.START);
        if (drawerIsOpen) drawer.closeDrawer(GravityCompat.START);
        else {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                super.onBackPressed();
                android.support.v4.app.Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ConvertFragment)
                    navigationView.getMenu().getItem(0).setChecked(true);
                else if (currentFragment instanceof CurrenciesFragment)
                    navigationView.getMenu().getItem(2).setChecked(true);
                else if (currentFragment instanceof FavoritesFragment)
                    navigationView.getMenu().getItem(1).setChecked(true);
                else if (currentFragment instanceof SettingsFragment)
                    navigationView.getMenu().getItem(3).setChecked(true);
                else if (currentFragment instanceof  AboutFragment)
                    navigationView.getMenu().getItem(4).setChecked(true);
            }//end if stack non-empty
            else super.onBackPressed();
        }//end if drawer closed
    }//end onBackPressed()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu. Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (preferences.getBoolean("pref_AutoUpdate", true))
            updateData(findViewById(R.id.fragment_container).getContext(), findViewById(R.id.fragment_container));
        else checkForUpdate(this.findViewById(R.id.fragment_container).getContext());
        return true;
    }//end onCreateOptionsMenu()

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            android.support.v4.app.FragmentTransaction settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
            settingsFragmentTransaction.replace(R.id.fragment_container,settingsFragment);
            settingsFragmentTransaction.addToBackStack(null);
            settingsFragmentTransaction.commit();
            navigationView.getMenu().getItem(3).setChecked(true);
        }//end if settings
        else if (id == R.id.action_resetToBaseCurrency) {
            ConvertFragment.selectedCurrency = preferences.getInt("pref_BaseCurrency", 0);
            ConvertFragment.convertFromSpinner.setSelection(ConvertFragment.selectedCurrency);
        }//end if reset
        else if (id == R.id.action_update) updateData(findViewById(R.id.fragment_container).getContext(), findViewById(R.id.fragment_container));

        return super.onOptionsItemSelected(item);
    }//end onOptiosnItemSelected()

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (ConvertFragment.selectedCurrency == preferences.getInt("pref_BaseCurrency", 0))
            menu.getItem(2).setEnabled(false);
        else menu.getItem(2).setEnabled(true);
        dateView = (TextView) findViewById(R.id.updatedDate);
        updateLastUpdateLabel();
        return true;
    }//end onPrepareOptionsMenu()

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_convert) {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ConvertFragment) {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.show(currentFragment);
            }//end if Convert
            else {
                android.support.v4.app.FragmentTransaction convertFragmentTransaction = getSupportFragmentManager().beginTransaction();
                convertFragmentTransaction.replace(R.id.fragment_container,convertFragment);
                convertFragmentTransaction.addToBackStack("convert_fragment");
                convertFragmentTransaction.commit();
            }//end if not Convert
        }//end if Convert clicked
        else if (id == R.id.nav_currencies) {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof CurrenciesFragment) {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.show(currentFragment);
            }//end if Currencies
            else {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.replace(R.id.fragment_container,currenciesFragment);
                currenciesFragmentTransaction.addToBackStack("currencies_fragment");
                currenciesFragmentTransaction.commit();
            }//end if not Currencies
        }//end if Currencies clicked
        else if (id == R.id.nav_favorites) {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof FavoritesFragment) {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.show(currentFragment);
            }//end if Favorites
            else {
                android.support.v4.app.FragmentTransaction favoritesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                favoritesFragmentTransaction.replace(R.id.fragment_container, favoritesFragment);
                favoritesFragmentTransaction.addToBackStack("favorites_fragment");
                favoritesFragmentTransaction.commit();
            }//end if not Favorites
        }//end if Favorites clicked
        else if (id == R.id.nav_settings) {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof SettingsFragment) {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.show(currentFragment);
            }//end if Settings
            else {
                android.support.v4.app.FragmentTransaction settingsFragmentTransaction = getSupportFragmentManager().beginTransaction();
                settingsFragmentTransaction.replace(R.id.fragment_container, settingsFragment);
                settingsFragmentTransaction.addToBackStack("settings_fragment");
                settingsFragmentTransaction.commit();
            }//end if not Settings
        }//end if Settings clicked
        else if (id == R.id.nav_about) {
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof AboutFragment) {
                android.support.v4.app.FragmentTransaction currenciesFragmentTransaction = getSupportFragmentManager().beginTransaction();
                currenciesFragmentTransaction.show(currentFragment);
            }//end if About
            else {
                android.support.v4.app.FragmentTransaction aboutFragmentTransaction = getSupportFragmentManager().beginTransaction();
                aboutFragmentTransaction.replace(R.id.fragment_container, aboutFragment);
                aboutFragmentTransaction.addToBackStack("about_fragment");
                aboutFragmentTransaction.commit();
            }//end if not About
        }//end if About clicked

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }//end onNavigationItemSelected()

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View vFocus = ((Activity) ctx).getCurrentFocus();
        if (vFocus == null) return;
        inputManager.hideSoftInputFromWindow(vFocus.getWindowToken(), 0);
    }//end hideKeyboard()

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View vFocus = getCurrentFocus();
        if (vFocus == null) return;
        inputManager.hideSoftInputFromWindow(vFocus.getWindowToken(), 0);
    }//end hideKeyboard()

    //Not used - left as stub:
    public static void showKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View vFocus = ((Activity) ctx).getCurrentFocus();
        if (vFocus == null) return;
        inputManager.showSoftInput(vFocus, 0);
    }//end showKeyboard()

    private void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View vFocus = getCurrentFocus();
        if (vFocus == null) return;
        inputManager.showSoftInput(vFocus, 0);
    }//end showKeyboard()

    public static void setDarkTheme(View v) {
        v.getContext().setTheme(R.style.AppTheme_Dark_NoActionBar);
        v.setBackgroundColor(v.getResources().getColor(R.color.darkColorPrimaryDark));
    }//end setDarkTheme()

    public static void setLightTheme(View v) {
        v.getContext().setTheme(R.style.AppTheme);
        v.setBackgroundColor(v.getResources().getColor(R.color.colorBackground));
    }//end setLightTheme()

    public static void updateData(Context ctx, View view) {
        new RetrieveDataHelper(ctx, view).execute(RetrieveDataHelper.DATA_LINK);
    }//end updateData()

    public static void checkForUpdate(Context ctx) {
        new CheckDataHelper(ctx).execute(CheckDataHelper.CHECK_LINK);
    }//end checkForUpdate()

    public static void updateLastUpdateLabel() {
        dateView.setText(preferences.getString("data_Date", Currency.baseDataDate));
    }//end updateLastUpdateLabel()

}//end class MainActivity
