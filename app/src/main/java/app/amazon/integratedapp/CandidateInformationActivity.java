package app.amazon.integratedapp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class CandidateInformationActivity extends AppCompatActivity {

    String  aadhar,name,url;
    String constituency;
    String party;
    TextView tc;
    TextView tp;
    TextView tn;
    TextView birthdate;
    TextView address;
    ImageView image;
    FirebaseFirestore db;
    String promises[];
    ExtractPromisesActivity adapter;
    ExpandableHeightGridViewActivity grid;
    ExpandableHeightGridViewActivity histgrid;
    ExpandableHeightGridViewActivity rfgrid;
    static final String TAG="candidateInfo";
    FirebaseStorage storage;
    StorageReference storageRef;
    RatingBar rb;
    EditText feedback;
    ArrayList<String> fblist;ArrayList<String> consti;
    ArrayList<String> ratingList;
    HashMap<String,String> h;
    HashMap<String,HashMap<String,String>> hmap;
    int rating;
    String fb;
    RatingFeedbackActivity rfAdapter;
    String dob,place;
    GlobalsActivity g;
    ExtractPromisesActivity histAdapter;
    String history[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_information);
        Intent it=getIntent();
        aadhar=it.getStringExtra("aadhar");
        tn=(TextView)findViewById(R.id.candidate);
        name=it.getStringExtra("name");
        image=(ImageView)findViewById(R.id.image);
        url=it.getStringExtra("url");
        tc=(TextView)findViewById(R.id.Constituency);
        constituency=it.getStringExtra("constituency");
        tp=(TextView)findViewById(R.id.party) ;
        party=it.getStringExtra("party");
        birthdate=(TextView)findViewById(R.id.dob);
        address=(TextView)findViewById(R.id.residence);
        promises=it.getStringExtra("promises").split("\\+");
        history=it.getStringExtra("history").split("\\+");
        g=(GlobalsActivity) getApplicationContext();
        info();
    }


    //getting dob and address of candidate from the database
    public void info(){
        DocumentReference rf = db.collection("citizen").document(aadhar.trim());
        rf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    dob=doc.getString("DOB");
                    place=doc.getString("Street")+","+doc.getString("State");
                    fill();
                }
            }
        });
    }

    //filling the candidate _info layout values
    public void fill(){
        tn.setText(name);
        Glide.with(this)
                .load(url)
                .into(image);
        tc.setText(constituency);
        tp.setText(party);
        birthdate.setText(dob);
        address.setText(place);
        //custom grid for promises and history section
        histAdapter=new ExtractPromisesActivity(CandidateInformationActivity.this,history);
        histgrid=(ExpandableHeightGridViewActivity) findViewById(R.id.history);
        histgrid.setAdapter(histAdapter);
        histgrid.setExpanded(true);
        adapter=new ExtractPromisesActivity(CandidateInformationActivity.this,promises);
        grid = (ExpandableHeightGridViewActivity) findViewById(R.id.promisegrid);
        grid.setAdapter(adapter);
        grid.setExpanded(true);
        rb=(RatingBar)findViewById(R.id.ratingBar);
        feedback=(EditText)findViewById(R.id.feedback);
        fblist=new ArrayList<>();
        ratingList=new ArrayList<>();
        consti=new ArrayList<>();
        //getting feedback and rating of a particular candidate from database
        DocumentReference rf = db.collection("Candidate").document(aadhar.trim());
        rf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    h = (HashMap<String, String>) doc.getData().get("Rating_feedback");
                    getRatings();
                }
            }
        });
    }


    //on click listener for download affidavit button
    public void affidavit(View v) {
        download("affidavit");
    }


    //method for downloading pdf from firebase
    public void download(final String type){
        StorageReference pdfRef=storageRef.child("elections/documents/" + type + "/" + aadhar.trim() + ".pdf");
        Toast.makeText(CandidateInformationActivity.this, "Registeration completed"+pdfRef.getBucket(),Toast.LENGTH_SHORT).show();
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(CandidateInformationActivity.this, "starting file download",Toast.LENGTH_SHORT).show();
                String url=uri.toString();
                downloadFiles(CandidateInformationActivity.this,name+"_Affidavit",".pdf",DIRECTORY_DOWNLOADS,url);
                Toast.makeText(CandidateInformationActivity.this, "downloading completed",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    //Assigns the task of downloading pdf to the Android device manager by providing it the download url
    public void downloadFiles(Context context, String fileName, String fileEx, String desDirec, String url){
        DownloadManager downloadManager=(DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,desDirec,fileName+fileEx);
        downloadManager.enqueue(request);
    }

    //on click listener for rating submit button
    public void rating(View v) {
        rating = (int) rb.getRating();
        fb = feedback.getText().toString().equals("")?"no feedback":feedback.getText().toString();
        h.put(aadhar.trim(), rating + "_" +g.userconsti+"_"+fb);
        hmap = new HashMap();
        hmap.put("Rating_feedback", h);
        update();
    }


    //update database on rating and feedback submissions
    public void update(){
        db.collection("Candidate").document(aadhar.trim()).set(hmap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CandidateInformationActivity.this, "Rating and feedback submitted",Toast.LENGTH_SHORT).show();
                        if(rfAdapter!=null) {
                            rfAdapter.clearData();
                        }
                        getRatings();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(CandidateInformationActivity.this, "Oops something went wrong while uploading to database",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void getRatings(){
        int i=0;
        for(String key:h.keySet()){
            String values[]=h.get(key).split("_");
            ratingList.add(i,values[0]);
            consti.add(i,values[1]);
            if(values[2].equals("no feedback")){
                fblist.add(i,"");
            }
            else
                fblist.add(i,values[2]);
            i++;
            if(h.size() == i){
                //creating a grid view for rating and feedback
                rfAdapter=new RatingFeedbackActivity(CandidateInformationActivity.this,ratingList,fblist,consti);
                rfgrid = (ExpandableHeightGridViewActivity) findViewById(R.id.ratingfeedbackgrid);
                rfgrid.setAdapter(rfAdapter);
                rfgrid.setExpanded(true);
            }
        }
        rb.setRating(0.0f);
        feedback.setText("");
    }

}

