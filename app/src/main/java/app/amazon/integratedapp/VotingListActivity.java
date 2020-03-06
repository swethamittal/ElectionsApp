package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VotingListActivity extends AppCompatActivity {

    GridView grid;
    private View b;
    private int longClickDuration = 5000;
    private boolean isLongPress = false;
    private ProgressBar pB;
    static  int i;
    private int mProgressStatus=0;
    int flag=0;
    Handler handler;
    ArrayList<String> web;
    ArrayList<Integer> ImageId;
    CustomGridActivity adapter;
    ArrayList<Number> aadhar=new ArrayList<>();
    int pos1=-1;
    String AssemblyConstituency;
    int currvotes=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_list);
        web=new ArrayList<>();
        ImageId=new ArrayList<>();
        AssemblyConstituency=LoginActivity.CONSTITUENCY;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Candidate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.getString("Assembly_Constituency").equals(AssemblyConstituency)) {
                                    web.add(document.getString("Name"));
                                    aadhar.add((Number)document.get("Aadharcard_number"));
                                    String party=document.getString("party_name").toLowerCase();
                                    if(party.equalsIgnoreCase("INC"))
                                        ImageId.add(R.drawable.congress);
                                    else if(party.equals("aap"))
                                        ImageId.add(R.drawable.aap);
                                    else if(party.equals("bjp"))
                                        ImageId.add(R.drawable.bjp);
                                    else if(party.equals("bsp"))
                                        ImageId.add(R.drawable.bsp);
                                    else if(party.equals("sp"))
                                        ImageId.add(R.drawable.sp);
                                    else if(party.equals("trs"))
                                        ImageId.add(R.drawable.trs);
                                    else if(party.equals("tdp"))
                                        ImageId.add(R.drawable.tdp);
                                    else if(party.equals("nyp"))
                                        ImageId.add(R.drawable.nyp);
                                }
                            }
                            aadhar.add((Number)1);
                            web.add("None Of The Above");
                            ImageId.add(R.drawable.notael);
                            adapter = new CustomGridActivity(VotingListActivity.this, web, ImageId);
                            grid = (GridView) findViewById(R.id.grid);
                            grid.setAdapter(adapter);
                            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                                    long viewId = view.getId();

                                    if (view instanceof  Button) {
                                        //Toast.makeText(MainActivity.this, "Button clicked"+" "+position, Toast.LENGTH_SHORT).show();
                                        b= view;
                                        int pos= CustomGridActivity.buttonsobj.indexOf(view);
                                        pB=CustomGridActivity.progressBarsobj.get(pos);
                                        b.setPressed(true);
                                        pos1=position;
                                        view.post(rotationRunnable);
                                    }else {
                                        //Toast.makeText(MainActivity.this, "ListView clicked", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            if (b.isPressed()) {
                try {
                    if (mProgressStatus < 100) {
                        Thread.sleep(0);

                        mProgressStatus = doWork();
                        pB.setProgress(mProgressStatus);
                    }
                    if (mProgressStatus == 100) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Election_Day")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if(document.toString().contains(AssemblyConstituency)) {
                                                    String str = document.get("Candidate_list") + "";
                                                    Log.d("candidate list",str);
                                                    //Log.d("Str",str);
                                                    String[] arr=str.substring(1,str.length()-1).split(",");
                                                    Map<String,String> map = new HashMap<>();
                                                    for(int i=0;i<arr.length;i++)
                                                    {
                                                        //Log.d("String",arr[i].trim());
                                                        Log.d("triming aadhar ",arr[i]+" "+(arr[i].indexOf("=")+1));
                                                        arr[i]=arr[i].trim();
                                                        ConverterActivity c=new ConverterActivity();
                                                        currvotes=c.getDecimal(arr[i].substring(arr[i].indexOf("=")+1));
                                                        Log.d("aadhar num",""+aadhar.get(pos1));
                                                        String numb=""+aadhar.get(pos1);
                                                        Log.d("value ",numb+" "+arr[i].contains(numb.equals("1")?"NOTA":(aadhar.get(pos1)+"")));
                                                        if(arr[i].contains(numb.equals("1")?"NOTA":(aadhar.get(pos1)+"")))
                                                        {
                                                            currvotes+=1;
                                                            if(currvotes==1){
                                                                map.put(arr[i].substring(0,arr[i].indexOf("=")),c.toHex(currvotes+536870911));
                                                            }
                                                            else
                                                                map.put(arr[i].substring(0,arr[i].indexOf("=")),c.toHex(currvotes));

                                                        }
                                                        else
                                                        {
                                                            if(currvotes==0){
                                                                map.put(arr[i].substring(0,arr[i].indexOf("=")),"1FFFFFFF");
                                                            }
                                                            map.put(arr[i].substring(0,arr[i].indexOf("=")),c.toHex(currvotes));
                                                        }
                                                    }
                                                    Map<String,Object> userMap = new HashMap<>();
                                                    userMap.put("Candidate_list",map);
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    db.collection("Election_Day")
                                                            .document(AssemblyConstituency)
                                                            .update(userMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //LOG.info("Success");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    //LOG.error("Failure "+e.toString());
                                                                }
                                                            });
                                                    //FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    Bundle bundle = getIntent().getExtras();
                                                    String aadharcard = bundle.getString("aadharcard");
                                                    Log.d("aadhar",aadharcard);
                                                    Map<String,Object> maketrue = new HashMap<>();
                                                    Log.d("updating vote status","true");
                                                    maketrue.put("Vote_Status",true);
                                                    db.collection("citizen")
                                                            .document(aadharcard)
                                                            .update(maketrue)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //LOG.info("Success");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    //LOG.error("Failure "+e.toString());
                                                                }
                                                            });
                                                }
                                            }
                                        } else {
                                            //Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                        method();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                b.postDelayed(rotationRunnable, 40);
            }
            else
            {
                mProgressStatus = 0;
                pB.setProgress(mProgressStatus);
            }
        }
    };
    void method()
    {
        Intent intent = new Intent(this, ThanksForVotingActivity.class);
        startActivity(intent);
        finish();
    }
    private int doWork() {
        // TODO Auto-generated method stub
        Timer t=new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mProgressStatus=mProgressStatus+1;
            }
        }, 100);
        return mProgressStatus;
    }
}