package app.amazon.integratedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ThanksForVotingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_for_voting);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(LoginActivity.temp.equals("Voter"))
        intent=new Intent(getApplicationContext(),VoterProfileActivity.class);
        else
        intent=new Intent(getApplicationContext(),CandidateProfileActivity.class);
        startActivity(intent);
    }
}

