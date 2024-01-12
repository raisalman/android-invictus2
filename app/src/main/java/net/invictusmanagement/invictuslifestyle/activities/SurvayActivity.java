package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.MainPagerAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.MultipleChoiceAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.SingleChoiceAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.NonSwipeableViewPager;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.enum_utils.SurveyQuestionEnum;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.MultipleChoiceInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Survey;
import net.invictusmanagement.invictuslifestyle.models.SurveyAnswer;
import net.invictusmanagement.invictuslifestyle.models.SurveyList;
import net.invictusmanagement.invictuslifestyle.models.SurveyResult;
import net.invictusmanagement.invictuslifestyle.models.SurveyResultLocal;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.ArrayList;
import java.util.List;

public class SurvayActivity extends AppCompatActivity implements MultipleChoiceInteractionListener {

    public static String SURVAYITEM = "SURVAYITEM";
    private Button btnPrevious, btnNext;
    private NonSwipeableViewPager container;
    private MainPagerAdapter pagerAdapter = null;
    private List<Survey> surveyArrayList = new ArrayList<>();
    private ArrayList<SurveyResultLocal> surveyResultLocalArrayList = new ArrayList<>();
    private RecyclerView rvMultipleChoice;
    private RecyclerView rvSingleChoice;
    private MultipleChoiceAdapter multipleChoiceAdapter;
    private SingleChoiceAdapter singleChoiceAdapter;
    private int _currentIndex = 0;
    private TextView tvAnsCheck;
    private SurveyList item;
    private LinearLayoutCompat llQuestion;
    private TextView tvNoSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servay);
        toolBar();
        getBundleData();
        /*testData();*/
        initControls();

    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            item = new Gson().fromJson(getIntent().getStringExtra(SURVAYITEM), new TypeToken<SurveyList>() {
            }.getType());

            callAPi(item);
        }
    }

    private void callAPi(SurveyList item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*_swipeRefreshLayout.setRefreshing(true);*/
                ProgressDialog.showProgress(SurvayActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    surveyArrayList = MobileDataProvider.getInstance().getSurvey(item.id);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                if (success) {
                    if (surveyArrayList.size() > 0) {
                        if (surveyArrayList.size() == 1) {
                            btnPrevious.setVisibility(View.INVISIBLE);
                            btnNext.setVisibility(View.VISIBLE);
                            btnNext.setText("Submit");
                        } else {
                            btnNext.setText("Next");
                        }
                        setDataInAdapter();
                    }
                }
                Utilities.showHide(SurvayActivity.this, tvNoSurvey, surveyArrayList.size() < 1);
                Utilities.showHide(SurvayActivity.this, llQuestion, surveyArrayList.size() > 0);
            }
        }.execute();
    }

    private void submitQuiz(ArrayList<SurveyResult> item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*_swipeRefreshLayout.setRefreshing(true);*/
                ProgressDialog.showProgress(SurvayActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().submitQuiz(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                if (success) {
                    successFullDialog();
                } else {
                    Toast.makeText(SurvayActivity.this, "Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void testData() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Test 1");
        strings.add("Test 2");
        strings.add("Test 3");
        strings.add("Test 4");
        strings.add("Test 5");

        ArrayList<SurveyAnswer> answerList = new ArrayList<>();
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.answer = strings.get(0);
        answerList.add(surveyAnswer);

        SurveyAnswer surveyAnswer1 = new SurveyAnswer();
        surveyAnswer1.answer = strings.get(1);
        answerList.add(surveyAnswer1);

        SurveyAnswer surveyAnswer2 = new SurveyAnswer();
        surveyAnswer2.answer = strings.get(2);
        answerList.add(surveyAnswer2);


        Survey survey1 = new Survey();
        survey1.id = 1;
        survey1.answerList = answerList;
        surveyArrayList.add(survey1);

        Survey survey2 = new Survey();
        survey2.id = 2;
        /*survey2.isMultipleChoice = true;*/
        survey2.answerList = answerList;
        surveyArrayList.add(survey2);

        Survey survey3 = new Survey();
        survey3.id = 3;
        survey3.answerList = answerList;
        surveyArrayList.add(survey3);

        Survey survey4 = new Survey();
        survey4.id = 4;
        /*survey4.isMultipleChoice = true;*/
        survey4.answerList = answerList;
        surveyArrayList.add(survey4);

        Survey survey5 = new Survey();
        survey5.id = 5;
        survey5.answerList = answerList;
        surveyArrayList.add(survey5);
    }

    private void initControls() {
        initViews();
        onClickListeners();

    }


    @SuppressLint("SetTextI18n")
    private void initViews() {
        tvAnsCheck = findViewById(R.id.tvAnsCheck);
        tvNoSurvey = findViewById(R.id.tvNoSurvey);
        llQuestion = findViewById(R.id.llQuestion);
        container = findViewById(R.id.container);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);


        pagerAdapter = new MainPagerAdapter();
        container.setAdapter(pagerAdapter);

        /*setDataInAdapter();*/
    }

    private void setDataInAdapter() {
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < surveyArrayList.size(); i++) {
            ConstraintLayout v0;

            if (surveyArrayList.get(i).questionTypeId == Integer.parseInt(SurveyQuestionEnum.MultipleChoice.value())) {
                v0 = (ConstraintLayout) inflater.inflate(R.layout.layout_multiple_choice, null);
                TextView tvQuestions = v0.findViewById(R.id.tvQuestions);
                rvMultipleChoice = v0.findViewById(R.id.rvMultipleChoice);
                multipleChoiceAdapter = new MultipleChoiceAdapter(SurvayActivity.this, SurvayActivity.this, surveyArrayList.get(i));
                tvQuestions.setText((i + 1) + ". " + surveyArrayList.get(i).questionName);
                rvMultipleChoice.setAdapter(multipleChoiceAdapter);
                multipleChoiceAdapter.refresh(i, surveyArrayList);

            } else {
                v0 = (ConstraintLayout) inflater.inflate(R.layout.layout_single_choice, null);
                TextView tvQuestions = v0.findViewById(R.id.tvQuestions);
                rvSingleChoice = v0.findViewById(R.id.rvSingleChoice);
                singleChoiceAdapter = new SingleChoiceAdapter(SurvayActivity.this);
                tvQuestions.setText((i + 1) + ". " + surveyArrayList.get(i).questionName);
                rvSingleChoice.setAdapter(singleChoiceAdapter);
                singleChoiceAdapter.refresh(i, surveyArrayList);


            }
            pagerAdapter.addView(v0);
        }
        pagerAdapter.notifyDataSetChanged();

        viewpagerListener();
    }


    private void viewpagerListener() {


        container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                _currentIndex = position;
                if (multipleChoiceAdapter != null) {
                    multipleChoiceAdapter.setId(surveyArrayList.get(position).id);
                    multipleChoiceAdapter.setSurvey(surveyArrayList.get(position));
                }

                if (singleChoiceAdapter != null) {
                    singleChoiceAdapter.setId(surveyArrayList.get(position).id);
                    singleChoiceAdapter.setSurvey(surveyArrayList.get(position));
                }


                if (surveyArrayList.size() == 1) {
                    btnPrevious.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText("Submit");
                } else {
                    if (position == 0) {
                        btnPrevious.setVisibility(View.INVISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        btnNext.setText("Next");
                    } else if (position == surveyArrayList.size() - 1) {
                        btnPrevious.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        btnNext.setText("Submit");
                    } else {
                        btnPrevious.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        btnNext.setText("Next");
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (multipleChoiceAdapter != null) {
            multipleChoiceAdapter.setId(surveyArrayList.get(_currentIndex).id);
            multipleChoiceAdapter.setSurvey(surveyArrayList.get(_currentIndex));
        }

        if (singleChoiceAdapter != null) {
            singleChoiceAdapter.setId(surveyArrayList.get(_currentIndex).id);
            singleChoiceAdapter.setSurvey(surveyArrayList.get(_currentIndex));
        }

    }

    private void onClickListeners() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousPage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nextOrSubmitQuestion(_currentIndex == surveyArrayList.size() - 1);
            }
        });
    }

    private void nextOrSubmitQuestion(boolean b) {
        if (multipleChoiceAdapter != null && singleChoiceAdapter != null) {
            if (multipleChoiceAdapter.getSurvey().questionTypeId == Integer.parseInt(SurveyQuestionEnum.MultipleChoice.value())) {
                if (multipleChoiceAdapter.getSurvey().checkboxCount > 0) {
                    if (b) {
                        setMultipleChoiceAns();
                        submitSurvey();
                    } else {
                        setMultipleChoiceAns();
                        nextPage();
                    }
                } else {
                    Toast.makeText(SurvayActivity.this, "Please attempt question", Toast.LENGTH_LONG).show();
                }
            } else {
                if (singleChoiceAdapter.getSurvey().isAnswered) {
                    if (b)
                        submitSurvey();
                    else
                        nextPage();
                } else {
                    Toast.makeText(SurvayActivity.this, "Please attempt question", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (multipleChoiceAdapter == null) {
                if (singleChoiceAdapter.getSurvey().isAnswered) {
                    if (b)
                        submitSurvey();
                    else
                        nextPage();
                } else {
                    Toast.makeText(SurvayActivity.this, "Please attempt question", Toast.LENGTH_LONG).show();
                }
            } else if (singleChoiceAdapter == null) {
                if (multipleChoiceAdapter.getSurvey().questionTypeId == Integer.parseInt(SurveyQuestionEnum.MultipleChoice.value())) {
                    if (multipleChoiceAdapter.getSurvey().checkboxCount > 0) {
                        if (b) {
                            setMultipleChoiceAns();
                            submitSurvey();
                        } else {
                            setMultipleChoiceAns();
                            nextPage();
                        }
                    } else {
                        Toast.makeText(SurvayActivity.this, "Please attempt question", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    public void setSingleChoiceAns(AppCompatRadioButton appCompatRadioButton, String id) {
        surveyArrayList.get(_currentIndex).isAnswered = true;
        SurveyResultLocal surveyResultLocal = new SurveyResultLocal();
        surveyResultLocal.questionNumber = String.valueOf(_currentIndex);
        surveyResultLocal.questionTypeId = surveyArrayList.get(_currentIndex).questionTypeId;
        surveyResultLocal.id = surveyArrayList.get(_currentIndex).id;
        surveyResultLocal.surveyFormId = surveyArrayList.get(_currentIndex).surveyFormId;
        surveyResultLocal.isAnswered = surveyArrayList.get(_currentIndex).isAnswered;
        surveyResultLocal.singleChoice = appCompatRadioButton.getText().toString();
        surveyResultLocal.singleChoiceId = id;
        if (_currentIndex >= surveyResultLocalArrayList.size()) {
            //index not exists
            surveyResultLocalArrayList.add(_currentIndex, surveyResultLocal);
        } else {
            // index exists
            surveyResultLocalArrayList.set(_currentIndex, surveyResultLocal);
        }

    }

    private void setMultipleChoiceAns() {
        surveyArrayList.get(_currentIndex).isAnswered = true;
        SurveyResultLocal surveyResultLocal = new SurveyResultLocal();
        surveyResultLocal.questionNumber = String.valueOf(_currentIndex);
        surveyResultLocal.questionTypeId = surveyArrayList.get(_currentIndex).questionTypeId;
        surveyResultLocal.id = surveyArrayList.get(_currentIndex).id;
        surveyResultLocal.surveyFormId = surveyArrayList.get(_currentIndex).surveyFormId;
        surveyResultLocal.isAnswered = surveyArrayList.get(_currentIndex).isAnswered;
        surveyResultLocal.isMultipleChoice = true;
        surveyResultLocal.multipleChoice = multipleChoiceAdapter.getSurvey().multiChoiceAns;
        surveyResultLocal.multipleChoiceId = multipleChoiceAdapter.getSurvey().multiChoiceAnsId;
        if (_currentIndex >= surveyResultLocalArrayList.size()) {
            //index not exists
            surveyResultLocalArrayList.add(_currentIndex, surveyResultLocal);
        } else {
            // index exists
            surveyResultLocalArrayList.set(_currentIndex, surveyResultLocal);
        }

    }

    private void submitSurvey() {
        StringBuilder finalString = new StringBuilder();
        for (int i = 0; i < surveyResultLocalArrayList.size(); i++) {
            if (surveyResultLocalArrayList.get(i).isMultipleChoice) {
                finalString.append(surveyResultLocalArrayList.get(i).multipleChoice.toString()).append(" -- ").append(surveyResultLocalArrayList.get(i).multipleChoiceId.toString()).append("\n");
            } else {
                finalString.append(surveyResultLocalArrayList.get(i).singleChoice.toString()).append(" -- ").append(surveyResultLocalArrayList.get(i).singleChoiceId.toString()).append("\n");
            }
        }
        tvAnsCheck.setText(finalString.toString());

        ArrayList<SurveyResult> surveyResultArrayList = new ArrayList<>();

        for (int j = 0; j < surveyResultLocalArrayList.size(); j++) {
            SurveyResult surveyResult = new SurveyResult();
            surveyResult.applicationUserId = HomeFragment.userId;
            surveyResult.surveyFormId = surveyResultLocalArrayList.get(j).surveyFormId;
            surveyResult.surveyFormQuestionId = surveyResultLocalArrayList.get(j).id;
            if (surveyResultLocalArrayList.get(j).isMultipleChoice) {
                String ans = String.valueOf(surveyResultLocalArrayList.get(j).multipleChoiceId);
                if (ans.contains("[")) {
                    ans = ans.replace("[", "");
                }
                if (ans.contains("]")) {
                    ans = ans.replace("]", "");
                }
                surveyResult.answersList = ans;
            } else {
                surveyResult.answersList = String.valueOf(surveyResultLocalArrayList.get(j).singleChoiceId);
            }
            surveyResultArrayList.add(surveyResult);

        }

        submitQuiz(surveyResultArrayList);

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

    private void nextPage() {
        container.setCurrentItem(getItem(+1), true);
    }

    private void previousPage() {
        container.setCurrentItem(getItem(-1), true);
    }

    private int getItem(int i) {
        return _currentIndex + i;
    }

    @Override
    public void onListFragmentInteraction(CheckBox item) {

    }

    @Override
    public void onBackPressed() {
        if (surveyArrayList.size() > 0) {
            AlertDialog();
        } else {
            setResult(0);
            finish();
        }

    }

    public void successFullDialog() {
        new AlertDialog.Builder(SurvayActivity.this)
                .setCancelable(false)
                .setMessage("Survey submitted successfully!!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(1);
                        finish();
                    }
                }).create().show();
    }

    private void AlertDialog() {
        new AlertDialog.Builder(SurvayActivity.this)
                .setCancelable(false)
                .setMessage("Are  you sure you want to leave this survey?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(0);
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //dismiss
            }
        }).create().show();
    }
}