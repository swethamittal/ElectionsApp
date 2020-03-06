package app.amazon.integratedapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class CandidateRegistrationActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{


    final Context context = this;
    FirebaseStorage storage;
    StorageReference storageRef;
    String TAG,aadhar;
    EditText aadharNumber,name,father_name,dob,Constituency;
    RadioButton male,female,others;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;
    int CODE=215;
    String type;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_registration);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        TAG="MainActivity";
        Intent in=getIntent();
        aadhar=in.getStringExtra("aadhar");
        aadharNumber = (EditText) findViewById(R.id.adhaarNumber);
        name = (EditText) findViewById(R.id.name);
        father_name = (EditText) findViewById(R.id.father_name);
        dob = (EditText) findViewById(R.id.dob);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        others = (RadioButton) findViewById(R.id.other);
        Constituency = (EditText) findViewById(R.id.Constituency);
        image=(ImageView)findViewById(R.id.image);
        if(haveNetworkConnection()) {
            loadimage();
            autofill();
        }
        else{
            Toast.makeText(CandidateRegistrationActivity.this, "Upload Failed,please try again...make sure you are connected to internet", Toast.LENGTH_SHORT).show();
        }
    }


    //autofilling details
    public void autofill(){
        DocumentReference user = db.collection("citizen").document(aadhar);
        user.get().addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
            @Override
            public void onComplete( Task < DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    aadharNumber.setText((Long) doc.get("Adharcard_number") + "");
                    father_name.setText((String) doc.get("Husband_Father name"));
                    name.setText((String) doc.get("Name"));
                    dob.setText((String) doc.get("DOB"));
                    Constituency.setText((String)doc.get("Assembly_constituency"));
                    if (((String) doc.get("Gender")).equals("Female")) {
                        female.setChecked(true);
                    } else if (((String) doc.get("Gender")).equals("Male")) {
                        male.setChecked(true);
                    } else {
                        others.setChecked(true);
                    }

                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(CandidateRegistrationActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CandidateRegistrationActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    public void loadimage(){
        StorageReference pdfRef=storageRef.child("elections/images/citizens/"+aadhar+".jpg");
        if(pdfRef==null){
            Log.d("child ref","null");
        }
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                Glide.with(CandidateRegistrationActivity.this)
                        .load(url)
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }



    //onclick method for upload button
    public void uppop(View v){
        PopupMenu popup=new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.activity_candidate_registration_upload_menu);
        popup.show();
    }

    //onclick method for download button
    public void dlpop(View v){
        PopupMenu popup=new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.activity_candidate_registration_download_menu);
        popup.show();

    }

    //implemnts downloading of pdf from firebase storage
    public void download(final String type){
        Log.d(TAG,"elections/"+type+".pdf");
        StorageReference pdfRef=storageRef.child("elections/"+type+".pdf");
        Log.d(TAG,pdfRef.getBucket());
        Toast.makeText(CandidateRegistrationActivity.this, "Registeration completed"+pdfRef.getBucket(),Toast.LENGTH_SHORT).show();
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(CandidateRegistrationActivity.this, "starting file download",Toast.LENGTH_SHORT).show();
                String url=uri.toString();
                if(type.equals("nomination")){
                    downloadFiles(CandidateRegistrationActivity.this,"nomination_papers",".pdf",DIRECTORY_DOWNLOADS,url);}
                else{
                    downloadFiles(CandidateRegistrationActivity.this,"affidavit",".pdf",DIRECTORY_DOWNLOADS,url);
                }
                Toast.makeText(CandidateRegistrationActivity.this, "downloading completed",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    //Assigns the task of downloading pdf to the Android device manager by providing it the download url
    public void downloadFiles(Context context,String fileName,String fileEx,String desDirec,String url){
        DownloadManager downloadManager=(DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,desDirec,fileName+fileEx);
        downloadManager.enqueue(request);
    }



    // method for choose file on selecting options from upload pop up menu
    public void selectFile ()
    {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i,"Select a file"), CODE);
    }

    //exexuted when a file from the system is selected or not selected
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            String filePath = data.getDataString();
            Uri SelectedFileLocation = Uri.parse(filePath);
            UploadFile(SelectedFileLocation);
            super.onActivityResult(requestCode, resultCode, data);
        }
        else{
            Toast.makeText(this, "Please choose a file", Toast.LENGTH_SHORT).show();
        }
    }


    //uploading the selected file on firebase storage
    public  void UploadFile(Uri file) {
        if (haveNetworkConnection()) {
            Toast.makeText(this, "Please wait.. the file is uploading!", Toast.LENGTH_SHORT).show();
            //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            StorageReference riversRef;
            if (type.equals("affidavit")) {
                riversRef = storageRef.child("elections/documents/" + type + "/" + aadhar + ".pdf");
            } else {
                riversRef = storageRef.child("elections/documents/" + type + "/" + aadhar + ".pdf");
            }
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadFileDatabase(type);
                            Toast.makeText(CandidateRegistrationActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(CandidateRegistrationActivity.this, "Upload Failed,please try again...make sure you are connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(CandidateRegistrationActivity.this, "Upload Failed,please try again...make sure you are connected to internet", Toast.LENGTH_SHORT).show();
        }
    }


    //making entry in the firestore if a particular file is uploaded successfully.
    public void uploadFileDatabase(String type){
        Map<String,String> details=new HashMap<>();
        details.put(type+"","set");

        db.collection("Candidate").document(aadhar).set(details, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CandidateRegistrationActivity.this, "updating database completed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(CandidateRegistrationActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CandidateRegistrationActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                });
    }


    //defining click events on popup menu items based on the id of the item
    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.affidavit:
                type="affidavit";
                selectFile();
                break;
            case R.id.nomination:
                type="nomination";
                selectFile();
                break;
            case R.id.dow_affidavit:
                download("affidavit");
                break;
            case R.id.dow_nomination:
                download("nominations");
                break;
        }
        return true;
    }


    //click method for register button
    public void register(View v){
        DocumentReference user = db.collection("Candidate").document(aadhar);
        //Verfying whether the candidate has submited both nomination and affidavit paper for successful registeration
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot  doc = task.getResult();
                    if (doc.exists()) {
                        boolean aff=doc.get("affidavit")!=null?doc.get("affidavit").equals("set"):false;
                        boolean nom=doc.get("nomination")!=null?doc.get("nomination").equals("set"):false;
                        if(aff==false&&nom==false){
                            Toast.makeText(CandidateRegistrationActivity.this, "Please upload affidavit and nomination papers to complete registration",Toast.LENGTH_SHORT).show();
                        }
                        else if(aff==false){
                            Toast.makeText(CandidateRegistrationActivity.this, "Please upload affidavit to complete registration",Toast.LENGTH_SHORT).show();
                        }
                        else if(nom==false){
                            Toast.makeText(CandidateRegistrationActivity.this, "Please upload nomination papers to complete registration",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(CandidateRegistrationActivity.this, "Registeration request submited",Toast.LENGTH_SHORT).show();
                            uploadDatabase();
                            Intent intent = new Intent(CandidateRegistrationActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(CandidateRegistrationActivity.this, "Oops something went wrong while submmiting registration,please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //to update the user type from voter to citizen
    public void uploadDatabase(){
        Map<String,String> details=new HashMap<>();
        details.put("Assembly_Constituency",Constituency.getText().toString());
        details.put("status","To_be_reviewed");
        db.collection("Candidate").document(aadhar).set(details, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CandidateRegistrationActivity.this, "updating database completed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(CandidateRegistrationActivity.this, "Oops something went wrong while uploading to database",
                                Toast.LENGTH_SHORT).show();
                    }

                });
        details.clear();
        details.put("User_type","Candidate");
        db.collection("citizen").document(aadhar).set(details, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CandidateRegistrationActivity.this, "updating database completed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(CandidateRegistrationActivity.this, "Oops something went wrong while uploading to database",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private boolean haveNetworkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
