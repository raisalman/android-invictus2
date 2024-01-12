package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.ChoosePostActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.MarketPostAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.BottomDialog;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.MarketPostListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.List;

public class BillBoardFragment extends Fragment implements IRefreshableFragment, SetOnBottomDialogButtonClick {

    public static MarketPostAdapter _adapter;
    private MarketPostListFragmentInteractionListener _listener;
    private RecyclerView _recyclerView;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private TextView _feedback, _tvNoFavourite, tvFilterType, txtCreateList;
    private RelativeLayout rlCreate;
    private ConstraintLayout _clSwitch;
    private Boolean isMyPost = false;
    private List<BulletinBoard> bulletinBoardList;
    private Boolean isDataAvailable = false;
    private Boolean isFavourite = false;
    private BottomDialog dialog = null;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private boolean isRWTBulletinBoard;
    private static BillBoardFragment instance;

    public BillBoardFragment() {
    }

    @SuppressWarnings("unused")
    public static BillBoardFragment newInstance() {
        if (instance != null)
            return instance;
        return new BillBoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bill_board, container, false);
        instance = this;
        if (view instanceof SwipeRefreshLayout) {

            isRWTBulletinBoard = sharedPreferences.getBoolean("isRWTBulletinBoard", true);
            tvFilterType = view.findViewById(R.id.tvFilterType);
            _feedback = view.findViewById(R.id.feedback);
            ImageView imgFilter = view.findViewById(R.id.imgFilter);
            _clSwitch = view.findViewById(R.id.clSwitch);
            _recyclerView = view.findViewById(R.id.productList);
            _tvNoFavourite = view.findViewById(R.id.tvNoFavourite);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));

            dialog = new BottomDialog(this);
            tvFilterType.setText("All");
            isFavourite = false;
            isMyPost = false;
            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.BulletinBoard.value());
                    refresh();
                }
            });

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

            rlCreate = view.findViewById(R.id.rlCreate);
            rlCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.addHaptic(v);
                    startActivity(new Intent(TabbedActivity.tabbedActivity, ChoosePostActivity.class));
                }
            });
            txtCreateList = view.findViewById(R.id.txtCreateList);
            txtCreateList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(TabbedActivity.tabbedActivity, ChoosePostActivity.class));
                }
            });

            _adapter = new MarketPostAdapter(getContext(), this,
                    _listener, isRWTBulletinBoard, rlCreate);
            _recyclerView.setAdapter(_adapter);

            refresh();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        if (_context instanceof MarketPostListFragmentInteractionListener) {
            _listener = (MarketPostListFragmentInteractionListener) _context;
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
                    bulletinBoardList = MobileDataProvider.getInstance().getBulletinBoard();
                    _adapter.refresh(bulletinBoardList, isMyPost, isFavourite);
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
                    Toast.makeText(getActivity(), "Unable to refresh bulletin board. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public void setErrorView() {
        Utilities.showHide(_context, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        Utilities.showHide(_context, _clSwitch, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
                if (isFavourite) {
                    _tvNoFavourite.setText(R.string.no_favourite_sell_service_available);
                } else {
                    _tvNoFavourite.setText(R.string.no_sell_service_available);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void setFilter(int number) {

        if (number == 1) {
            //all
            tvFilterType.setText("All");
            isMyPost = false;
            isFavourite = false;
            _adapter.refresh(bulletinBoardList, isMyPost, isFavourite);
            _adapter.notifyDataSetChanged();
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
                _tvNoFavourite.setText(R.string.no_sell_service_available);
            }

        } else if (number == 2) {
            //mine
            tvFilterType.setText("Mine");
            isMyPost = true;
            isFavourite = false;
            if (isDataAvailable) {
                _adapter.refresh(bulletinBoardList, isMyPost, isFavourite);
                _adapter.notifyDataSetChanged();
                if (_adapter.getItemCount() > 0) {
                    _tvNoFavourite.setVisibility(View.GONE);
                } else {
                    _tvNoFavourite.setVisibility(View.VISIBLE);
                    _tvNoFavourite.setText(R.string.no_sell_service_available);
                }
            }
        } else if (number == 3) {
            //fav
            tvFilterType.setText("Favorite");
            isMyPost = false;
            isFavourite = true;
            if (isDataAvailable) {
                _adapter.refresh(bulletinBoardList, isMyPost, isFavourite);
                _adapter.notifyDataSetChanged();
                if (_adapter.getItemCount() > 0) {
                    _tvNoFavourite.setVisibility(View.GONE);
                } else {
                    _tvNoFavourite.setVisibility(View.VISIBLE);
                    _tvNoFavourite.setText(R.string.no_favourite_sell_service_available);
                }
            }
        }

    }
}