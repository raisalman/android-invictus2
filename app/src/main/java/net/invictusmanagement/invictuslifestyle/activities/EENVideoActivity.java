package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.webservice.RestClientEEN;

import java.util.Objects;

public class EENVideoActivity extends AppCompatActivity {

    private ImageView imgClose;
    private TextView tvCameraName;
    private String cameraId;
    private String cameraName;
    private CountDownTimer cTimer = null;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    //    private VLCVideoLayout mVideoLayout = null;
    private ExoPlayer exoPlayer;
    private StyledPlayerView idExoPlayerVIew;

//    private LibVLC mLibVLC = null;
//    private MediaPlayer mMediaPlayer = null;

    String videoUrl = "";
    int cameraOperator = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_een_video);
        Objects.requireNonNull(getSupportActionBar()).hide();

        cameraId = getIntent().getStringExtra("ID");
        cameraName = getIntent().getStringExtra("NAME");
        cameraOperator = getIntent().getIntExtra("CAMERA_OPERATOR", 1);
        initViews();
        if (cameraOperator == 1) {
            refreshEENVideo();
        } else if (cameraOperator == 2) {
            refreshAVAVideo();
        }
    }

    private void refreshAVAVideo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String avaUserName = sharedPreferences.getString("avaUserName", "");
        String avaPassword = sharedPreferences.getString("avaPassword", "");
        String avaServerName = sharedPreferences.getString("avaServerName", "");

        if (avaServerName != null)
            if (avaServerName.length() > 0) {
                String first = avaServerName.split("\\.")[0];
                avaServerName = avaServerName.replace(first, first + ".rtsp");
                Log.e("TAG CAMERA", avaServerName);
            }

        if (avaUserName != null && avaPassword != null && avaServerName != null) {
            if (avaUserName.length() > 0 && avaPassword.length() > 0 && avaServerName.length() > 0) {
                videoUrl = "rtsps://" + avaUserName + ":" + avaPassword + "@" + avaServerName + ":322/deviceStreams?deviceId=" + cameraId;
                Log.e("TAG CAMERA", videoUrl);
                setMediaData();
            } else {
                Toast.makeText(this, "Something went wrong, please try again later!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong, please try again later!", Toast.LENGTH_LONG).show();
        }

    }

    private void refreshEENVideo() {
        String baseURL = "https://c001.eagleeyenetworks.com/api/v2/media/cameras/";
        String cookie = RestClientEEN.restClient.getAuthenticationCookie()
                .split(";")[0].split("=")[1];

        videoUrl = "https://c001.eagleeyenetworks.com/asset/play/video.flv?id=" + cameraId + "&A=" +
                cookie + "&start_timestamp=stream_20220530000016.768&end_timestamp=+15000";
        setMediaData();

//        String url = baseURL + cameraId + "/streams?A=" + cookie;


//        WebServiceEEN.getInstance().eenStreamUrl(url, new RestCallBack<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject object = new JSONObject(response);
//                    if (object.getInt("status_code") == 200) {
//                        if (object.has("data")) {
//                            JSONObject jsonObject = object.getJSONObject("data");
//                            videoUrl = jsonObject.getString("rtsp");
//
//                            setMediaData();
//                        }
//                    } else {
//                        Toast.makeText(EENVideoActivity.this,
//                                "Something went wrong. Please try again after sometime!"
//                                , Toast.LENGTH_LONG).show();
//                        onBackPressed();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(WSException wse) {
//
//            }
//        });
    }

    private void setMediaData() {
//        Media media = new Media(mLibVLC, Uri.parse(videoUrl));
//        mMediaPlayer.setMedia(media);
//        media.release();
//        mMediaPlayer.play();
//        startTimer();

        try {
//            MediaSource mediaSource =
//                    new RtspMediaSource.Factory()
//                            .createMediaSource(MediaItem.fromUri(videoUrl));
            exoPlayer = new ExoPlayer.Builder(this).build();
            MediaItem mediaSource = MediaItem.fromUri(videoUrl);
            idExoPlayerVIew.setPlayer(exoPlayer);
            idExoPlayerVIew.setShowNextButton(false);
            idExoPlayerVIew.setShowPreviousButton(false);
            idExoPlayerVIew.hideController();
            exoPlayer.setMediaItem(mediaSource);
//            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            idExoPlayerVIew.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Objects.requireNonNull(idExoPlayerVIew.getPlayer()).play();
                    startTimer();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Objects.requireNonNull(idExoPlayerVIew.getPlayer()).pause();
                }
            });

        } catch (Exception e) {
            Log.e("EENVideoActivity", " exoplayer error " + e.toString());
            Toast.makeText(EENVideoActivity.this,
                    "Something went wrong. Please try again after sometime!",
                    Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void initViews() {
        imgClose = findViewById(R.id.imgClose);
        tvCameraName = findViewById(R.id.tvCameraName);

//        final ArrayList<String> args = new ArrayList<>();
//        args.add("-vvv");
//        mLibVLC = new LibVLC(this, args);
//        mMediaPlayer = new MediaPlayer(mLibVLC);
//        mVideoLayout = findViewById(R.id.video_layout);

        idExoPlayerVIew = findViewById(R.id.idExoPlayerVIew);
        tvCameraName.setText(cameraName);

        imgClose.setOnClickListener(v -> onBackPressed());
    }

    private void startTimer() {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                cancelTimer();
                finish();
            }
        };
        cTimer.start();
    }

    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        cancelTimer();
        finish();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mMediaPlayer.release();
//        mLibVLC.release();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mMediaPlayer.stop();
//        mMediaPlayer.detachViews();
//    }
}

