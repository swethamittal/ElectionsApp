package app.amazon.integratedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {
    TextView info;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        info=findViewById(R.id.textView);
        status=findViewById(R.id.status);
        Intent it=getIntent();
        status.setText(it.getStringExtra("status"));
        info.setText(it.getStringExtra("message"));
    }
}
