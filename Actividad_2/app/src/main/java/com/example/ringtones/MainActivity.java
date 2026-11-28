package com.example.ringtones;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private static final long SPLASH_DURATION_MS = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Cargar las animaciones
        Animation animAbajoArriba = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajoarriba);
        Animation animArribaAbajo = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arribaabajo);

        // Referencias a los elementos
        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewLogo = findViewById(R.id.textViewLogo);

        // Asignar e iniciar una animación a cada elemento
        imageViewLogo.startAnimation(animArribaAbajo);
        textViewLogo.startAnimation(animAbajoArriba);

        // Tiempo de espera y pasar automáticamente a la pantalla View
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, View.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION_MS);
    }
}



