package com.example.ringtones;

import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class View extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int currentPlayingIndex = -1;


    private final String[] songFiles = {
            "¡Atrépalos Ya! - Pokémon.mp3",
            "Bling-Bang-Bang-Born - Mashle.mp3",
            "Blue Bird - Naruto.mp3",
            "Chala Head Chala - DBZ.mp3",
            "El Poder Nuestro Es - DBZ.mp3",
            "Mariposa - Digimon.mp3",
            "Mi Corazón Encantado - Dragon Ball GT.mp3",
            "Olvida la amargura - Ranma.mp3",
            "Pegasus Fantasy - Saint Seiya.mp3",
            "We Are - One Piece.mp3"
    };

    private final int[] playIds = {
            R.id.play1, R.id.play2, R.id.play3, R.id.play4, R.id.play5,
            R.id.play6, R.id.play7, R.id.play8, R.id.play9, R.id.play10
    };

    private final int[] downloadIds = {
            R.id.iVDownload1, R.id.iVDownload2, R.id.iVDownload3, R.id.iVDownload4, R.id.iVDownload5,
            R.id.iVDownload6, R.id.iVDownload7, R.id.iVDownload8, R.id.iVDownload9, R.id.iVDownload10
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        for (int i = 0; i < playIds.length; i++) {
            final int index = i;

            ImageView ivPlay = findViewById(playIds[i]);
            ImageView ivDownload = findViewById(downloadIds[i]);

            ivPlay.setOnClickListener(v -> togglePlaySong(index));
            ivDownload.setOnClickListener(v -> downloadSongToDownloads(index));
        }
    }


    //Metodo para reproducir las canciones y que no se encimen
    private void togglePlaySong(int index) {
        if (currentPlayingIndex == index && mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Toast.makeText(this, "Pausada: " + songFiles[index], Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.start();
                Toast.makeText(this, "Reanudando: " + songFiles[index], Toast.LENGTH_SHORT).show();
            }
            return;
        }

        releaseMediaPlayer();

        try (AssetFileDescriptor afd = getAssets().openFd("canciones/" + songFiles[index])) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();

            currentPlayingIndex = index;
            mediaPlayer.setOnCompletionListener(mp -> {
                currentPlayingIndex = -1;
                releaseMediaPlayer();
            });

            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo: " + songFiles[index], Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al reproducir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }


    //Metodo para descargar las canciones
    private void downloadSongToDownloads(int index) {
        String fileName = songFiles[index];
        String assetPath = "canciones/" + fileName;

        try (InputStream in = getAssets().open(assetPath)) {
            Uri targetUri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                targetUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            }

            if (targetUri == null) {
                Toast.makeText(this, "No se pudo preparar la descarga", Toast.LENGTH_SHORT).show();
                return;
            }

            try (OutputStream out = getContentResolver().openOutputStream(targetUri)) {
                if (out == null) {
                    Toast.makeText(this, "No se pudo escribir el archivo", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }

            Toast.makeText(this, "Descargada en Descargas: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al descargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}