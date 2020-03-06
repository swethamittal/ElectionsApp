package app.amazon.integratedapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;

public class VerifyOTPForForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button verify;
    private EditText phone;
    private String verificationCode;
    private EditText eotp;
    String aadhar,reason;
    private String phoneNumber = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "Code sent"+verificationCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "verification fialed", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_for_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        verify = (Button) findViewById(R.id.butver);
        Intent in=getIntent();
        eotp=(EditText)findViewById(R.id.otp);
        aadhar=in.getStringExtra("aadhar");
        //reason=in.getStringExtra("reason");
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
                            phoneNumber = "+91" + (Long) doc.get("mobile");
                            Toast.makeText(VerifyOTPForForgotPasswordActivity.this, phoneNumber,

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
                VerifyOTPForForgotPasswordActivity.this,        // Activity (for callback binding)
                mCallback);// OnVerificationStateChangedCallbacks
        Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "phone verification",

                Toast.LENGTH_SHORT).show();
    }

    //getting the code entered by the user manually and calling the verification function.
    public void verify(View v) {
        String code = eotp.getText().toString();
        if(code.equals(""))
        Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "Please enter a otp", Toast.LENGTH_SHORT).show();
        else
        verifyCode(code);
    }

    //verifying the code entered by user and displaying the registration screen if the code is valid.
    public void verifyCode(String code){
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verificationCode,code);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(VerifyOTPForForgotPasswordActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(VerifyOTPForForgotPasswordActivity.this, ChangePasswordActivity.class);
                    in.putExtra("aadhar",aadhar);
                    startActivity(in);
                }
                else
                {
                    Toast.makeText(VerifyOTPForForgotPasswordActivity.this, "Incorrect code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


