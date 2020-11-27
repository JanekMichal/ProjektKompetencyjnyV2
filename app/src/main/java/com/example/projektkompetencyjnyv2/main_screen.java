package com.example.projektkompetencyjnyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class main_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CurrentUser currentUser = new CurrentUser(getApplicationContext());
        if(currentUser.getId()!= 0)
        {
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(myIntent);
        }else
        {
            setContentView(R.layout.activity_main_screen);
        }
    }
    public  void  register(View view)
    {
        Intent myIntent = new Intent(getBaseContext(), Register.class);
        startActivity(myIntent);
    }
}