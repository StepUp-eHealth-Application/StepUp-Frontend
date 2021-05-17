package de.hskempten.stepup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               openActivity();
           }
        });

    }
    public void openActivity(){
        Intent intent = new Intent(this, gesundheitszieleSetztenAendern.class);
        startActivity(intent);
    }
}