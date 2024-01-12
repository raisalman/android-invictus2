package net.invictusmanagement.invictuslifestyle.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.VoiceMailAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.VoiceMailFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.VoiceMail;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class VoiceMailFragment extends Fragment implements IRefreshableFragment {

    public static VoiceMailAdapter _adapter;
    private VoiceMailFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback;
    private boolean isRWTVoiceMail;
    private static VoiceMailFragment instance;

    public VoiceMailFragment() {
    }

    @SuppressWarnings("unused")
    public static VoiceMailFragment newInstance() {
        if (instance != null)
            return instance;
        return new VoiceMailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_voice_mail_list, container, false);
        instance = this;
        if (view instanceof SwipeRefreshLayout) {

            isRWTVoiceMail = sharedPreferences.getBoolean("isRWTVoiceMail", true);
            _feedback = (TextView) view.findViewById(R.id.feedback);
            _recyclerView = (RecyclerView) view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new VoiceMailAdapter(_listener, this, isRWTVoiceMail);
            _recyclerView.setAdapter(_adapter);

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.VoiceMail.value());
                    refresh();
                }
            });
            refresh();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof VoiceMailFragmentInteractionListener) {
            _listener = (VoiceMailFragmentInteractionListener) _context;
        } else {
            throw new RuntimeException(_context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    _adapter.refresh(MobileDataProvider.getInstance().getVoiceMail());
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                setErrorView();
                if (!success)
                    Toast.makeText(getActivity(), "Unable to refresh voice mail. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
        /*if (isRWTVoiceMail) {
            if (_adapter.getItemCount() > 0) {
                _adapter.showWalkThrough();
                sharedPreferences.edit().putBoolean("isRWTVoiceMail", false).apply();
                isRWTVoiceMail = sharedPreferences.getBoolean("isRWTVoiceMail", true);
            }
        }*/

    }

    public void downloadFile(VoiceMail item) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.videoName));

        request.setDescription("Please Wait");
        request.setTitle("Downloading Voice Mail");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        try {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Utilities.getFileNameFromUrl(new URL(item.videoName)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
