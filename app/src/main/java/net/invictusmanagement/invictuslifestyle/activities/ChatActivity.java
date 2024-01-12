package net.invictusmanagement.invictuslifestyle.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.twilio.video.AudioCodec;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalTrackPublicationOptions;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TrackPriority;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.SlideView;
import net.invictusmanagement.invictuslifestyle.enum_utils.AccessPointOperator;
import net.invictusmanagement.invictuslifestyle.enum_utils.DeviceType;
import net.invictusmanagement.invictuslifestyle.interfaces.OnFinishListener;
import net.invictusmanagement.invictuslifestyle.models.ChatToken;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.utils.CameraCapturerCompat;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends BaseActivity {

    public final static String EXTRA_ACCESS_POINT_ID = "net.invictusmanagement.invictusmobile.Access.Point.Id";
    public final static String EXTRA_CALLER_NAME = "net.invictusmanagement.invictusmobile.Caller.Name";
    public final static String EXTRA_ACCESSPOINT_NAME = "net.invictusmanagement.invictusmobile.AccessPoint.Name";
    public final static String EXTRA_ROOM_ID = "net.invictusmanagement.invictusmobile.Room.Id";

    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;

    private ProgressBar _progressBar;
    private VideoView _remoteVideoView;
    private VideoView _localVideoView;
    private LinearLayout _buttonAnswer;
    //    private TextView _buttonOpenAccessPoint;
    //    private TextView _buttonVideoMail;
    private LinearLayout _buttonDecline;
    private TextView txtDecline;

    private Boolean _isDoNotDisturb;
    private CameraCapturer _cameraCapturer;
    private LocalAudioTrack _localAudioTrack;
    private LocalVideoTrack _localVideoTrack;

    private int _accessPointId;
    private Room _room;
    private String _kioskName;
    private String _accessPointName;
    private String _roomId;

    private Vibrator mvibrator;
    private Ringtone _ringtone;
    private int _previousAudioMode;
    private boolean _previousIsSpeakerPhoneOn;
    private boolean _previousMicrophoneMute;
    private int _previousRingerMode;
    private AudioManager _audioManager;
    private boolean _hasCameraAndMicPermissions;
    private Timer _timer;

    public static Boolean isVisible = false;
    private SlideView slideView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        NotificationManager notify_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notify_manager.cancelAll();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        _progressBar = findViewById(R.id.progress);
        _localVideoView = findViewById(R.id.video_local);
        _remoteVideoView = findViewById(R.id.video_remote);
        _buttonAnswer = findViewById(R.id.button_answer);
//        _buttonOpenAccessPoint = findViewById(R.id.button_open_access_point);
//        _buttonVideoMail = (TextView) findViewById(R.id.button_video_mail);
        slideView = findViewById(R.id.slide_view);
        _buttonDecline = findViewById(R.id.button_decline);
        txtDecline = findViewById(R.id.txtDecline);

        _buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.addHaptic(_buttonAnswer);
                stopRingtone();

                if (_room == null || _room.getState() != Room.State.CONNECTED || !_hasCameraAndMicPermissions)
                    return;

                Utilities.showHide(ChatActivity.this, _buttonAnswer, false);
//                Utilities.showHide(ChatActivity.this, _buttonVideoMail, false);

                txtDecline.setText("End");
                setupLocalVideo();
                setAutoFinishTimer();
            }
        });

        _buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_buttonAnswer.getVisibility() == View.VISIBLE) {
                    callWebServiceForSendToVoice();
                }
                Utilities.addHaptic(_buttonDecline);
                finishAndKillApp();
            }
        });

        slideView.setOnFinishListener(new OnFinishListener() {
            @Override
            public void onFinish() {
                Utilities.addHaptic(slideView);
                slideView.setBackground(getDrawable(R.drawable.swipe_bottom_corner_yellow));
                callWebServiceToUnlockDoor();
            }
        });

//        _buttonVideoMail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addHaptic(_buttonVideoMail);

//            }
//        });

        final Intent intent = getIntent();
        _accessPointId = Integer.parseInt(intent.getStringExtra(EXTRA_ACCESS_POINT_ID));
        _kioskName = intent.getStringExtra(EXTRA_CALLER_NAME);
        // If this isn't a new app install the push notification template will already have be registered without the accessPointName so let's default
        // the access point name to the kiosk name.
        _accessPointName = TextUtils.isEmpty(intent.getStringExtra(EXTRA_ACCESSPOINT_NAME)) ? _kioskName : intent.getStringExtra(EXTRA_ACCESSPOINT_NAME);
        _roomId = intent.getStringExtra(EXTRA_ROOM_ID);

        setTitle("Call from " + _kioskName);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context) ChatActivity.this);
        MobileDataProvider.getInstance().setAuthenticationCookie(sharedPreferences.getString("authenticationCookie", null));

        _hasCameraAndMicPermissions = checkPermissionForCameraAndMicrophone();
        if (!_hasCameraAndMicPermissions)
            requestPermissionForCameraAndMicrophone();

        try {

            _isDoNotDisturb = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                _isDoNotDisturb = notificationManager.getCurrentInterruptionFilter() != NotificationManager.INTERRUPTION_FILTER_NONE;
            }
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            _previousAudioMode = _audioManager.getMode();
            _audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            _audioManager.setMode(AudioManager.MODE_IN_CALL);

            _previousRingerMode = _audioManager.getRingerMode();
            if (!_isDoNotDisturb)
                _audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            _previousIsSpeakerPhoneOn = _audioManager.isSpeakerphoneOn();
            _audioManager.setSpeakerphoneOn(true);

            _previousMicrophoneMute = _audioManager.isMicrophoneMute();
            _audioManager.setMicrophoneMute(false);

            final Uri uri = Uri.parse(sharedPreferences.getString("chat_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString()));
//            Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            _ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);


            new AsyncTask<String, Void, ChatToken>() {

                private String _roomId;

                @Override
                protected void onPreExecute() {
                    playRingtone();
                }

                @Override
                protected ChatToken doInBackground(String... args) {
                    try {
                        _roomId = args[0];
                        return MobileDataProvider.getInstance().getChatToken(_roomId);
                    } catch (Exception ex) {
                        Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(ChatToken token) {
                    AudioCodec audioCodec = new OpusCodec();
                    if (token == null) {
                        Toast.makeText((Context) ChatActivity.this, "Unable to initialize video chat.  Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            ConnectOptions connectOptions = new ConnectOptions.Builder(token.token)
                                    .roomName(_roomId)
                                    .preferAudioCodecs(Collections.singletonList(audioCodec))
                                    .build();
                            _room = Video.connect((Context) ChatActivity.this, connectOptions, roomListener());
                            Log.d(Utilities.TAG, "Connecting to room: " + _room.getName());
                            setAutoFinishTimer();

                        } catch (Exception ex) {
                            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                            Toast.makeText((Context) ChatActivity.this, "Unable to connect to the specified video chat room.  Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }.execute(_roomId);

        } catch (Exception ex) {
            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
            Toast.makeText((Context) this, "Unable to initialize video chat.  Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    private void playRingtone() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        boolean status = false;
        if (audioManager != null) {
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                status = true;
            }
        }
        if (status) {
            if (_ringtone != null)
                _ringtone.play();
        } else {
            mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Start without a delay
            long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                    150, 75, 150};

            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            mvibrator.vibrate(pattern, 0);
        }
    }


    private void stopRingtone() {
        if (_ringtone != null)
            _ringtone.stop();


        try {
            if (mvibrator != null) {
                if (mvibrator.hasVibrator()) {
                    mvibrator.cancel();
                }
                mvibrator.cancel();
                mvibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Intent intent1 = new Intent(this, NotificationSoundService.class);
//        intent1.setAction(NotificationSoundService.ACTION_STOP_PLAYBACK);
//        startService(intent1);
    }

    private void callWebServiceForSendToVoice() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                stopRingtone();
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().sendToVoiceMail(_accessPointId);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                finishAndKillApp();
            }
        }.execute();
    }

    private void callWebServiceToUnlockDoor() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                stopRingtone();
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    OpenAccessPoint model = new OpenAccessPoint();
                    model.id = _accessPointId;
                    model.isSilent = false;
                    model.isVideoAccess = true;
                    model.entryName = _accessPointName;
                    model.deviceType = DeviceType.Mobile.value();
                    model.operator = AccessPointOperator.Invictus.value();
                    MobileDataProvider.getInstance().openAccessPoint(model);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    slideView.setBackground(getDrawable(R.drawable.swipe_bottom_corner_green));
                } else {
                    slideView.setBackground(getDrawable(R.drawable.swipe_bottom_corner_red));
                }
                Toast.makeText((Context) ChatActivity.this, success ? _accessPointName + " was successfully opened." :
                        "Opening the " + _accessPointName + " access point failed.", Toast.LENGTH_LONG).show();
                finishAndKillApp();
            }
        }.execute();
    }


    private void setupLocalVideo() {
        _localAudioTrack = LocalAudioTrack.create((Context) ChatActivity.this, true, "mic");

        // Share your camera
        CameraCapturerCompat cameraCapturerCompat =
                new CameraCapturerCompat(this, CameraCapturerCompat.Source.FRONT_CAMERA);
        _localVideoTrack =
                LocalVideoTrack.create((Context) this, true, cameraCapturerCompat, "camera");

        // Render a local video track to preview your camera
        if (_localVideoTrack != null) {
            _localVideoTrack.addSink(_localVideoView);
        }
        LocalParticipant participant = _room.getLocalParticipant();
        if (participant != null) {
            if (_localAudioTrack != null) {
                _localAudioTrack.enable(true);
                participant.publishTrack(_localAudioTrack);
            }
            if (_localVideoTrack != null)
                participant.publishTrack(_localVideoTrack,
                        new LocalTrackPublicationOptions(TrackPriority.HIGH));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {

            _hasCameraAndMicPermissions = true;
            for (int grantResult : grantResults) {
                _hasCameraAndMicPermissions &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (!_hasCameraAndMicPermissions)
                notifyPermissionsNeeded();

            Utilities.showHide(ChatActivity.this, _buttonAnswer, _hasCameraAndMicPermissions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_timer != null) {
            Log.d(Utilities.TAG, "Stopping auto finish timer.");
            _timer.cancel();
            _timer.purge();
        }

        stopRingtone();

        if (_audioManager != null) {
            _audioManager.setMode(_previousAudioMode);
            _audioManager.abandonAudioFocus(null);
            _audioManager.setSpeakerphoneOn(_previousIsSpeakerPhoneOn);
            _audioManager.setMicrophoneMute(_previousMicrophoneMute);
            if (!_isDoNotDisturb)
                _audioManager.setRingerMode(_previousRingerMode);
        }

        if (_room != null && _room.getState() != Room.State.DISCONNECTED) {
            Log.d(Utilities.TAG, "Disconnecting from room.");

            // Release the audio track to free native memory resources
            if (_localAudioTrack != null)
                _localAudioTrack.release();

            // Release the video track to free native memory resources
            if (_localVideoTrack != null)
                _localVideoTrack.release();
            _room.disconnect();
        }

        if (_localAudioTrack != null) {
            Log.d(Utilities.TAG, "Releasing local audio.");
            _localAudioTrack.release();
            _localAudioTrack = null;
        }

        if (_localVideoTrack != null) {
            Log.d(Utilities.TAG, "Releasing local video.");
            _localVideoTrack.release();
            _localVideoTrack = null;
        }
    }

    private void finishAndKillApp() {
        finishAndRemoveTask();
    }


    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                Log.d(Utilities.TAG, "Connected to room: " + room.getName() + " >> " + room.getState());
                Utilities.showHide(ChatActivity.this, _buttonAnswer, _hasCameraAndMicPermissions);
                List<RemoteParticipant> participants = room.getRemoteParticipants();
                if (!participants.isEmpty()) {
                    RemoteParticipant participant = participants.iterator().next();
                    if (participant.getVideoTracks().size() > 0) {
                        VideoTrack track = participant.getVideoTracks().get(0).getVideoTrack();
                        if (track != null) {
                            track.addSink(_remoteVideoView);
                        }
                    }
                    participant.setListener(participantListener());
                }
                Utilities.showHide(ChatActivity.this, _progressBar, false);
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                Log.e(Utilities.TAG, e.getMessage());
                Toast.makeText((Context) ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onReconnected(@NonNull Room room) {

            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                Log.d(Utilities.TAG, "Disconnected from room: " + room.getName());

                // Release the audio track to free native memory resources
                if (_localAudioTrack != null)
                    _localAudioTrack.release();

                // Release the video track to free native memory resources
                if (_localVideoTrack != null)
                    _localVideoTrack.release();
                ChatActivity.this._room = null;
                finishAndKillApp();
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.d(Utilities.TAG, "Participant connected: " + remoteParticipant.getIdentity());
                if (remoteParticipant.getVideoTracks().size() > 0) {
                    Log.d(Utilities.TAG, "Adding participant video (onParticipantConnected): " + remoteParticipant.getIdentity());
                    VideoTrack track = remoteParticipant.getVideoTracks().get(0).getVideoTrack();
                    if (track != null) {
                        track.addSink(_remoteVideoView);
                    }
                }
                remoteParticipant.setListener(participantListener());
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                Log.d(Utilities.TAG, "Participant disconnected: " + remoteParticipant.getIdentity());
                if (remoteParticipant.getVideoTracks().size() > 0) {
                    Log.d(Utilities.TAG, "Removing participant video: " + remoteParticipant.getIdentity());
                    VideoTrack track = remoteParticipant.getVideoTracks().get(0).getVideoTrack();
                    if (track != null) {
                        track.removeSink(_remoteVideoView);
                    }
                }
                finishAndKillApp();
            }

            @Override
            public void onRecordingStarted(Room room) {

            }

            @Override
            public void onRecordingStopped(Room room) {

            }
        };
    }

    private RemoteParticipant.Listener participantListener() {
        return new RemoteParticipant.Listener() {

            @Override
            public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

            }

            @Override
            public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.e("Remote Room >> ", "onVideoTrackPublished");
            }

            @Override
            public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.e("Remote Room >> ", "onVideoTrackUnpublished");
            }

            @Override
            public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                               @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               @NonNull RemoteVideoTrack remoteVideoTrack) {
                _remoteVideoView.setMirror(false);
                remoteVideoTrack.addSink(_remoteVideoView);

            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {
                Log.e("Remote Room >> ", "onVideoTrackSubscriptionFailed");
            }

            @Override
            public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {

            }

            @Override
            public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

            }

            @Override
            public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

            }

            @Override
            public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED && resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            notifyPermissionsNeeded();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO}, CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void notifyPermissionsNeeded() {
        Log.w(Utilities.TAG, getResources().getString(R.string.permissions_needed));
        Toast.makeText((Context) this, R.string.permissions_needed, Toast.LENGTH_LONG).show();
    }

    private void setAutoFinishTimer() {

        if (_timer != null) {
            Log.d(Utilities.TAG, "Stopping auto finish timer.");
            _timer.cancel();
            _timer.purge();
            _timer = null;
        }   // timer null?

        Log.d(Utilities.TAG, "Starting auto finish timer.");
        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finishAndKillApp();
            }
        }, 60000);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }
}
