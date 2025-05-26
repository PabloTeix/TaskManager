package com.example.taskmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Después de 1 segundo se redirige a MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Redirigir a MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Finaliza la actividad SplashScreen para que no se quede en la pila de actividades
            }
        }, 1000);  // 1000 ms = 1 segundo
    }
}
