package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ChooseServeyAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.ChooseSurveyItem;
import net.invictusmanagement.invictuslifestyle.models.SurveyList;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.ArrayList;
import java.util.List;

public class ChooseSurveyActivity extends AppCompatActivity implements ChooseSurveyItem {

    private RecyclerView rvChooseSurvey;
    private TextView tvNoSurvey;
    private ChooseServeyAdapter chooseServeyAdapter;
    private List<SurveyList> surveyLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey);
        toolBar();
        /*testdata();*/
        initControls();
        setDataInAdapter();
        callSurveyList();
    }

    private void setDataInAdapter() {
        rvChooseSurvey.setAdapter(chooseServeyAdapter);
        chooseServeyAdapter.refresh(surveyLists);
    }

    private void testdata() {
        surveyLists = new ArrayList<>();

        SurveyList surveyList = new SurveyList();
        surveyList.surveyName = "Test";
        surveyLists.add(surveyList);

        SurveyList surveyList2 = new SurveyList();
        surveyList2.surveyName = "Test2";
        surveyLists.add(surveyList2);

    }

    private void initControls() {
        initViews();
        chooseServeyAdapter = new ChooseServeyAdapter(ChooseSurveyActivity.this, ChooseSurveyActivity.this);

    }

    private void initViews() {
        rvChooseSurvey = findViewById(R.id.rvChooseSurvey);
        tvNoSurvey = findViewById(R.id.tvNoSurvey);
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void selectedSurvey(int position, SurveyList item) {
        Intent i = new Intent(ChooseSurveyActivity.this, SurvayActivity.class);
        i.putExtra(SurvayActivity.SURVAYITEM, new Gson().toJson(item));
        startActivityForResult(i, 1);
    }

    public void callSurveyList() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*_swipeRefreshLayout.setRefreshing(true);*/
                ProgressDialog.showProgress(ChooseSurveyActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    surveyLists = MobileDataProvider.getInstance().getSurveyList();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialog.dismissProgress();
                    }
                }, 2000);

                if (success) {
                    if (surveyLists.size() > 0) {
                        setDataInAdapter();
                    }
                }
                Utilities.showHide(ChooseSurveyActivity.this, tvNoSurvey, surveyLists.size() < 1);
                Utilities.showHide(ChooseSurveyActivity.this, rvChooseSurvey, surveyLists.size() > 0);
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            finish();
        }
    }

}