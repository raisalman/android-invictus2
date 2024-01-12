package net.invictusmanagement.invictuslifestyle.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.Feedback;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

public class NewFeedbackActivity extends BaseActivity {

    private ProgressBar _progressBar;
    private RadioGroup _typeRadioGroup;
    private EditText _messageEditText;
    private Boolean _changesMade = false;
    private TextWatcher _watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            _changesMade = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        _typeRadioGroup = (RadioGroup) findViewById(R.id.feedback_type);
        _messageEditText = (EditText) findViewById(R.id.message);
        _messageEditText.addTextChangedListener(_watcher);
        _progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (_changesMade)
                    Utilities.showDiscardChangesDialog(this);
                else
                    NavUtils.navigateUpFromSameTask(NewFeedbackActivity.this);
                return true;

            case R.id.action_send:
                item.setEnabled(false);
                boolean cancel = false;
                View focusView = null;

                _messageEditText.setError(null);

                if (TextUtils.isEmpty(_messageEditText.getText().toString())) {
                    _messageEditText.setError(getString(R.string.error_field_required));
                    focusView = _messageEditText;
                    cancel = true;
                }

                if (!cancel) {
                    Feedback feedback = new Feedback();
                    int index = _typeRadioGroup.indexOfChild(findViewById(_typeRadioGroup.getCheckedRadioButtonId()));
                    feedback.type = Feedback.Type.values()[index];
                    feedback.message = _messageEditText.getText().toString();

                    new AsyncTask<Feedback, Void, Boolean>() {

                        @Override
                        protected void onPreExecute() {
                            Utilities.hideKeyboard(NewFeedbackActivity.this);
                            Utilities.showHide(NewFeedbackActivity.this, _progressBar, true);
                        }

                        @Override
                        protected Boolean doInBackground(Feedback... args) {
                            try {
                                MobileDataProvider.getInstance().createFeedback(args[0]);
                                return true;
                            } catch (Exception ex) {
                                Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean success) {
                            if (success) {
                                Toast.makeText(NewFeedbackActivity.this, "Feedback successfully sent.", Toast.LENGTH_LONG).show();
                                setResult(1);
                                finish();
                            } else {
                                item.setEnabled(true);
                                Toast.makeText(NewFeedbackActivity.this, "Feedback creation failed.  Please try again later.", Toast.LENGTH_LONG).show();
                            }
                            Utilities.showHide(NewFeedbackActivity.this, _progressBar, false);
                        }

                    }.execute(feedback);

                } else {
                    focusView.requestFocus();
                    item.setEnabled(true);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (_changesMade)
            Utilities.showDiscardChangesDialog(this);
        else
            super.onBackPressed();
    }
}
