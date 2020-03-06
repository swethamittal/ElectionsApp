package app.amazon.integratedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText pwd;
    EditText pwd1;
    static String adhaar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_change_password );
        pwd = findViewById( R.id.pwd );
        pwd1 = findViewById( R.id.pwd1 );
        Button but=findViewById( R.id.ch );
        Intent in=getIntent();
        adhaar=in.getStringExtra("aadhar");
    }


    public void change(View v) {
        String pw=pwd.getText().toString().trim();
        String pw1 = pwd1.getText().toString().trim();
        if(pw.equalsIgnoreCase(pw1)){
        Map<String, String> pmap = new HashMap<>();
        String hex= encrypt(pw1);
        pmap.put( "Password", hex+"" );
        db.collection( "citizen" ).document(adhaar).set( pmap, SetOptions.merge() )
                .addOnSuccessListener( new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d( "succ", "succesful" );
                        Intent i=new Intent(ChangePasswordActivity.this,LoginActivity.class);
                        startActivity( i );
                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d( "fail", "failed" );
                    }

                } );
    }
        else{
            Toast.makeText(ChangePasswordActivity.this,"Password Mismatch",Toast.LENGTH_SHORT).show();
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

