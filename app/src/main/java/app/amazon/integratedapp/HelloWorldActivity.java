package app.amazon.integratedapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static app.amazon.integratedapp.LoginActivity.AADHAR;

public class HelloWorldActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        Bundle extras = new Bundle();
        if(extras!=null) {
            //String image1=extras.getString("img")
            //String name = extras.getString( "name" );
            String name = extras.getString("name");
            String partyname = extras.getString("partyname");
            String constituency = extras.getString("constituency");
            String partyno = extras.getString("partyno");
            String sino = extras.getString("sino");
            String aadhar = extras.getString("aadhar");
            System.out.println("#Purva Retrieved extras in HelloWorldActivity are: " + name + " " + partyname + " " + constituency + " " + partyno + " " + sino + " " + aadhar);
            System.out.println("#Purva Retrived aadhar in HelloWorld:"+LoginActivity.AADHAR);
            //extras.putString("name",name);
        }
    }
}
