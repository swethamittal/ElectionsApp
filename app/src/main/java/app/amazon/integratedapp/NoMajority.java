package app.amazon.integratedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NoMajority extends AppCompatActivity {

    TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_majority);
        Intent it=getIntent();
        String mess=it.getStringExtra("mess");
        message=findViewById(R.id.message);
        message.setText(mess);
    }
}

