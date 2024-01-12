package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import net.invictusmanagement.invictuslifestyle.activities.RecentTopicListActivity;
import net.invictusmanagement.invictuslifestyle.adapters.RecentChatListAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.RecentChatListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.RecentChat;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class RecentChatFragment extends Fragment implements IRefreshableFragment,
        RecentChatListFragmentInteractionListener {

    private RecyclerView _recyclerView;
    private RecentChatListAdapter _adapter;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback, tvNoActive;
    private List<RecentChat> recentChats;
    private Boolean isDataAvailable = false;

    public RecentChatFragment() {
    }

    @SuppressWarnings("unused")
    public static RecentChatFragment newInstance() {
        return new RecentChatFragment();
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
            _adapter = new RecentChatListAdapter(_context, this);
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
            _adapter.refresh(recentChats);
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

        WebService.getInstance().getRecentCHatList(new RestCallBack<List<RecentChat>>() {
            @Override
            public void onResponse(List<RecentChat> response) {

                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    recentChats = response;
                    _adapter.refresh(recentChats);
                    _adapter.notifyDataSetChanged();
                    setErrorView();
                } else {
                    Toast.makeText(_context, "Unable to refresh recent chat list." +
                            " Please try again later.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(WSException wse) {
                Toast.makeText(_context, "Unable to refresh recent chat list." +
                        " Please try again later.", Toast.LENGTH_LONG).show();
                _swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
        isDataAvailable = _adapter.getItemCount() > 0;
        if (_adapter.getItemCount() > 0) {
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

    @Override
    public void onListFragmentInteraction(RecentChat item) {
        Intent intent = new Intent(_context, RecentTopicListActivity.class);
        intent.putExtra("ID", item.applicationUserId);
        startActivity(intent);
    }
}