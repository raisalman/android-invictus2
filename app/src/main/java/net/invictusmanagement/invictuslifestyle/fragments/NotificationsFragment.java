package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.NotificationsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.NotificationsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.NotificationCount;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

public class NotificationsFragment extends Fragment implements IRefreshableFragment {

    public static boolean isRWTNotifictions;
    public static NotificationsListFragmentInteractionListener _listener;
    public static RecyclerView _recyclerView;
    public static NotificationsAdapter _adapter;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;
    public static TextView _feedback;
    public static NotificationCount notificationCount;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private static NotificationsFragment instance;

    public NotificationsFragment() {
    }

    @SuppressWarnings("unused")
    public static NotificationsFragment newInstance() {
        if (instance != null)
            return instance;
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        instance = this;
        if (view instanceof SwipeRefreshLayout) {

            if (TabbedActivity.isGuestUser) {
                isRWTNotifictions = sharedPreferences.getBoolean("isGWTNotifictions", true);
            } else {
                isRWTNotifictions = sharedPreferences.getBoolean("isRWTNotifictions", true);
            }

            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new NotificationsAdapter(_listener, _context, isRWTNotifictions);
            _recyclerView.setAdapter(_adapter);

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
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
        if (_context instanceof NotificationsListFragmentInteractionListener) {
            _listener = (NotificationsListFragmentInteractionListener) _context;
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

    @Override
    public void refresh() {
        refresh(true);
    }

    public void refresh(Boolean isProgress) {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        getNotificationCount();
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (isProgress)
                    _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    _adapter.refresh(MobileDataProvider.getInstance().getNotifications());
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
                Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
                Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
                if (!success)
                    Toast.makeText(_context, "Unable to refresh notifications. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }


    public void getNotificationCount() {
        WebService.getInstance().getNotificationCount(new RestCallBack<NotificationCount>() {

            @Override
            public void onResponse(NotificationCount notificationCount) {
                if (notificationCount != null && getActivity() != null) {
                    ((TabbedActivity) getActivity()).updateCount(notificationCount.getUnreadNotificationCount());
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }
}
