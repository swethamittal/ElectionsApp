package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoterProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static TextView tv1;
    static TextView tv2;
    TextView timer, result;
    String AADHAR = LoginActivity.AADHAR;
    String NAME = LoginActivity.NAME;
    String AREA = LoginActivity.AREA;
    String CONSTITUENCY = LoginActivity.CONSTITUENCY;
    Intent intent;
    GlobalsActivity g;
    String type;
    FirebaseFirestore db;
    String edate, cdate;
    SimpleDateFormat rdf1 = new SimpleDateFormat("dd-mm-yyyy");
    Date d1, d2;
    int hour;
    String fdate;
    Long rt;
    String Winner;
    Intent it;
    boolean voteflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_profile);
        db = FirebaseFirestore.getInstance();
        timer = (TextView) findViewById(R.id.timer);
        result = findViewById(R.id.result);
        tv1 = (TextView) findViewById(R.id.nam);
        tv2 = (TextView) findViewById(R.id.aad);
        tv1.setText("Welcome " + NAME + "!");
        tv2.setText("AADHAR NO:" + AADHAR);
        getDate();
        intent = getIntent();
        type = intent.getStringExtra("type_of_user");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.hom) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VoterProfileActivity");
            startActivity(intent);
        } else if (id == R.id.CL) {
            Intent intent = new Intent();
            intent.putExtra("AssemblyConstituency", "*");
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VoterCandidateListActivity");
            startActivity(intent);
        } else if (id == R.id.Stats) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VotersStatisticsIPAddressActivity");
            startActivity(intent);
        } else if (id == R.id.rules) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VotersRulesActivity");
            startActivity(intent);
        } else if (id == R.id.abu) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VotersAboutUsActivity");
            startActivity(intent);
        } else if (id == R.id.prof) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VoterProfileInformationActivity");
            intent.putExtra("type_of_user", type);
            startActivity(intent);
        } else if (id == R.id.lo) {
            Intent intent = new Intent();
            intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.LoginActivity");
            intent.putExtra("EXIT", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void votecheck(View v) {
        voteflag = true;
        getDate();
    }

    //voting method
    public void methodxyz() {
        String arr[] = cdate.split(" ");
        fdate = arr[0];
        if (edate.equals(fdate)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("citizen")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d("Aadharnumber: ",String.valueOf(document.get("Adharcard_number")));
                                    if (String.valueOf(document.get("Adharcard_number")).equals(AADHAR)) {
                                        Boolean votestatus = document.getBoolean("Vote_Status");
                                        if (votestatus == true) {
                                            Intent intent = new Intent(VoterProfileActivity.this, AlreadyVotedActivity.class);
                                            startActivity(intent);
                                        } else {

                                            Bundle bundle = new Bundle();
                                            bundle.putString("aadharcard", AADHAR);
                                            Intent intent = new Intent(VoterProfileActivity.this, VotingListActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(VoterProfileActivity.this, "Today is not the election day", Toast.LENGTH_SHORT).show();
        }
    }

    public void onProfileClick(View view) {
        //System.out.println("Icon click extras values: "+name+" "+partyname+" "+partyno+" "+constituency+" "+sino);
        Intent intent = new Intent(getApplicationContext(), VoterProfileInformationActivity.class);
        intent.putExtra("Ano", LoginActivity.AADHAR);
        System.out.println("putting Ano in icon" + LoginActivity.AADHAR);
        startActivity(intent);
    }

    public void onStatisticsClick(View view) {
        Intent intent = new Intent(getApplicationContext(), VotersStatisticsIPAddressActivity.class);
        startActivity(intent);
    }

    public void onRulesClick(View view) {
        Intent intent = new Intent(getApplicationContext(), VotersRulesActivity.class);
        startActivity(intent);
    }

    public void all(View v) {
        Intent intent = new Intent();
        intent.putExtra("AssemblyConstituency", "*");
        intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VoterCandidateListActivity");
        startActivity(intent);
    }

    public void self(View v) {
        Intent intent = new Intent();
        intent.setClassName(VoterProfileActivity.this, "app.amazon.integratedapp.VoterCandidateListActivity");
        intent.putExtra("AssemblyConstituency", LoginActivity.CONSTITUENCY);
        startActivity(intent);
    }

    public void ReadFromDb() {
        String date[] = edate.split("-");
        //Log.d(TAG,"date "+date[0]);
        String output[] = cdate.split(" ");
        String dt[] = (output[0].toString()).split("-");
        int cd = Integer.parseInt(dt[0]);
        int cy = Integer.parseInt(dt[2]);
        int cm = Integer.parseInt(dt[1]);
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);
        int day = Integer.parseInt(date[0]);
        TextView txt = (TextView) findViewById(R.id.usage);
        txt.setText("Elections in your area will start soon!");
        if (cm == month && cy == year && day - cd <= 3 && day - cd > 0) {
            blink();
        }
        if (cm == month && cy == year &&  day - cd == 0) {
            txt.setText("Elections in your area started!");
        }
        else if(cy>year||cm>month||(cd>day&&cm>=month)){
            txt.setVisibility(View.VISIBLE);
            txt.setText("Election Date Passed");
        }
    }







    public void getDate(){
        DocumentReference user = db.collection("Election_Day").document(LoginActivity.CONSTITUENCY);
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    edate=doc.get("Election_day").toString();
                    Log.d("election date",edate);
                    getServerDateAndTime();
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(VoterProfileActivity.this, "Election date not set yet",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getServerDateAndTime(){
        DocumentReference user = db.collection("loginp").document("DateAndTime");
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    cdate=doc.get("date and time").toString();
                    System.out.println("current date"+cdate);
                    if(voteflag==false){
                    ReadFromDb();
                    startTimer();}
                    else{
                        voteflag=false;
                        methodxyz();
                    }
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("server date not fetched");
                        Toast.makeText(VoterProfileActivity.this, "Election date not set yet",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Blink
    private void blink() {
        //blink_status=true;
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.usage);
                        if (txt.getVisibility() == View.VISIBLE) {
                            txt.setVisibility(View.INVISIBLE);
                        } else {
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    //Timer
    public void startTimer(){
        String arr[]=cdate.split(" ");
        String output =arr[1];
        String time[]=output.split(":");
        hour=Integer.parseInt(time[0]);
        int mins=Integer.parseInt(time[1]);
        int secs=Integer.parseInt(time[2]);
        int daydiff=0;
        /* On day of election */
        fdate=arr[0];
        Log.d("date ",edate.equals(fdate)+" "+fdate);
        try{
            d1=rdf1.parse(fdate);
        }
        catch(ParseException p){
            Log.d("parse exception",p.getMessage());
        }
        try{
            d2=rdf1.parse(edate);
        }
        catch(ParseException p){
            Log.d("parse exception",p.getMessage());
        }
        System.out.println("hour value"+hour+" "+(hour>=7&&hour<24));
        if(edate.equals(fdate))
        {
            if(hour>=7&&hour<17) {
                timer.setVisibility(View.VISIBLE);
                int rh = (17 - hour) - 1;
                int rm = 60 - mins - 1;
                int rs = 60 - secs;
                rt = (long) (rh * 60 * 60 + rm * 60 + rs);
                timer();
            }
        }
        if((edate.equals(fdate)&&hour>=17)||(d1.compareTo(d2)>0&&!edate.equals(fdate)))
        {
            Log.d("hour past 7",(edate.equals(fdate)&&hour>19)+"");
            Log.d("today date",d1.toString());
            Log.d("election date",d2.toString());
            Log.d("date after election",(d1.compareTo(d2)>0)+"");
            result.setVisibility(View.VISIBLE);
        }
    }

    public void timer(){
        new CountDownTimer(rt*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int cdtime=(int)(millisUntilFinished/1000);
                int days=(int)(cdtime/ 86400);
                // Log.d("days rema",days+""+cdtime/ 86400);
                int rh=(int)(cdtime%86400);
                int hours=(int)(rh/3600);
                int rs=rh%3600;
                int mins= rs/60;
                int secs=rs%60;
                timer.setText("Voting completes in: " +hours+":"+mins+":"+secs);
            }

            public void onFinish() {
                timer.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void click(View v){
        Log.d("constituency",LoginActivity.CONSTITUENCY);
        DocumentReference user = db.collection("Election_Day").document(LoginActivity.CONSTITUENCY);
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    // election_date=doc.get("Election_day").toString();
                    Winner=doc.get("Winner").toString();
                    getDate();
                    String arr[]=cdate.split(" ");
                    String output =arr[1];
                    String time[]=output.split(":");
                    hour=Integer.parseInt(time[0]);
                    Log.d("election date",edate);
                    Log.d("today's date",fdate);
                    if((edate.equals(fdate)&&hour>=20)||(d1.compareTo(d2)>0&&!edate.equals(fdate)))
                    {
                        if(Winner.equals("No winner")){
                            String message="No Majority";
                            it=new Intent(VoterProfileActivity.this,NoMajority.class);
                            it.putExtra("mess",message);
                        }
                        else{
                            it=new Intent(VoterProfileActivity.this,ResultActivity.class);
                        }
                    }
                    else{
                        String message="Results to be declared yet!";
                        it=new Intent(VoterProfileActivity.this,NoMajority.class);
                        it.putExtra("mess",message);
                    }
                    startActivity(it);
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(VoterProfileActivity.this, "Election day not set yet",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

}
