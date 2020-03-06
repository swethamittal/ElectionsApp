package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

public class CandidatePromisesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String aadhar;
    EditText et1;
    EditText et2;
    Button sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_candidate_promises );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        et1=findViewById(R.id.oath );
        et2=findViewById(R.id.hist);
        sub=findViewById( R.id.butt);
        Intent in = getIntent();
        aadhar=LoginActivity.AADHAR;

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

    }
    public void addNote(View v){
        String promise=et1.getText().toString().trim();
        String history=et2.getText().toString().trim();
        Map<String,String> prom=new HashMap<>();
        prom.put("oath",promise);
        prom.put("history",history);
        db.collection("Candidate").document(aadhar).set(prom, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d( "succ","succesful");
                        Toast.makeText(CandidatePromisesActivity.this,"promises and history submited!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d( "fail", "failed" );
                    }

                });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
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
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.CandidateProfileActivity");
            startActivity(intent);
        }else if (id == R.id.cl) {
            Intent intent=new Intent();
            intent.putExtra("AssemblyConstituency",LoginActivity.CONSTITUENCY);
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.CandidateListActivity");
            startActivity(intent);
        } else if (id == R.id.log) {
            Intent intent = new Intent();
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            intent.putExtra( "EXIT", true );
            intent.setClassName( CandidatePromisesActivity.this, "app.amazon.integratedapp.LoginActivity" );
            startActivity( intent );
        }
        else if (id == R.id.can) {
            Intent intent=new Intent();
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.CandidatePromisesActivity");
            startActivity(intent);
        }
        else if (id == R.id.abu) {
            Intent intent=new Intent();
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.AboutUsActivity");
            startActivity(intent);
        }
        else if (id == R.id.prof) {
            Intent intent=new Intent();
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.CandidateProfileInformationActivity");
            startActivity(intent);
        }
        else if (id == R.id.rul) {
            Intent intent=new Intent();
            intent.setClassName(CandidatePromisesActivity.this,"app.amazon.integratedapp.RulesIPAddressActivity");
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
