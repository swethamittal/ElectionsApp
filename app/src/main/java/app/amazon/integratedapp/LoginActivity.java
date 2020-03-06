package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {
    public static String name;
    private static String LOG_TAG ="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String AADHAR="";
    public static String partyname="";
    public static String CONSTITUENCY="";
    public static String PARTYNAME="";
    public static String PARTYNO="";
    public static String SINO="";
    public static String NAME="";
    public static String AREA="";
    public static String PASSWORD="";
    public static String FATHER="";
    public static String DOB="";
    public static String MOBILE="";
    public static String GENDER="";
    public static String HNO="";
    public static String STREET="";
    public static String DISTRICT="";
    public static String STATE="";

    EditText type;
    EditText aadhar;
    Intent in;
    String ut="";
    String pwd="";
    String status;
    String message;
    private Button login_button,reg_button;
    EditText username;
    EditText getaadhar;
    EditText password;
    EditText et;
    Spinner spinner;
    static String temp="";
    String HEX1;
    String HEX2;
    Intent intent;
    static TextView pr;
    DocumentSnapshot doc;
    DocumentReference noteRef=null;
    String candpwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById( R.id.enter1 );
        password = (EditText) findViewById( R.id.enter2);
        getaadhar = (EditText) findViewById( R.id.enter_adhar_for_reg);

        login_button = (Button) findViewById(R.id.login_button);
        reg_button = (Button) findViewById(R.id.reg_button);

        type=(EditText)findViewById(R.id.type);
        aadhar=(EditText)findViewById(R.id.enter_adhar_for_online_reg);
        in=new Intent(LoginActivity.this,OTPActivity.class);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getaadhar.getText().toString().equals(""))
                check();
                else
                    Toast.makeText(LoginActivity.this, "Enter aadhar",
                            Toast.LENGTH_SHORT).show();
            }
        });

        pr =findViewById(R.id.fp);
        pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,PasswordResetActivity.class );
                startActivity( i );
            }
        });
    }


    public void check(){
        DocumentReference user = db.collection("citizen").document(getaadhar.getText().toString());
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    if (doc.exists()) {

                        in = new Intent(getApplicationContext(), OTPActivity.class);
                        spinner = (Spinner)findViewById(R.id.spinner_voter_candidate);
                        final String myString = String.valueOf(spinner.getSelectedItem());
                        in.putExtra("aadhar", getaadhar.getText().toString());
                        System.out.println("Original spinner value is: "+myString);

                        if(myString.equals("मतदाता") || myString.equals("Voter")){
                            temp="Voter";
                        } else if(myString.equals("उम्मीदवार") || myString.equals("Candidate")){
                            temp="Candidate";
                        }
                        System.out.println("temp value:"+temp);
                        in.putExtra("type_of_user",temp);
                        ifalreadyregistered();

                    }
                    else{
                        Toast.makeText(LoginActivity.this, "incorrect aadhar",Toast.LENGTH_SHORT).show();
                    }
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(LoginActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    public void ifalreadyregistered(){
        DocumentReference user = db.collection("citizen").document(getaadhar.getText().toString());
        Task<DocumentSnapshot> documentSnapshotTask = user.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if(temp.equalsIgnoreCase("voter")){
                                if(doc.getString("User_type").equalsIgnoreCase("Voter")==false&&doc.getString("Password").equals("")){
                                    startActivity(in);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "User Registered Already",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(temp.equalsIgnoreCase("candidate")&&doc.getString("User_type").equalsIgnoreCase("Voter")){
                                startActivity(in);
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "User Registered Already",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }



    //Login check for candidate and voter
    public void Read(View v)
    {
        AADHAR=username.getText().toString().trim();
        PASSWORD = password.getText().toString().trim();
        System.out.print("password is"+PASSWORD);
        HEX1= encrypt(PASSWORD);
        spinner = (Spinner)findViewById(R.id.spinner_voter_candidate);
        final String myString = String.valueOf(spinner.getSelectedItem());

        System.out.println("#Purva Original spinner value is: "+myString);

        if(myString.equals("मतदाता") || myString.equals("Voter")){
            temp="Voter";
        } else if(myString.equals("उम्मीदवार") || myString.equals("Candidate")){
            temp="Candidate";
        }
        //if user is a voter
        if(temp.equals("Voter")&&!AADHAR.equals("")&&!password.equals("")){
            System.out.println("You are a voter!");
            PASSWORD = password.getText().toString().trim();
            HEX1= encrypt(PASSWORD);
            et = (EditText) findViewById( R.id.enter1 );
            noteRef = db.document("citizen/"+AADHAR);
            noteRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                NAME = documentSnapshot.getString( "Name" );
                                AREA = documentSnapshot.getString( "Area" );
                                FATHER = documentSnapshot.getString("Husband_Father name");
                                DOB = documentSnapshot.getString("DOB");
                                GENDER = documentSnapshot.getString("Gender");
                                HNO = documentSnapshot.getString("H_NO");
                                STREET = documentSnapshot.getString("Street");
                                DISTRICT = documentSnapshot.getString("District_name");
                                STATE = documentSnapshot.getString("State");
                                CONSTITUENCY= documentSnapshot.getString( "Assembly_constituency" );
                                HEX2 = documentSnapshot.getString("Password")+"";
                                System.out.println(HEX1+" "+HEX2);
                                if(HEX2.equals("")){
                                    Toast.makeText(LoginActivity.this, "Please register first online!", Toast.LENGTH_SHORT).show();
                                }
                                else if(HEX1.equals(HEX2)){
                                    Intent intent = new Intent( LoginActivity.this, VoterProfileActivity.class );
                                    spinner = (Spinner)findViewById(R.id.spinner_voter_candidate);
                                    final String myString = String.valueOf(spinner.getSelectedItem());
                                    if(myString.equals("मतदाता") || myString.equals("Voter")){
                                        temp="Voter";
                                    } else if(myString.equals("उम्मीदवार") || myString.equals("Candidate")){
                                        temp="Candidate";
                                    }
                                    intent.putExtra("type_of_user",temp);
                                    intent.putExtra("aadhar",AADHAR);
                                    startActivity( intent );
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                }
                                //Log.d("name",Name);

                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid aadhar!", Toast.LENGTH_SHORT).show();
                            }
                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else if(temp.equals("Candidate")&&!AADHAR.equals("")&&!password.equals("")){
                //get pwd from citizen database if user is a candidate
                getCandidatePwd();
        }
        else{
            Toast.makeText(LoginActivity.this, "Enter the correct details!", Toast.LENGTH_SHORT).show();
        }
    }

    //pwd check function for candidate login
    public void checkPassword(String s1,String s2){
        if(s2.equalsIgnoreCase("")){
            Toast.makeText(LoginActivity.this, "Please register first as Voter online!", Toast.LENGTH_SHORT).show();
        }
        else if(s1.equals(s2)){
            if(temp.equalsIgnoreCase("Candidate")) {
                checkStatus();
            }}
        else{
            Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
        }
    }

    //see the status of candidate's application
    public void checkStatus(){
        DocumentReference user = db.collection("Candidate").document(AADHAR);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    status=doc.getString("status");
                    message=doc.getString("message");
                    Intent it=null;
                    System.out.println(status+" "+status.equalsIgnoreCase("Rejected"));
                    if(status.equalsIgnoreCase("Rejected")){
                        it=new Intent(LoginActivity.this,MessageActivity.class);
                        it.putExtra("message","Reason for rejection: "+message);
                        it.putExtra("status","Rejected");
                        startActivity(it);
                    }
                    else if(status.equalsIgnoreCase("Approved")){
                        startActivity(intent);

                    }
                    else if(status.equalsIgnoreCase("To_be_reviewed")){
                        it=new Intent(LoginActivity.this,MessageActivity.class);
                        it.putExtra("message","Application Status will be updated soon");
                        it.putExtra("status","Your Application has not been reviewed yet!");
                        startActivity(it);
                    }

                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(LoginActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    //getting candidate pwd for login
    public void getCandidatePwd(){
        AADHAR = username.getText().toString().trim();
        System.out.println("Addhar candpwd"+AADHAR);
        noteRef = db.document("citizen/"+AADHAR);
        Task<DocumentSnapshot> documentSnapshotTask = noteRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                if(doc.getString("User_type").equalsIgnoreCase("Candidate")){
                                candpwd = doc.getString("Password");
                                //login check after obtaining pwd
                                candLogin();}
                                if(doc.getString("User_type").equalsIgnoreCase("Voter")){
                                    Toast.makeText(LoginActivity.this, "Please register as candidate online,first!", Toast.LENGTH_SHORT).show();
                                }
                                if(doc.getString("User_type").equalsIgnoreCase("")){
                                    Toast.makeText(LoginActivity.this, "Please register as Voter online,first!", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Invalid aadhar", Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }

    //login check after obtaining candidate pwd
    public void candLogin(){
        AADHAR = username.getText().toString().trim();
        noteRef = db.document("Candidate/"+AADHAR);
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            NAME = documentSnapshot.getString("Name");
                            PARTYNAME = documentSnapshot.getString("party_name");
                            CONSTITUENCY= documentSnapshot.getString("Assembly_Constituency");
                            PARTYNO = documentSnapshot.getString("Part_NO");
                            SINO = documentSnapshot.getString("SI_NO");
                            HEX2 = candpwd;
                            System.out.println(HEX1+" "+HEX2);
                            intent = new Intent(LoginActivity.this, CandidateProfileActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("name",NAME);
                            extras.putString("partyname",PARTYNAME);
                            extras.putString("constituency",CONSTITUENCY);
                            extras.putString("partyno",PARTYNO);
                            extras.putString("sino",SINO);
                            extras.putString("AADHAR",AADHAR);
                            intent.putExtras(extras);
                            checkPassword(HEX1,HEX2);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid aadhar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, e.toString());
                    }
                });
    }


    public void setPwd(View v){
        if(!aadhar.getText().toString().equals("")&&!type.getText().toString().equals("")){
        phone();
        }
        else {
            Toast.makeText(LoginActivity.this, "Enter aadhar",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void phone() {
        DocumentReference user = db.collection("citizen").document(aadhar.getText().toString());
        Task<DocumentSnapshot> documentSnapshotTask = user.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                ut = doc.getString("User_type");
                                pwd = doc.getString("Password");
                                Toast.makeText(LoginActivity.this, ut + " " + pwd,

                                        Toast.LENGTH_SHORT).show();
                                check2();
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Invalid aadhar",

                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }
    public void check2(){
        System.out.println("ut"+ut);
        System.out.println("pwd"+pwd);
        String utype=type.getText().toString();
        System.out.println("utype"+utype);
        if(utype.equalsIgnoreCase("Voter")||utype.equalsIgnoreCase("Candidate")){
        if((ut.equalsIgnoreCase("Candidate")||ut.equalsIgnoreCase("Voter"))&&pwd.equals("")){
            //call otp screen
            Intent in=new Intent(LoginActivity.this,VerifyOTPForForgotPasswordActivity.class);
            in.putExtra("aadhar",aadhar.getText().toString());
            startActivity(in);
        }
        else if(ut.equalsIgnoreCase("Candidate")||ut.equalsIgnoreCase("Candidate")){
            Toast.makeText(LoginActivity.this, "Already registered online,same pwd can be used for Voter and candidate", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(LoginActivity.this, "Please ,register first", Toast.LENGTH_SHORT).show();
        } }
        else{
            Toast.makeText(LoginActivity.this, "Please,select a type first - voter/candidate?", Toast.LENGTH_SHORT).show();
        }
    }

    public String encrypt(String text){
        String cipher="";
        text=text.toLowerCase();
        System.out.println(text);
        int k=7;
        for(int i=0;i<text.length();i++){
            System.out.println("i value"+i);
            char ch=text.charAt(i);
            if(ch>='a'&&ch<='z'){
                cipher+=(char)((int)(ch+k-97)%26 +97);
            }
            else if(ch>='0'&&ch<='9'){
                cipher+=(char)((int)(ch+k-'0')%9+'0');
            }
            else{
                cipher+=ch;
            }
        }
        return cipher;
    }

}
