package net.invictusmanagement.invictuslifestyle.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.GeneralChatActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.TopicAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.BottomChatDialog;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.ChatRequestStatus;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.interfaces.TopicListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.ChatNewTopicRequest;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class GeneralChatFragment extends Fragment implements IRefreshableFragment,
        TopicListFragmentInteractionListener, SetOnBottomDialogButtonClick {


    //    public static FloatingActionButton _newFloatingActionButton;
    private RelativeLayout rlCreate;
    public SwipeRefreshLayout _swipeRefreshLayout;
    public RecyclerView _recyclerView;
    public Context _context;
    public TopicAdapter _adapter;
    public static boolean active = false;
    public TextView _feedback;
    public TextView tvFilterType;
    private ConstraintLayout _clSwitch;
    private BottomChatDialog dialog = null;
    public EditText edDescription, edTitle;
    public Spinner spnMRS;
    public static List<Topic> topicArrayList = new ArrayList<>();
    public boolean isFirstTime = true;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private boolean isRWTTopic;
    private static GeneralChatFragment instance;
    private List<MaintenanceRequestResponse> maintenanceList = new ArrayList<>();

    public GeneralChatFragment() {
        // Required empty public constructor
    }


    @SuppressWarnings("unused")
    public static GeneralChatFragment newInstance() {

        if (instance == null) {
            return new GeneralChatFragment();
        }
        return instance;
    }

    public void getTopicList(boolean isProgress) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (isProgress)
                    _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    topicArrayList = MobileDataProvider.getInstance().getTopicList();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (_adapter == null) {
                    _adapter = new TopicAdapter(getContext(), GeneralChatFragment.this,
                            isRWTTopic, rlCreate);
                }
                _adapter.refresh(topicArrayList);
                _adapter.notifyDataSetChanged();

                if (tvFilterType.getText().toString().equalsIgnoreCase("All")) {
                    setFilter(1);
                } else if (tvFilterType.getText().toString().equalsIgnoreCase("Pending")) {
                    setFilter(2);
                } else if (tvFilterType.getText().toString().equalsIgnoreCase("Open")) {
                    setFilter(3);
                } else if (tvFilterType.getText().toString().equalsIgnoreCase("Close")) {
                    setFilter(4);
                }

                if (isProgress)
                    _swipeRefreshLayout.setRefreshing(false);

                if (GeneralChatFragment.newInstance().isFirstTime) {
                    for (int i = 0; i < topicArrayList.size(); i++) {
                        TabbedActivity.tabbedActivity.joinGroup(String.valueOf(topicArrayList.get(i).id));
                    }
                }

                GeneralChatFragment.newInstance().isFirstTime = false;

                if (!success) {
                    Utilities.showHide(_context, _feedback, true);
                    Utilities.showHide(_context, _recyclerView, false);
                } else {
                    Utilities.showHide(TabbedActivity.tabbedActivity, _recyclerView, _adapter.getItemCount() > 0);
                    Utilities.showHide(TabbedActivity.tabbedActivity, _feedback, _adapter.getItemCount() <= 0);
                }
                // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general_chat, container, false);
        if (view instanceof SwipeRefreshLayout) {

            _feedback = view.findViewById(R.id.feedback);
            _recyclerView = view.findViewById(R.id.rvTopicList);
            _recyclerView.setHasFixedSize(true);
            _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));

            _swipeRefreshLayout = (SwipeRefreshLayout) view;
            _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
            _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Chat.value());
                    refresh();
                }
            });
            isRWTTopic = sharedPreferences.getBoolean("isRWTTopic", true);

            tvFilterType = view.findViewById(R.id.tvFilterType);
            ImageView imgFilter = view.findViewById(R.id.imgFilter);
            _clSwitch = view.findViewById(R.id.clSwitch);
            dialog = new BottomChatDialog(this);
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

            rlCreate = view.findViewById(R.id.rlCreate);
            rlCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utilities.addHaptic(view);
                    openDialogToBooking();
                }
            });

            _adapter = new TopicAdapter(getContext(), this, isRWTTopic, rlCreate);
            _recyclerView.setAdapter(_adapter);

            refresh();
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void openDialogToBooking() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TabbedActivity.tabbedActivity);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chat_topic, null);
        builder.setView(view);

        final AlertDialog show = builder.setCancelable(true).show();

        spnMRS = view.findViewById(R.id.spnMRS);
        edDescription = view.findViewById(R.id.edDescription);
        edTitle = view.findViewById(R.id.edTitle);

        if (sharedPreferences.getString("userRole", "").equals(getString(R.string.role_vendor))) {
            spnMRS.setVisibility(View.VISIBLE);
            ArrayList<String> catString = new ArrayList<>();
            for (int i = 0; i < maintenanceList.size(); i++) {
                catString.add(maintenanceList.get(i).getTitle());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(_context, R.layout.spinner_item, catString);
            spnMRS.setAdapter(adapter);
        } else {
            spnMRS.setVisibility(View.GONE);
        }
        view.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit form
                Utilities.addHaptic(v);
                if (TabbedActivity.isHubConnected) {
                    checkValidation(show);
                } else {
                    Utilities.hideKeyboard(TabbedActivity.tabbedActivity);
                    Toast.makeText(TabbedActivity.tabbedActivity, "Please check after some time", Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog
                show.dismiss();
            }
        });


    }

    private void checkValidation(AlertDialog show) {
        boolean cancel = false;
        View focusView = null;

        edTitle.setError(null);

        if (TextUtils.isEmpty(edTitle.getText().toString())) {
            edTitle.setError(getString(R.string.error_field_required));
            focusView = edTitle;
            cancel = true;
        }

        if (!cancel) {
            ChatNewTopicRequest chatNewTopicRequest = new ChatNewTopicRequest();
            if (sharedPreferences.getString("userRole", "").equals(getString(R.string.role_vendor))) {
                chatNewTopicRequest.topic = edTitle.getText().toString().trim()
                        + " (" + spnMRS.getSelectedItem().toString() + ")";
                chatNewTopicRequest.locationId = maintenanceList.get(spnMRS.getSelectedItemPosition()).getLocationId();
            } else {
                chatNewTopicRequest.topic = edTitle.getText().toString().trim();
                chatNewTopicRequest.locationId = Long.parseLong(HomeFragment.roomLocationId);
            }
            chatNewTopicRequest.description = edDescription.getText().toString().trim();
            chatNewTopicRequest.sender = HomeFragment.userName;

            chatNewTopicRequest.applicationUserId = Long.parseLong(HomeFragment.userId);
            show.dismiss();
            TabbedActivity.tabbedActivity.addNewTopicRequest(chatNewTopicRequest);

            Topic item = new Topic();
            Intent intent = new Intent(getContext(), GeneralChatActivity.class);
            item.locationId = Integer.parseInt(String.valueOf(chatNewTopicRequest.locationId));
            item.topic = chatNewTopicRequest.topic;
            item.description = chatNewTopicRequest.description;
            item.applicationUserId = String.valueOf(chatNewTopicRequest.applicationUserId);
            item.status = "1";
            item.appStatus = "1";
            intent.putExtra(GeneralChatActivity.TOPIC_JSON, new Gson().toJson(item));
            startActivity(intent);

        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }

    @Override
    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Chat.value());
        getTopicList(true);
        getMaintenanceList();

    }

    private void getMaintenanceList() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    maintenanceList = MobileDataProvider.getInstance().getMaintenanceRequests();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
            }
        }.execute();
    }

    @Override
    public void onListFragmentInteraction(Topic item) {
        Intent intent = new Intent(getContext(), GeneralChatActivity.class);
        intent.putExtra(GeneralChatActivity.TOPIC_JSON, new Gson().toJson(item));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("--OnResume", "onResume: ");
        refresh();
    }

    @Override
    public void setFilter(int number) {
        if (number == 1) {
            //all
            tvFilterType.setText("All");
            List<Topic> filterList = new ArrayList<>();
            filterList.addAll(topicArrayList);

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No Topics available.\nSwipe down to refresh.");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        } else if (number == 2) {
            //mine
            tvFilterType.setText("Pending");

            List<Topic> filterList = new ArrayList<>();
            for (int i = 0; i < topicArrayList.size(); i++) {
                if (topicArrayList.get(i).status.equals(ChatRequestStatus.Pending.value())) {
                    filterList.add(topicArrayList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No pending topics available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }

        } else if (number == 3) {
            //fav
            tvFilterType.setText("Open");

            List<Topic> filterList = new ArrayList<>();
            for (int i = 0; i < topicArrayList.size(); i++) {
                if (topicArrayList.get(i).status.equals(ChatRequestStatus.Open.value())) {
                    filterList.add(topicArrayList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();


            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No open topics available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }

        } else if (number == 4) {
            //fav
            tvFilterType.setText("Close");

            List<Topic> filterList = new ArrayList<>();
            for (int i = 0; i < topicArrayList.size(); i++) {
                if (topicArrayList.get(i).status.equals(ChatRequestStatus.Close.value())) {
                    filterList.add(topicArrayList.get(i));
                }
            }

            _adapter.refresh(filterList);
            _adapter.notifyDataSetChanged();

            Utilities.showHide(_context, _feedback, _adapter.getItemCount() <= 0);
            Utilities.showHide(_context, _recyclerView, _adapter.getItemCount() > 0);
            if (filterList.size() == 0) {
                _feedback.setText("No closed topics available");
            } else {
                _feedback.setText(MobileDataProvider.mrsString);
            }
        }
    }
}