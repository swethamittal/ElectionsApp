package app.amazon.integratedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.*;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.widget.Toast;
import android.util.Log;

public class PasswordActivity extends AppCompatActivity {

    String aadhar;
    EditText pwd1;
    EditText pwd2;
    String myString;
    String constituency;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pwd1=(EditText)findViewById(R.id.password);
        pwd2=(EditText)findViewById(R.id.re_password);
        Intent in = getIntent();
        constituency=in.getStringExtra("constituency");
        aadhar = in.getStringExtra("aadhar");
        myString=in.getStringExtra("type_of_user");
        System.out.println("password temp"+myString);
    }
    public void check(View v){
        final String TAG = "PasswordActivity";
        String pw1=pwd1.getText().toString();
        String pw2=pwd2.getText().toString();
        if(pw1.equals(pw2)){
            Log.d(TAG,pw2);
            Map<String,String> pwd=new HashMap<>();
            String hex= encrypt(pw1);
            Map<String,String> map=new HashMap<>();
            map.put("Password",hex+"");
            map.put("User_type",myString);

            db.collection("citizen").document(aadhar).set(map, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PasswordActivity.this, "Your password is set! Registeration completed.Please login",Toast.LENGTH_SHORT).show();
                            updateVotersRegistered();
                            Intent intent = new Intent(PasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {

                            Toast.makeText(PasswordActivity.this, "Oops something went wrong. Try again!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    });
        }
        else{
            Log.d(TAG,"password not matched");
            Toast.makeText(PasswordActivity.this, "Password mismatch,enter password again",Toast.LENGTH_SHORT).show();
            pwd1.setText("");
            pwd2.setText("");
        }
    }
    public void updateVotersRegistered(){
        DocumentReference user = db.collection("Election_Day").document(constituency);
        user.get().addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
            @Override
            public void onComplete( Task < DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        updateCount((Number)doc.get("Voters_registered"));
                    }

                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }

    public void updateCount(Number vr){
        HashMap<String,Number> h=new HashMap<String,Number>();
        long ivr=(long)vr;
        h.put("Voters_registered",(Number)(ivr+1));
        db.collection("Election_Day").document(constituency).set(h, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PasswordActivity.this, "success",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(PasswordActivity.this, "failure",
                                Toast.LENGTH_SHORT).show();
                    }

                });

    }

    public void back(View v){
        finish();
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

