package app.amazon.integratedapp;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.Handler;
        import android.support.annotation.NonNull;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.GridView;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;
        import com.google.firebase.firestore.SetOptions;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.security.Key;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.Timer;
        import java.util.TimerTask;

        import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class  VoterCandidateListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    GridView grid;
    private View b;
    private int longClickDuration = 5000;
    private boolean isLongPress = false;
    private ProgressBar pB;
    static int i;
    private int mProgressStatus = 0;
    int flag = 0;
    Handler handler;
    ArrayList<String> web;
    ArrayList<String> ImageId;
    ArrayList<String> party;
    ArrayList<String> constituency;
    ArrayList<String> promises,history;
    CustomGridForCandidateListActivity adapter;
    ArrayList<Number> aadhar = new ArrayList<>();
    int pos1 = -1;
    String AssemblyConstituency ;
    int currvotes = 0;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_candidate_list);

        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
        Intent it=getIntent();
        AssemblyConstituency =it.getStringExtra("AssemblyConstituency");
        web = new ArrayList<>();
        party=new ArrayList<>();
        constituency=new ArrayList<>();
        ImageId = new ArrayList<>();
        promises=new ArrayList<>();
        history=new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        //FirebaseUser user = mAuth.getCurrentUser();
        getAadhar();
    }

    //gettting candiadtes belonging to one particular consti
    public void getAadhar(){
        db.collection("Candidate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if ((AssemblyConstituency.equals("*")||document.getString("Assembly_Constituency").equals(AssemblyConstituency))&&document.getString("status").equals("Approved")) {
                                    aadhar.add((Number) document.get("Aadharcard_number"));
                                    web.add(document.getString("Name"));
                                    party.add(document.getString("party_name"));
                                    constituency.add(document.getString("Assembly_Constituency"));
                                    promises.add(document.getString("oath"));
                                    history.add(document.getString("history"));
                                }
                            }
                            count(0);
                        }
                    }
                });
    }



    public void count(int i){
        if(i<aadhar.size()){
            getImages(i);
        }
        else{
            displaygrid();
        }
    }

    //getting images of the candidate
    public void getImages(int i){
        final int pos=i;
        Number caadhar=aadhar.get(i);
        Log.d("caadhar","image"+caadhar);
        StorageReference pdfRef=storageRef.child("elections/images/citizens/"+caadhar+".jpg");
        if(pdfRef==null){
            Log.d("child ref","null");
        }
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                ImageId.add(url);
                count(pos+1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ImageId.add("https://firebasestorage.googleapis.com/v0/b/elections-37945.appspot.com/o/elections%2Fimages%2Fparty_symbol%2Fbjplogo.png?alt=media&token=616c64a0-32c7-4dac-bdf3-9ba0c2ec91b3");
                displaygrid();
            }
        });
    }

    //creating a grid view of all the candidates
    public void displaygrid(){
        adapter = new CustomGridForCandidateListActivity( VoterCandidateListActivity.this, web, ImageId,party);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(adapter);
        //setting on click listener for each item in the list
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                Intent it=new Intent( VoterCandidateListActivity.this,CandidateInformationActivity.class);
                it.putExtra("aadhar",aadhar.get(position)+" ");
                it.putExtra("party",party.get(position)+"");
                it.putExtra("constituency",constituency.get(position)+"");
                it.putExtra("name",web.get(position)+"");
                it.putExtra("url",ImageId.get(position)+"");
                it.putExtra("promises",promises.get(position).equals("")?"No Promises made yet":promises.get(position)+"");
                it.putExtra("history",history.get(position).equals("")?"No History":history.get(position)+"");
                startActivity(it);
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.hom) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VoterProfileActivity");
            startActivity(intent);
        }else if (id == R.id.CL) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VoterCandidateListActivity");
            startActivity(intent);
        } else if (id == R.id.lo) {
            Intent intent = new Intent();
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            intent.putExtra( "EXIT", true );
            intent.setClassName(  VoterCandidateListActivity.this, "app.amazon.integratedapp.LoginActivity" );
            startActivity( intent );
        }
        else if (id == R.id.can) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.CandidatePromisesActivity");
            startActivity(intent);
        }
        else if (id == R.id.abu) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VotersAboutUsActivity");
            startActivity(intent);
        }
        else if (id == R.id.prof) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VoterProfileInformationActivity");
            startActivity(intent);
        }
        else if (id == R.id.rules) {
            Intent intent=new Intent();
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VotersRulesActivity");
            startActivity(intent);
        }
        else if(id ==R.id.Stats){
            Intent intent=new Intent( );
            intent.setClassName( VoterCandidateListActivity.this,"app.amazon.integratedapp.VotersStatisticsIPAddressActivity" );
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

}
