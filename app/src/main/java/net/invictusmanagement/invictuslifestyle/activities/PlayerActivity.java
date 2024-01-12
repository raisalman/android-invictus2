package net.invictusmanagement.invictuslifestyle.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class PlayerActivity extends BaseActivity {
    BetterVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        player = findViewById(R.id.player);

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String filename = Utilities.getFileNameFromUrl(new URL(getIntent().getExtras().getString("URL")));
            if (fileExists(path, filename)) {
                player.setSource(FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", new File(path, filename)));// new File(path, filename)
            } else {
                player.setSource(Uri.parse(getIntent().getExtras().getString("URL")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    public static boolean fileExists(File path, String filename) {
        return new File(path, filename).exists();
    }
}
