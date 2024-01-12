package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.NewCommunityNotificationActivity;
import net.invictusmanagement.invictuslifestyle.activities.NotificationReadUnread;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.CommunityNotificationAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.BottomChatDialog;
import net.invictusmanagement.invictuslifestyle.customviews.BottomNotificationDialog;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.interfaces.CommunityNotificationFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class CommunityNotificationFragment extends Fragment implements IRefreshableFragment,
        CommunityNotificationFragmentInteractionListener, SetOnBottomDialogButtonClick {

    public static Boolean isRWTServiceKey = false;
    public static RecyclerView _recyclerView;
    private CommunityNotificationAdapter _adapter;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView tvNoActive;
    private TextView txtCreateKey;
    private RelativeLayout rlCreate;
    public TextView tvFilterType;
    private ConstraintLayout _clSwitch;
    private BottomNotificationDialog dialog = null;
    public static List<CommunityNotificationList> notificationLists;
    public static Boolean isDataAvailable = false;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);

    public CommunityNotificationFragment() {
    }

    @SuppressWarnings("unused")
    public static CommunityNotificationFragment newInstance() {
        return new CommunityNotificationFragment();
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

        View view = inflater.inflate(R.layout.fragment_community_notification, container, false);
        if (view instanceof SwipeRefreshLayout) {

            isRWTServiceKey = sharedPreferences.getBoolean("isRWTServiceKey", true);
            txtCreateKey = view.findViewById(R.id.txtCreateKey);
            rlCreate = view.findViewById(R.id.rlCreate);
            rlCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),
                                    NewCommunityNotificationActivity.class),
                            ((TabbedActivity) _context)._sectionViewPagerAdapter.FRAGMENT_POSITION_SERVICE_KEYS);
                }
            });

            tvFilterType = view.findViewById(R.id.tvFilterType);
            ImageView imgFilter = view.findViewById(R.id.imgFilter);
            _clSwitch = view.findViewById(R.id.clSwitch);
            dialog = new BottomNotificationDialog(this);
            tvFilterType.setText("All");


            imgFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        if (!dialog.isHidden()) {
                            dialog.show(getActivity().getSupportFragmentManager(), "dialogFilter");
                            dialog.setCancelable(false);
                        } else {
                            dialog.dismiss();
                        }
                    }
                }
            });


            tvNoActive = view.findViewById(R.id.tvNoActive);
            _recyclerView = view.findViewById(R.id.list);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
            _adapter = new CommunityNotificationAdapter(_context, this, this,
                    isRWTServiceKey, rlCreate);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refreshAdapter() {
        if (isDataAvailable) {
            _adapter.refresh(notificationLists);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                tvNoActive.setVisibility(View.GONE);
            } else {
                tvNoActive.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callGetServiceKeyAPI() {
        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getCommunityNotifications(new RestCallBack<List<CommunityNotificationList>>() {
            @Override
            public void onResponse(List<CommunityNotificationList> response) {
                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    notificationLists = response;
                    _adapter.refresh(notificationLists);
                    _adapter.notifyDataSetChanged();
                    _swipeRefreshLayout.setRefreshing(false);
                    setErrorView();
                } else {
                    Utilities.showLongToast(_context,
                            getString(R.string.error_community_notification));
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);
                Utilities.showLongToast(_context,
                        getString(R.string.error_community_notification));
            }
        });
    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        callGetServiceKeyAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void setErrorView() {
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
    public void onListFragmentInteraction(CommunityNotificationList item, CommunityNotificationFragment fragment) {
        Intent intent = new Intent(_context, NotificationReadUnread.class);
        intent.putExtra("ID", item.id);
        startActivity(intent);
    }

    @Override
    public void refreshList() {
        refresh();
    }

    @Override
    public void setFilter(int number) {
        if (number == 1) {
            //all
            tvFilterType.setText("All");
            List<CommunityNotificationList> filterList = new ArrayList<>();
            filterList.addAll(notificationLists);

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, tvNoActive, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                tvNoActive.setText("No Notifications available.\nSwipe down to refresh.");
            } else {
                tvNoActive.setText(MobileDataProvider.mrsString);
            }
        } else if (number == 2) {
            //mine
            tvFilterType.setText("Group");

            List<CommunityNotificationList> filterList = new ArrayList<>();
            for (int i = 0; i < notificationLists.size(); i++) {
                if (notificationLists.get(i).applicationUserNotifications.size() > 1) {
                    filterList.add(notificationLists.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, tvNoActive, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                tvNoActive.setText("No Group Notification available");
            } else {
                tvNoActive.setText(MobileDataProvider.mrsString);
            }

        } else if (number == 3) {
            //mine
            tvFilterType.setText("Individual");

            List<CommunityNotificationList> filterList = new ArrayList<>();
            for (int i = 0; i < notificationLists.size(); i++) {
                if (notificationLists.get(i).applicationUserNotifications.size() <= 1) {
                    filterList.add(notificationLists.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, tvNoActive, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                tvNoActive.setText("No Individual Notification available");
            } else {
                tvNoActive.setText(MobileDataProvider.mrsString);
            }
        }
    }
}