package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about_us );
        txt=findViewById( R.id.txt );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        NavigationView navigationView = findViewById( R.id.nav_view );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );
        txt.setText("ELECTIONS APP\n\n"+"The app is a one stop for elections\n"+"\nABOUT ELECTIONS\n\n"
                +"India is a sovereign, " +
                "socialist, secular, democratic republic. Democracy runs like a golden thread in the social, economic and" +
                " political fabric woven by the Constitution given by ‘We, the People of India’ unto ourselves. " +
                "The concept of democracy as visualised by the Constitution pre-supposes the representation of the" +
                " people in Parliament and State legislatures by the method of election. The Supreme Court has held that democracy " +
                "is one of the inalienable basic features of the Constitution of India and forms part of its basic structure. " +
                "The Constitution of India adopted a Parliamentary form of government. Parliament consists of the President of India and the two Houses — Rajya Sabha and Lok Sabha. " +
                "India, being a Union of states, has separate state legislatures for each state. " +
                "State legislatures consist of the Governor and two Houses — Legislative Council and Legislative Assembly — in seven states, namely, " +
                "Andhra Pradesh, Telangana, Bihar, Jammu & Kashmir, Karnataka, Maharashtra and Uttar Pradesh, and of the Governor and " +
                "the state Legislative Assembly in the remaining 22 states. Apart from the above, two out of the seven Union Territories, namely," +
                " National Capital Territory of Delhi and Puducherry, also have their Legislative Assemblies.\n\n"+"CONTACT US:\n"+"+91 9967890111,040-24146531");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.hom) {
            Intent intent=new Intent();
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.CandidateProfileActivity");
            startActivity(intent);
        }else if (id == R.id.cl) {
            Intent intent=new Intent();
            intent.putExtra("AssemblyConstituency",LoginActivity.CONSTITUENCY);
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.CandidateListActivity");
            startActivity(intent);
        } else if (id == R.id.log) {
            Intent intent=new Intent();
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            intent.putExtra( "EXIT",true );
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.LoginActivity");
            startActivity(intent);
        }
        else if(id ==R.id.stas){
            Intent intent=new Intent( );
            intent.setClassName( AboutUsActivity.this,"app.amazon.integratedapp.StatisticsIPAddressActivity" );
            startActivity(intent);
        }
        else if (id == R.id.abu) {
            Intent intent=new Intent();
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.AboutUsActivity");
            startActivity(intent);
        }
        else if (id == R.id.prof) {
            Intent intent=new Intent();
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.CandidateProfileInformationActivity");
            startActivity(intent);
        }

        else if (id == R.id.can) {
            Intent intent=new Intent();
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.CandidatePromisesActivity");
            startActivity(intent);
        }
        else if (id == R.id.rul) {
            Intent intent=new Intent();
            intent.setClassName(AboutUsActivity.this,"app.amazon.integratedapp.RulesIPAddressActivity");
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
