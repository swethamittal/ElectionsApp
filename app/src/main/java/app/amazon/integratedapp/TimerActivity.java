package app.amazon.integratedapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestore;


public class TimerActivity extends AppCompatActivity {


    String aadhar="123456789876";
    TextView timer;
    String edate="";
    Long rt;
    String consti;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        timer = (TextView) findViewById(R.id.timer);
        getConsti();
    }
    public void getConsti(){
        DocumentReference user = db.collection("citizen").document(aadhar);
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    consti=doc.get("Assembly_constituency").toString();
                    Log.d("constituency",consti);
                    getDate();
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(TimerActivity.this, "Oops something went wrong",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
    public void getDate(){
        DocumentReference user = db.collection("Election_Day").document(consti);
        user.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete( Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    edate=doc.get("Election_day").toString();
                    Log.d("election date",edate);
                    startTimer();
                }}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(TimerActivity.this, "Election date not set yet",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void startTimer(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar c = Calendar.getInstance();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        c.setTime(new Date()); // Now use today date.
//        c.add(Calendar.DATE, 5); // Adding 5 days
        String output = sdf.format(c.getTime());
        String time[]=output.split(":");
        int hour=Integer.parseInt(time[0]);
        int mins=Integer.parseInt(time[1]);
        int secs=Integer.parseInt(time[2]);
        int daydiff=0;
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        /* On day of election */
        String fdate=df.format(c.getTime());
        Log.d("date ",edate.equals(fdate)+" "+fdate);
        if(edate.equals(fdate))
        {
            timer.setVisibility(View.VISIBLE);
            if(hour>=7&&hour<=19) {
                int rh = (19 - hour) - 1;
                int rm = 60 - mins - 1;
                int rs = 60 - secs;
                rt = (long) (rh * 60 * 60 + rm * 60 + rs);
                timer();
            }
        }
    }

    public void timer(){


        new CountDownTimer(rt*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int cdtime=(int)(millisUntilFinished/1000);
                int days=(int)(cdtime/ 86400);
                Log.d("days rema",days+""+cdtime/ 86400);
                int rh=(int)(cdtime%86400);
                int hours=(int)(rh/3600);
                int rs=rh%3600;
                int mins= rs/60;
                int secs=rs%60;
                timer.setText("Voting completes in: " +hours+":"+mins+":"+secs);
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();


    }


}
