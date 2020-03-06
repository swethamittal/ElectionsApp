package app.amazon.integratedapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class VoterProfileInformationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static TextView tv1;
    static TextView tv2;
    static TextView tv3;
    static TextView tv4;
    static ImageView iv;
    static String Ano=LoginActivity.AADHAR;
    DocumentSnapshot doc;
    DocumentReference noteRef=null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;
    Intent intent;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_voter_profile_information );
        iv=findViewById( R.id.ivp );
        intent=getIntent();
        type=intent.getStringExtra("type_of_user");
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://elections-37945.appspot.com/elections/images/citizens/"+Ano+".jpg");
        try {
            final File localFile = File.createTempFile( "img","jpg" );
            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {
        }

        tv1=(TextView)findViewById(R.id.na);
        //tv2=(TextView) findViewById(R.id.An);
        //tv3=(TextView)findViewById(R.id.ar);
        //tv4=(TextView)findViewById( R.id.con );

        tv1.setText("Name:"+LoginActivity.NAME);
        //tv3.setText("Area:"+LoginActivity.AREA);
        //tv2.setText("Aadhaar number:"+LoginActivity.AADHAR);
        //tv4.setText("Constituency:"+LoginActivity.CONSTITUENCY);

        EditText et1=(EditText)findViewById(R.id.adhaarNumber);
        EditText et2=(EditText)findViewById(R.id.father_name);
        EditText et3=(EditText)findViewById(R.id.dob);
        //EditText et4=(EditText)findViewById(R.id.Mobile_no);
        EditText et5=(EditText)findViewById(R.id.Gender);
        EditText et6=(EditText)findViewById(R.id.Hno);
        EditText et7=(EditText)findViewById(R.id.Street);
        EditText et8=(EditText)findViewById(R.id.Area);
        EditText et9=(EditText)findViewById(R.id.District);
        EditText et10=(EditText)findViewById(R.id.Constituency);
        EditText et11=(EditText)findViewById(R.id.State);

        et1.setText(LoginActivity.AADHAR);
        et2.setText(LoginActivity.FATHER);
        et3.setText(LoginActivity.DOB);
        //et4.setText(LoginActivity.MOBILE);
        et5.setText(LoginActivity.GENDER);
        et6.setText(LoginActivity.HNO);
        et7.setText(LoginActivity.STREET);
        et8.setText(LoginActivity.AREA);
        et9.setText(LoginActivity.DISTRICT);
        et10.setText(LoginActivity.CONSTITUENCY);
        et11.setText(LoginActivity.STATE);

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        NavigationView navigationView = findViewById( R.id.nav_view );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );
    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            moveTaskToBack( true );
        }
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.hom) { Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VoterProfileActivity");
            startActivity(intent);
        } else if (id == R.id.CL) {
            Intent intent=new Intent();
            intent.putExtra("AssemblyConstituency","*");
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VoterCandidateListActivity");
            startActivity(intent);
        } else if (id == R.id.Stats) {
            Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VotersStatisticsIPAddressActivity");
            intent.putExtra("type_of_user",type);
            startActivity(intent);
        } else if (id == R.id.rules) {
            Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VotersRulesActivity");
            startActivity(intent);
        }
        else if (id == R.id.abu) {
            Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VotersAboutUsActivity");
            startActivity(intent);
        }else if (id == R.id.prof) {
            Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.VoterProfileInformationActivity");
            startActivity(intent);
        }
        else if (id == R.id.lo) {
            Intent intent=new Intent();
            intent.setClassName(VoterProfileInformationActivity.this,"app.amazon.integratedapp.LoginActivity");
            intent.putExtra( "EXIT",true );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}
