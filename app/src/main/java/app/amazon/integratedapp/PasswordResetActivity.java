package app.amazon.integratedapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PasswordResetActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String aadhar;
    EditText et;
    DocumentReference noteRef = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_password_reset );
        et = findViewById( R.id.et1 );
        Button og = findViewById( R.id.otpgen );
    }

    public void otpg(View v) {
        aadhar = et.getText().toString().trim();;
        noteRef = db.document( "citizen/"+aadhar );
        noteRef.get() .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String pass=documentSnapshot.getString("Password");
                    String utype=documentSnapshot.getString("User_type");
                    if(pass == null & utype == null) {
                        Toast.makeText(PasswordResetActivity.this,"Not a registered user",Toast.LENGTH_SHORT).show();
                    }
                    else if(utype==null) {
                        Toast.makeText(PasswordResetActivity.this,"PLease register online",Toast.LENGTH_SHORT ).show();
                    }
                    else {
                        Intent i=new Intent( PasswordResetActivity.this,VerifyOTPForForgotPasswordActivity.class);
                        i.putExtra("aadhar",aadhar);
                        startActivity(i);
                    }

                } else {
                    Log.d("error","errpr");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, e.toString());
                    }
                });
    }
}