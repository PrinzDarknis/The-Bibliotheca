package com.prinzdarknis.thebibliotheca;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.database.v1.DBAndroid;
import com.prinzdarknis.thebibliotheca.imageManager.ImageManagerAndroid;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;
import com.prinzdarknis.thebibliotheca.ui.Overviews.ExemplarOverview;
import com.prinzdarknis.thebibliotheca.ui.SingleViews.ExemplarView;
import com.prinzdarknis.thebibliotheca.ui.SingleViews.SeriesView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static MainActivity me = null;
    NavController navController;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;

        //init
        DBAndroid.initialize(getApplicationContext());
        ProgramLogic.initialize(
                new ImageManagerAndroid(getFilesDir()),
                DBAndroid.getInstance()
        );

        //ActionBar (prepair)
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigator
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_libary, R.id.nav_series, R.id.nav_exemplar, R.id.nav_appInfo)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Navcontroler set Libraryname
        TextView libraryName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_libary_name);
        String activeLibrary = ProgramLogic.getInstance().getActiveLibraryName();
        if (activeLibrary != null)
            libraryName.setText(activeLibrary);
        else
            libraryName.setText(getString(R.string.menu_noLibrary));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void setBibNameInNav(String bibName) {
        if (me != null) {
            TextView t = me.findViewById(R.id.nav_header_libary_name);
            if (t != null)
                t.setText(bibName);
        }
    }

    public static void openSeries(Series series) {
        if (me != null) {
            Bundle args = new Bundle();
            args.putString(SeriesView.ARG_TITLE, series.name);
            args.putSerializable(SeriesView.ARG_SERIES, series);
            me.navController.navigate(R.id.nav_series_edit, args);
        }
    }

    public static void openExemplar(Exemplar exemplar) {
        if (me != null) {
            Bundle args = new Bundle();
            args.putString(ExemplarView.ARG_TITLE, exemplar.name);
            args.putSerializable(ExemplarView.ARG_EXEMPLAR, exemplar);
            me.navController.navigate(R.id.nav_exemplar_edit, args);
        }
    }

    public static void openExemplarsOfSeries(Series series) {
        if (me != null) {
            Bundle args = new Bundle();
            args.putString(ExemplarOverview.ARG_TITLE, series.name);
            args.putSerializable(ExemplarOverview.ARG_SERIES_ID, series.id);
            me.navController.navigate(R.id.nav_exemplar, args);
        }
    }
}
