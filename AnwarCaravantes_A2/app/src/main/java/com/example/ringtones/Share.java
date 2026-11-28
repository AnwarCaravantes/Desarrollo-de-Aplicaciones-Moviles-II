package com.example.ringtones;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Share extends AppCompatActivity {

    private String songFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        songFile = getIntent().getStringExtra("SONG_FILE");

        ImageButton ibBack = findViewById(R.id.back);
        TextView tvVolver = findViewById(R.id.tVBack);

        View.OnClickListener volverListener = v -> finish();
        ibBack.setOnClickListener(volverListener);
        tvVolver.setOnClickListener(volverListener);

        // Facebook
        ImageButton ibFace = findViewById(R.id.face);
        TextView tvFace = findViewById(R.id.tVFacebook);
        View.OnClickListener faceListener = v -> shareToApp("com.facebook.katana");
        ibFace.setOnClickListener(faceListener);
        tvFace.setOnClickListener(faceListener);

        // WhatsApp
        ImageButton ibWhats = findViewById(R.id.whats);
        TextView tvWhats = findViewById(R.id.tVWhatsapp);
        View.OnClickListener whatsListener = v -> shareToApp("com.whatsapp");
        ibWhats.setOnClickListener(whatsListener);
        tvWhats.setOnClickListener(whatsListener);

        // TwitterX
        ImageButton ibX = findViewById(R.id.x);
        TextView tvX = findViewById(R.id.tVX);
        View.OnClickListener xListener = v -> shareToApp("com.twitter.android");
        ibX.setOnClickListener(xListener);
        tvX.setOnClickListener(xListener);

        // Instagram
        ImageButton ibInsta = findViewById(R.id.instagram);
        TextView tvInsta = findViewById(R.id.tVInstagram);
        View.OnClickListener instaListener = v -> shareToApp("com.instagram.android");
        ibInsta.setOnClickListener(instaListener);
        tvInsta.setOnClickListener(instaListener);
    }


    private void shareToApp(String packageName) {
        if (songFile == null || songFile.isEmpty()) {
            Toast.makeText(this, "No se recibió la canción", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri audioUri = getUriFromAssets(songFile);
        if (audioUri == null) {
            return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("audio/mpeg");
        sendIntent.putExtra(Intent.EXTRA_STREAM, audioUri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (packageName != null) {
            sendIntent.setPackage(packageName);
            try {
                startActivity(sendIntent);
                return;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "App no instalada, abriendo opciones...", Toast.LENGTH_SHORT).show();
            }
        }

        Intent chooser = Intent.createChooser(sendIntent, "Compartir canción");
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(chooser);
    }

    private Uri getUriFromAssets(String fileName) {
        File outDir = new File(getCacheDir(), "canciones_compartir");
        if (!outDir.exists() && !outDir.mkdirs()) {
            Toast.makeText(this, "No se pudo crear la carpeta temporal", Toast.LENGTH_SHORT).show();
            return null;
        }

        File outFile = new File(outDir, fileName);
        try (InputStream in = getAssets().open("canciones/" + fileName);
             OutputStream out = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            Toast.makeText(this, "Error al preparar audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

        return FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".fileprovider",
                outFile
        );
    }

}