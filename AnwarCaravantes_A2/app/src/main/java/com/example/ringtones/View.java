package com.example.ringtones;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class View extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int currentPlayingIndex = -1;


    private final String[] songFiles = {
            "¡Atrápalos Ya! - Pokémon.mp3",
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

    private final int[] shareIds = {
            R.id.share1, R.id.share2, R.id.share3, R.id.share4, R.id.share5,
            R.id.share6, R.id.share7, R.id.share8, R.id.share9, R.id.share10
    };

    private final int[] bluetoothIds = {
            R.id.bluetooth1, R.id.bluetooth2, R.id.bluetooth3, R.id.bluetooth4, R.id.bluetooth5,
            R.id.bluetooth6, R.id.bluetooth7, R.id.bluetooth8, R.id.bluetooth9, R.id.bluetooth10
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        for (int i = 0; i < playIds.length; i++) {
            final int index = i;

            ImageView ivPlay = findViewById(playIds[i]);
            ImageView ivDownload = findViewById(downloadIds[i]);
            ImageButton ibShare = findViewById(shareIds[i]);
            ImageButton ibBluetooth = findViewById(bluetoothIds[i]);

            ivPlay.setOnClickListener(v -> togglePlaySong(index));
            ivDownload.setOnClickListener(v -> downloadSongToDownloads(index));
            ibShare.setOnClickListener(v -> openShareActivity(index));
            ibBluetooth.setOnClickListener(v -> shareViaBluetooth(index));
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


    //Metodo para irnos a la sig pagina, deteniendo el repdroductor si esta reproduciendo
    private void openShareActivity(int index) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        Intent intent = new Intent(View.this, Share.class);
        intent.putExtra("SONG_FILE", songFiles[index]);
        intent.putExtra("SONG_INDEX", index);
        startActivity(intent);
    }


    //Metodo para compartir via Bluetooth, deteniendo el repdroductor si esta reproduciendo

    private void shareViaBluetooth(int index) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        Uri audioUri = getUriFromAssets(songFiles[index]);
        if (audioUri == null) {
            return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("audio/mpeg");
        sendIntent.putExtra(Intent.EXTRA_STREAM, audioUri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(sendIntent, "Enviar por Bluetooth");
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