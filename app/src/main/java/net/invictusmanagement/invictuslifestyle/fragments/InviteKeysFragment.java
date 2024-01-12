package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.GuestDigitalKeyMapActivity;
import net.invictusmanagement.invictuslifestyle.activities.SingleVideoImageActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.InviteKeysAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.InviteKeysListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.GuestDigitalKey;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InviteKeysFragment extends Fragment implements IRefreshableFragment, InviteKeysListFragmentInteractionListener {

    public static RecyclerView _recyclerView;
    public static InviteKeysAdapter _adapter;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;
    public static TextView _feedback, tvNoActive;
    public static List<GuestDigitalKey> inviteKeys;
    public static Boolean isDataAvailable = false;

    public InviteKeysFragment() {
    }

    @SuppressWarnings("unused")
    public static InviteKeysFragment newInstance() {
        return new InviteKeysFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite_keys, container, false);
        if (view instanceof SwipeRefreshLayout) {

            tvNoActive = view.findViewById(R.id.tvNoActive);
            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new InviteKeysAdapter(_context, this);
            _recyclerView.setAdapter(_adapter);

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.DigitalKey.value());
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
    }

    public void refreshAdapter() {
        if (isDataAvailable) {
            Boolean isActive = DigitalKeysFragment.isActive;
            _adapter.refresh(inviteKeys, isActive);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }

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
                    inviteKeys = MobileDataProvider.getInstance().getDigitalKeysForGuest();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.refresh(inviteKeys, DigitalKeysFragment.isActive);
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                setErrorView();
                if (!success)
                    Toast.makeText(_context, "Unable to refresh invite keys. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void onListFragmentInteraction(GuestDigitalKey item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_digital_key, null);

        builder.setView(view)
                .setTitle(item.recipient);

        String status = "Expired";
        if (item.isRevoked)
            status = "Revoked";
        else {
            Date now = new Date();
            if (now.after(item.fromUtc) && now.before(item.toUtc))
                status = "Valid";
            else if (now.before(item.fromUtc) && now.before(item.toUtc))
                status = "Upcoming";
        }
        ((TextView) view.findViewById(R.id.status)).setText(status);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(item.createdUtc));
        ((TextView) view.findViewById(R.id.fromUtc)).setText(formatter.format(item.fromUtc));
        ((TextView) view.findViewById(R.id.toUtc)).setText(formatter.format(item.toUtc));
        view.findViewById(R.id.notes_label).setVisibility(TextUtils.isEmpty(item.notes) ? View.GONE : View.VISIBLE);
        ((TextView) view.findViewById(R.id.notes)).setText(item.notes);
        view.findViewById(R.id.tvKeyTitle).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tvKey).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.tvKey)).setText(item.key);
        ImageView ivQRCode = view.findViewById(R.id.ivQrCode);
        if (item.qrCodeSrc != null) {
            ivQRCode.setVisibility(View.VISIBLE);
        } else {
            ivQRCode.setVisibility(View.GONE);
        }

        ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
                maintenanceRequesFiles.isImage = true;
                maintenanceRequesFiles.isFromAd = false;
                maintenanceRequesFiles.maintenanceRequestImageSrc = item.qrCodeSrc;
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                _context.startActivity(i);
            }
        });


        if (status.equals("Valid")) {
            if (item.mapUrl.length() > 0)
                builder.setNegativeButton("View Map", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(_context, GuestDigitalKeyMapActivity.class);
                        intent.putExtra("imageMap", item.mapUrl);
                        intent.putExtra("imageMaps", item.mapUrls);
                        startActivity(intent);
                    }
                });
        }

        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
}