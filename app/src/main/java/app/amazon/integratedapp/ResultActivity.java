package app.amazon.integratedapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ResultActivity extends AppCompatActivity {
    String consti,aadhar;
    FirebaseFirestore db;
    HashMap<String,String> cList;
    Long winner;
    int votes_recieved;
    String party;
    StorageReference storageRef;
    FirebaseStorage storage;
    ImageView ImageId;
    String url;
    String person;
    TextView name,party_name,vcount;
    String edate;
    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-mm-yyyy");
    Calendar c = Calendar.getInstance();
    String fdate;
    Date d1,d2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        consti=LoginActivity.CONSTITUENCY;
        db=FirebaseFirestore.getInstance();
        aadhar=LoginActivity.AADHAR;
        ImageId=findViewById(R.id.image);
        name=findViewById(R.id.name);
        party_name=findViewById(R.id.party);
        vcount=findViewById(R.id.votes);
        /*c.setTime(new Date());
        fdate=sdf1.format(c.getTime());
        try{
            d1=sdf1.parse(fdate);
        }
        catch(ParseException p){
            Log.d("parse exception",p.getMessage());
        }
        getData();*/
        getData();
    }
    public void getData(){
        DocumentReference user = db.collection("Election_Day").document(consti);
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    edate=doc.get("Election_day").toString();
                    cList=(HashMap<String, String>)doc.get("Candidate_list");

                    Log.d("hash size",cList.size()+"");
                    calculateResult();
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(ResultActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    public void calculateResult(){
        int max=0;
        String name="";
        int sum=0;
        for(String cand:cList.keySet()) {
            ConverterActivity c=new ConverterActivity();
            int vr = c.getDecimal(cList.get(cand))-536870911;
            sum += vr;
            if (vr > max) {
                max = vr;
                name = cand;
            }
        }
        if(max>=(sum/2)&&max!=0){
            votes_recieved=max;
            if(name.equals("NOTA")==false){
                winner=Long.valueOf(name);
                getWinnerData();}
            else{
                String message="Winner of election:NOTA!Votes Recieved are "+max+"!";
                Intent it=new Intent(ResultActivity.this,NoMajority.class);
                it.putExtra("mess",message);
                startActivity(it);
                finish();
            }
        }
        else{
            winner=-1L;
            String message="No Majority!";
            Intent it=new Intent(ResultActivity.this,NoMajority.class);
            it.putExtra("mess",message);
            startActivity(it);
        }

    }

    public void updateDatabase(){
        Map<String,String> details=new HashMap<>();
        if(winner==-1L)
            details.put("Winner","No winner");
        else
            details.put("Winner",winner+"_"+votes_recieved);
        db.collection("Election_Day").document(consti).set(details, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResultActivity.this, "updating database completed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(ResultActivity.this, "Oops something went wrong while uploading to database",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void getWinnerData(){
        DocumentReference user = db.collection("Candidate").document(winner+"");
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    person=doc.get("Name").toString();
                    party=doc.get("party_name").toString();
                    Log.d("party",party+"");
                    getImage();
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(ResultActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    public void getImage(){
        Log.d("winner ",winner+"yo!");
        Log.d("bucket",storageRef.getBucket());
        StorageReference pdfRef=storageRef.child("elections/images/citizens/"+winner+".jpg");
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url=uri.toString();
                fillData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                url="https://firebasestorage.googleapis.com/v0/b/elections-37945.appspot.com/o/elections%2Fimages%2Fparty_symbol%2Fbjplogo.png?alt=media&token=616c64a0-32c7-4dac-bdf3-9ba0c2ec91b3";
                fillData();
            }
        });
    }
    public  void fillData(){
        name.setText(person+"");
        party_name.setText(party);
        Log.d("vote count view ",vcount.getText().toString());
        Log.d("count ",votes_recieved+"");
        vcount.setText(votes_recieved+"");
        Glide.with(ResultActivity.this)
                .load(url)
                .into(ImageId);
    }

}

