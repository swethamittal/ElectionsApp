package app.amazon.integratedapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AutoFillActivity extends AppCompatActivity {

    String aadhar;
    EditText aadharNumber;
    EditText name;
    EditText father_name;
    EditText dob;
    EditText Mobile_no;
    EditText Hno;
    EditText Area;
    EditText District;
    EditText State;
    EditText Constituency;
    RadioButton male;
    RadioButton female;
    RadioButton others;
    EditText Street;
    String myString;
    TextView final_text;
    int back=0;
    int flag=0;
    ImageView image;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot doc;
    FirebaseStorage storage;
    StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        boolean flag=false;
        final_text=(TextView)findViewById(R.id.final_result);
        Intent in = getIntent();
        aadhar = in.getStringExtra("aadhar");
        myString=in.getStringExtra("type_of_user");
        System.out.println("autofill temp"+myString);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        DocumentReference user = db.collection("citizen").document(aadhar);
        user.get().addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
            @Override
            public void onComplete( Task < DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    if (doc.exists()) {
                        autofill();
                    }
                    else{
                        Toast.makeText(AutoFillActivity.this, "incorrect aadhar",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AutoFillActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(AutoFillActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AutoFillActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
    public void password(View v)
    {
        if(flag==1){
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra("aadhar",aadhar);
            intent.putExtra("type_of_user",myString);
            intent.putExtra("constituency",Constituency.getText().toString());
            //final_text.setText("SUCCESSFUL!");
            startActivity(intent);
        }
        else{
            //final_text.setText("FAILED!",getColor(256));
            //final_text.setText("FAILED!");
            Toast toast=Toast.makeText(getApplicationContext(),"Agreement required! Please check the check box ",Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void autofill(){
        setContentView(R.layout.activity_autofill);
        //  TextView text =(TextView) findViewById(R.id.link);
        image=(ImageView)findViewById(R.id.image);
        loadimage();
        aadharNumber = (EditText) findViewById(R.id.adhaarNumber);
        name = (EditText) findViewById(R.id.name);
        father_name = (EditText) findViewById(R.id.father_name);
        dob = (EditText) findViewById(R.id.dob);
        Mobile_no = (EditText) findViewById(R.id.Mobile_no);
        Hno = (EditText) findViewById(R.id.Hno);
        Area = (EditText) findViewById(R.id.Area);
        District = (EditText) findViewById(R.id.District);
        State = (EditText) findViewById(R.id.State);
        Constituency = (EditText) findViewById(R.id.Constituency);
        Street = (EditText) findViewById(R.id.Street);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        others = (RadioButton) findViewById(R.id.other);
        aadharNumber.setText((Long) doc.get("Adharcard_number") + "");
        Area.setText((String) doc.get("Area"));
        Constituency.setText((String) doc.get("Assembly_constituency"));
        dob.setText((String) doc.get("DOB"));
        District.setText((String) doc.get("District_name"));
        Hno.setText((String) doc.get("H_NO"));
        father_name.setText((String) doc.get("Husband_Father name"));
        name.setText((String) doc.get("Name"));
        Mobile_no.setText((Long) doc.get("Mobile_number") + " ");
        State.setText((String) doc.get("State"));
        if (((String) doc.get("Gender")).equals("Female")) {
            female.setChecked(true);
        } else if (((String) doc.get("Gender")).equals("Male")) {
            male.setChecked(true);
        } else {
            others.setChecked(true);
        }
        Street.setText((String) doc.get("Street"));
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
                Glide.with(AutoFillActivity.this)
                        .load(url)
                        .into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void selectItem(View view)
    {
        boolean checked=((CheckBox) view).isChecked();
        if(checked==true)
        {
            flag=1;
            // intentChange2(view);
        }
        else {
            Toast toast=Toast.makeText(getApplicationContext(),"please check the check box ",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void finalSelection(View view)
    {
        if(flag==1 && back==0)
        {
            Intent intent = new Intent(this, PasswordActivity.class);
            final_text.setText("Successful!");
            startActivity(intent);
        }
        else
        {
            Toast toast=Toast.makeText(getApplicationContext(),"please check the check box ",Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    @Override
    public void onBackPressed() {
        flag=0;
        back=1;
    }

}



