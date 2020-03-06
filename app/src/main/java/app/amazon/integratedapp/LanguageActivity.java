package app.amazon.integratedapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    private Button eng_button,hin_button,tel_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_language);

        eng_button=(Button)findViewById(R.id.eng_button);
        hin_button=(Button)findViewById(R.id.hin_button);
        tel_button=(Button)findViewById(R.id.tel_button);

        eng_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });

        hin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("hi");
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });

        tel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("te");
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });


    }

    private void loadLocale() {
        SharedPreferences preferences=getSharedPreferences("language", Activity.MODE_PRIVATE);
        String lang=preferences.getString("selected_lang","");
        setLocale(lang);
    }

    private void setLocale(String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor=getSharedPreferences("Language",MODE_PRIVATE).edit();
        editor.putString("selected_lang",lang);
        editor.apply();
    }

}
