package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import com.google.firebase.firestore.FirebaseFirestore;

public class OTPActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button verify;
    private EditText phone;
    private String verificationCode;
    private EditText eotp;
    String aadhar;
    String myString="";
    private String phoneNumber = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG="OTPActivity";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                String code=phoneAuthCredential.getSmsCode();
                if(code!=null) {
                    eotp.setText(code);
                    verifyCode(code);
                }
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Log.d("TAG", "onCodeSent:" + verificationCode);
                Toast.makeText(OTPActivity.this, "Code sent"+verificationCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG,e.getStackTrace().toString());
                Log.d(TAG,e.getMessage());
                Toast.makeText(OTPActivity.this, "verification fialed", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        verify = (Button) findViewById(R.id.verify);
        Intent in=getIntent();
        aadhar=in.getStringExtra("aadhar");
        eotp=(EditText)findViewById(R.id.otp);
        myString=in.getStringExtra("type_of_user");
        System.out.println("oto temp value"+myString);
        phone();
    }

    //function to get the mobile number of the user from the aadhar and calling sendotp() method.
    public void phone() {

        DocumentReference user = db.collection("citizen").document(aadhar);
        Task<DocumentSnapshot> documentSnapshotTask = user.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            StringBuilder fields = new StringBuilder("");
                            //phoneNumber=(String)doc.get("mobile");
                            fields.append("phone: ").append(doc.get("mobile"));
                            Log.d(TAG, doc.get("mobile").toString());
                            phoneNumber = "+91" + (Long) doc.get("mobile");
                            Toast.makeText(OTPActivity.this, phoneNumber,

                                    Toast.LENGTH_SHORT).show();
                            sendOtp();
                            // aadhar.setText(fields.toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
    }
    //function to generate and send otp.
    public void sendOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                     // Phone number to verify
                0,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OTPActivity.this,        // Activity (for callback binding)
                mCallback);// OnVerificationStateChangedCallbacks
        Toast.makeText(OTPActivity.this, "phone verification",

                Toast.LENGTH_SHORT).show();
    }

    //function to store otp in the firebase.
    public void store() {
        Map<String, Object> reg = new HashMap<>();
        reg.put("user_type", "voter");
        db.collection("citizen").document(aadhar).set(reg, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OTPActivity.this, "User Registered",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(OTPActivity.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }

    //getting the code entered by the user manually and calling the verification function.
    public void verify(View v) {
        String code = eotp.getText().toString();
        if(code.equals(""))
        Toast.makeText(OTPActivity.this, "Please enter a otp", Toast.LENGTH_SHORT).show();
        else
        verifyCode(code);
    }

    //verifying the code entered by user and displaying the registration screen if the code is valid.
    public void verifyCode(String code){




        PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(verificationCode,code);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OTPActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
                    Intent intent=getIntent();
                    myString = intent.getStringExtra("type_of_user");
                    System.out.println("$$ SPINNER = "+myString);
                    if(myString.equals("Voter")){
                        Intent in = new Intent(OTPActivity.this, AutoFillActivity.class);
                        in.putExtra("aadhar", aadhar);
                        in.putExtra("type_of_user",myString);
                        startActivity(in);
                    }
                    else{
                        Intent in = new Intent(OTPActivity.this, CandidateRegistrationActivity.class);
                        in.putExtra("aadhar", aadhar);
                        in.putExtra("type_of_user",myString);
                        startActivity(in);
                    }
                }
                else
                {
                    Toast.makeText(OTPActivity.this, "Incorrect code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}