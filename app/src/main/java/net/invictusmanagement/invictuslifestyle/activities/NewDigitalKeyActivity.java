package net.invictusmanagement.invictuslifestyle.activities;

import static android.Manifest.permission.READ_CONTACTS;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.GuestEntryDoorsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.GuestEntryDoor;
import net.invictusmanagement.invictuslifestyle.models.QuickDigitalKeyResponse;
import net.invictusmanagement.invictuslifestyle.models.Service;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jagerfield.mobilecontactslibrary.ImportContacts;
import jagerfield.mobilecontactslibrary.ImportContactsAsync;

public class NewDigitalKeyActivity extends BaseActivity {

    private GuestEntryDoorsAdapter adapter;

    private class ContactHelper {
        public String name;
        public String email;
        public String phoneNumber;

        @Override
        public String toString() {
            if (phoneNumber != null && email != null) {
                return name + " (" + email + " - " + phoneNumber + ")";
            } else if (phoneNumber != null) {
                return name + " (" + phoneNumber + ")";
            } else if (email != null) {
                return name + " (" + email + ")";
            } else
                return name;
        }
    }

    private Service serviceTypes = new Service();
    private Spinner spnServiceType;
    private QuickDigitalKeyResponse quickDigitalKeyResponse;
    private Button _buttonQuickKey, _buttonSend;
    private AutoCompleteTextView _recipientEditText;
    private EditText _emailEditText, _mobileNumberEditText;
    private Calendar _fromDateTime;
    private TextView tvQuickKeyMessage;
    private EditText _fromDateEditText;
    private EditText _fromTimeEditText;
    private Calendar _toDateTime;
    private EditText _toDateEditText;
    private EditText _toTimeEditText;
    private EditText _notesEditText;
    private ProgressBar _progressBar;
    private Boolean _changesMade = false;
    private boolean isMobileValid = false;
    private boolean isEmailValid = false;
    private boolean isFormatted = true;
    private boolean isUnFormatted = false;
    private SharedPreferences sharedPreferences;
    private TextView txtTtlSelectDoor;
    private RecyclerView rvChooseDoor;

    private final TextWatcher _watcher = new TextWatcher() {
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
        setContentView(R.layout.activity_new_digital_key);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _fromDateTime = Calendar.getInstance();
        _toDateTime = Calendar.getInstance();
        _toDateTime.add(Calendar.DATE, 1);

        _buttonQuickKey = findViewById(R.id.buttonQuickKey);
        createQuickKey();

        _buttonSend = findViewById(R.id.buttonSend);
        createDigitalKey();

        _emailEditText = findViewById(R.id.email);
        tvQuickKeyMessage = findViewById(R.id.tvQuickKeyMessage);
        _mobileNumberEditText = findViewById(R.id.mobileNumber);
        txtTtlSelectDoor = findViewById(R.id.txtTtlSelectDoor);
        rvChooseDoor = findViewById(R.id.rvChooseDoor);

        _progressBar = findViewById(R.id.progress);
        spnServiceType = findViewById(R.id.spnServiceType);

        _recipientEditText = findViewById(R.id.recipent);
        _recipientEditText.addTextChangedListener(_watcher);
        _recipientEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactHelper helper = (ContactHelper) parent.getAdapter().getItem(position);
                _recipientEditText.setText(helper.name);
                _emailEditText.setText(helper.email);
                if (helper.phoneNumber != null)
                    if (helper.phoneNumber.length() == 10) {
                        _mobileNumberEditText.setText(Utilities.formatPhone(helper.phoneNumber));
                    } else {
                        _mobileNumberEditText.setText(helper.phoneNumber);
                    }
            }
        });
        populateAutoComplete();

        if (sharedPreferences.getBoolean("hasExtraIntegration", false)) {
            callWebServiceForGetDoors();
        }

        _mobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int filteredString = s.toString().replaceAll("[^0-9]", "").length();
                if (filteredString < 10) {
                    if (!isUnFormatted) {
                        isFormatted = false;
                        isUnFormatted = true;
                        _mobileNumberEditText.setText(s.toString().replaceAll("[^0-9]", ""));
                        _mobileNumberEditText.setSelection(_mobileNumberEditText.getText().length());
                    }

                }

                if (filteredString == 10) {
                    if (!isFormatted) {
                        isUnFormatted = false;
                        isFormatted = true;
                        _mobileNumberEditText.setText(Utilities.formatPhone(_mobileNumberEditText.getText().toString().trim()));
                        _mobileNumberEditText.setSelection(_mobileNumberEditText.getText().length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _fromDateEditText = findViewById(R.id.from_date);
        _fromDateEditText.setInputType(InputType.TYPE_NULL);
        _fromDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(_fromDateTime.getTime()));
        _fromDateEditText.addTextChangedListener(_watcher);
        _fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showDatePicker(NewDigitalKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
                    @Override
                    public void dateTimeChanged(Calendar date) {
                        _fromDateTime = date;
                        _fromDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(date.getTime()));

                        _toDateTime = _fromDateTime;
                        _toDateTime.add(Calendar.DATE, 1);
                        _toDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
                    }
                });
            }
        });
        _fromTimeEditText = findViewById(R.id.from_time);
        _fromTimeEditText.setInputType(InputType.TYPE_NULL);
        _fromTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_fromDateTime.getTime()));
        _fromTimeEditText.addTextChangedListener(_watcher);
        _fromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showTimePicker(NewDigitalKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
                    @Override
                    public void dateTimeChanged(Calendar date) {
                        _fromDateTime = date;
                        _fromTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));

                        _toDateTime = _fromDateTime;
                        _toDateTime.add(Calendar.DATE, 1);
                        _toTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
                    }
                });
            }
        });

        _toDateEditText = findViewById(R.id.to_date);
        _toDateEditText.setInputType(InputType.TYPE_NULL);
        _toDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
        _toDateEditText.addTextChangedListener(_watcher);
        _toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showDatePicker(NewDigitalKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
                    @Override
                    public void dateTimeChanged(Calendar date) {
                        _toDateTime = date;
                        _toDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(date.getTime()));
                    }
                });
            }
        });
        _toTimeEditText = findViewById(R.id.to_time);
        _toTimeEditText.setInputType(InputType.TYPE_NULL);
        _toTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
        _toTimeEditText.addTextChangedListener(_watcher);
        _toTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showTimePicker(NewDigitalKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
                    @Override
                    public void dateTimeChanged(Calendar date) {
                        _toDateTime = date;
                        _toTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));
                    }
                });
            }
        });

        _notesEditText = findViewById(R.id.notes);
        _notesEditText.addTextChangedListener(_watcher);

        setServiceTypes();

    }

    private void callWebServiceForGetDoors() {
        WebService.getInstance().getGuestEntry(new RestCallBack<List<GuestEntryDoor>>() {
            @Override
            public void onResponse(List<GuestEntryDoor> response) {
                if (response != null) {
                    if (response.size() > 0) {
                        txtTtlSelectDoor.setVisibility(View.VISIBLE);
                        rvChooseDoor.setVisibility(View.VISIBLE);
                        setAdapterForGuestEntryDoors(response);
                    } else {
                        txtTtlSelectDoor.setVisibility(View.GONE);
                        rvChooseDoor.setVisibility(View.GONE);
                    }
                } else {
                    txtTtlSelectDoor.setVisibility(View.GONE);
                    rvChooseDoor.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    private void setAdapterForGuestEntryDoors(List<GuestEntryDoor> response) {
        adapter = new GuestEntryDoorsAdapter(this, response);

//        rvChooseDoor.setHasFixedSize(true);
        rvChooseDoor.setLayoutManager(new LinearLayoutManager(this));
        rvChooseDoor.addItemDecoration(new DividerItemDecoration(rvChooseDoor.getContext(),
                DividerItemDecoration.VERTICAL));
        rvChooseDoor.setAdapter(adapter);
    }

    private void createDigitalKey() {
        _buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(_buttonSend);
                createNewDigitalKey();
            }
        });

    }

    private void createNewDigitalKey() {
        boolean cancel = false;
        View focusView = null;
        View focusView2 = null;

        _recipientEditText.setError(null);
        _emailEditText.setError(null);
        _mobileNumberEditText.setError(null);
        _fromDateEditText.setError(null);
        _fromTimeEditText.setError(null);
        _toDateEditText.setError(null);
        _toTimeEditText.setError(null);

        if (TextUtils.isEmpty(_recipientEditText.getText().toString())) {
            _recipientEditText.setError(getString(R.string.error_field_required));
            focusView = _recipientEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(_emailEditText.getText().toString())
                && TextUtils.isEmpty(_mobileNumberEditText.getText().toString())) {
            if (sharedPreferences.getBoolean("hasExtraIntegration", false)) {
                _emailEditText.setError(getString(R.string.error_field_required));
                _mobileNumberEditText.setError(getString(R.string.error_field_required));
            } else {
                _emailEditText.setError(getString(R.string.error_field_optional_required));
                _mobileNumberEditText.setError(getString(R.string.error_field_optional_required));
            }
            focusView2 = _emailEditText;
            focusView = _mobileNumberEditText;
            cancel = true;
        } else if (sharedPreferences.getBoolean("hasExtraIntegration", false)) {
            if (TextUtils.isEmpty(_emailEditText.getText().toString())) {
                _emailEditText.setError(getString(R.string.error_field_required));
                focusView2 = _emailEditText;
                cancel = true;
            } else if (TextUtils.isEmpty(_mobileNumberEditText.getText().toString())) {
                _mobileNumberEditText.setError(getString(R.string.error_field_required));
                focusView = _mobileNumberEditText;
                cancel = true;
            } else {
                if (Utilities.isValidEmail(_emailEditText.getText()) &&
                        _mobileNumberEditText.getText().toString().trim().length() == 14) {
                    isEmailValid = true;
                    isMobileValid = true;
                } else if (Utilities.isValidEmail(_emailEditText.getText())) {
                    isEmailValid = true;
                    isMobileValid = false;
                } else if (_mobileNumberEditText.getText().toString().trim().length() == 14) {
                    isEmailValid = false;
                    isMobileValid = true;
                } else {
                    if (!TextUtils.isEmpty(_emailEditText.getText())) {
                        if (!Utilities.isValidEmail(_emailEditText.getText())) {
                            _emailEditText.setError(getString(R.string.error_invalid_email));
                        } else {
                            _emailEditText.setError(getString(R.string.error_field_optional_required));
                        }
                        focusView = _emailEditText;
                    } else if (!TextUtils.isEmpty(_mobileNumberEditText.getText())) {
                        if (_mobileNumberEditText.getText().toString().trim().length() != 14) {
                            _mobileNumberEditText.setError(getString(R.string.error_invalid_mobile));
                        } else {
                            _mobileNumberEditText.setError(getString(R.string.error_field_optional_required));
                        }
                        focusView = _mobileNumberEditText;
                    } else {
                        _emailEditText.setError(getString(R.string.error_field_optional_required));
                        _mobileNumberEditText.setError(getString(R.string.error_field_optional_required));
                        focusView2 = _emailEditText;
                        focusView = _mobileNumberEditText;
                    }
                    isEmailValid = false;
                    isMobileValid = false;
                }

                if (!isMobileValid && !isEmailValid) {
                    cancel = true;
                }
            }

        } else if (!TextUtils.isEmpty(_emailEditText.getText()) ||
                !TextUtils.isEmpty(_mobileNumberEditText.getText())) {

            if (Utilities.isValidEmail(_emailEditText.getText()) &&
                    _mobileNumberEditText.getText().toString().trim().length() == 14) {
                isEmailValid = true;
                isMobileValid = true;
            } else if (Utilities.isValidEmail(_emailEditText.getText())) {
                isEmailValid = true;
                isMobileValid = false;
            } else if (_mobileNumberEditText.getText().toString().trim().length() == 14) {
                isEmailValid = false;
                isMobileValid = true;
            } else {
                if (!TextUtils.isEmpty(_emailEditText.getText())) {
                    if (!Utilities.isValidEmail(_emailEditText.getText())) {
                        _emailEditText.setError(getString(R.string.error_invalid_email));
                    } else {
                        _emailEditText.setError(getString(R.string.error_field_optional_required));
                    }
                    focusView = _emailEditText;
                } else if (!TextUtils.isEmpty(_mobileNumberEditText.getText())) {
                    if (_mobileNumberEditText.getText().toString().trim().length() != 14) {
                        _mobileNumberEditText.setError(getString(R.string.error_invalid_mobile));
                    } else {
                        _mobileNumberEditText.setError(getString(R.string.error_field_optional_required));
                    }
                    focusView = _mobileNumberEditText;
                } else {
                    _emailEditText.setError(getString(R.string.error_field_optional_required));
                    _mobileNumberEditText.setError(getString(R.string.error_field_optional_required));
                    focusView2 = _emailEditText;
                    focusView = _mobileNumberEditText;
                }

                isEmailValid = false;
                isMobileValid = false;
            }


            if (!isMobileValid && !isEmailValid) {
                cancel = true;
            }

        } else if (TextUtils.isEmpty(_fromDateEditText.getText().toString())) {
            _fromDateEditText.setError(getString(R.string.error_field_required));
            focusView = _fromDateEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(_fromTimeEditText.getText().toString())) {
            _fromTimeEditText.setError(getString(R.string.error_field_required));
            focusView = _fromTimeEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(_toDateEditText.getText().toString())) {
            _toDateEditText.setError(getString(R.string.error_field_required));
            focusView = _toDateEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(_toTimeEditText.getText().toString())) {
            _toTimeEditText.setError(getString(R.string.error_field_required));
            focusView = _toTimeEditText;
            cancel = true;
        } else if (convertToDateTime(_fromDateEditText.getText() + " " + _fromTimeEditText.getText()) == null) {
            _fromDateEditText.setError(getString(R.string.error_invalid_date_time));
            focusView = _fromDateEditText;
            cancel = true;
        } else if (convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText()) == null) {
            _toDateEditText.setError(getString(R.string.error_invalid_date_time));
            focusView = _toDateEditText;
            cancel = true;
        } else if (convertToDateTime(_fromDateEditText.getText() + " " + _fromTimeEditText.getText()).after(convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText()))) {
            _toDateEditText.setError(getString(R.string.error_from_date_time_after_to_date_time));
            _toTimeEditText.setError(getString(R.string.error_from_date_time_after_to_date_time));
            focusView = _toDateEditText;
            cancel = true;
        }

        if (!cancel) {
            showAlertDialog(1);
        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }

            if (focusView2 != null) {
                focusView2.requestFocus();
            }

        }
    }

    private void callAPIForCreateDigitalKey(int deliverTo) {
        DigitalKey key = new DigitalKey();
        key.setRecipient(_recipientEditText.getText().toString());
        if (isEmailValid) {
            key.setEmail(_emailEditText.getText().toString());
        } else {
            key.setEmail(null);
        }
        if (isMobileValid) {
            key.setPhoneNumber(_mobileNumberEditText.getText().toString());
        } else {
            key.setPhoneNumber(null);
        }

        if (adapter == null) {
            key.setSelectedEntry(null);
        } else if (adapter.getSelectedEntry() == null) {
            key.setSelectedEntry(null);
        } else if (adapter.getSelectedEntry().size() > 0) {
            key.setSelectedEntry(adapter.getSelectedEntry());
        } else {
            key.setSelectedEntry(null);
        }
        key.setFromUtc(convertToDateTime(_fromDateEditText.getText() + " " + _fromTimeEditText.getText()));
        key.setToUtc(convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText()));
        key.setNotes(_notesEditText.getText().toString());
        key.setQuickKey(false);
        key.setToPackageCenter(deliverTo != 0);

        new AsyncTask<DigitalKey, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                Utilities.hideKeyboard(NewDigitalKeyActivity.this);
                ProgressDialog.showProgress(NewDigitalKeyActivity.this);
            }

            @Override
            protected Boolean doInBackground(DigitalKey... args) {
                try {
                    MobileDataProvider.getInstance().createDigitalKey(args[0]);
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
                    Toast.makeText(NewDigitalKeyActivity.this, "Digital key successfully sent.", Toast.LENGTH_LONG).show();
                } else {
                    /*item.setEnabled(true);*/
                    Toast.makeText(NewDigitalKeyActivity.this, "Error while creating digital key", Toast.LENGTH_LONG).show();
                }
                setResult(1);
                finish();
            }

        }.execute(key);

    }

    private void showAlertDialog(int i) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Deliver TO:");
        String[] items = {"Unit", "Package Center/Office"};
        int checkedItem = 0;
        final int[] selectedItem = {0};
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectedItem[0] = 0;
                        break;
                    case 1:
                        selectedItem[0] = 1;
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (i == 1) {
                    callAPIForCreateDigitalKey(selectedItem[0]);
                } else {
                    createNewQuickKey(selectedItem[0]);
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void createQuickKey() {
        _buttonQuickKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(_buttonQuickKey);
                showAlertDialog(0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (_changesMade)
                Utilities.showDiscardChangesDialog(this);
            else
                NavUtils.navigateUpFromSameTask(NewDigitalKeyActivity.this);
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

    @SuppressLint("CheckResult")
    private void populateAutoComplete() {
        Dexter.withActivity(this).withPermissions(
                READ_CONTACTS).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    fetchContacts();
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(getApplicationContext(), "Please accept all permissions from settings.", Toast.LENGTH_LONG).show();
                    openSetting();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                if (!permissions.isEmpty()) {
                    token.continuePermissionRequest();
                }
            }

        }).onSameThread().check();


    }

    private void fetchContacts() {

        ImportContacts importContacts = new ImportContacts(this);
        ArrayList<ContactHelper> recipients = new ArrayList<>();
        ArrayList<jagerfield.mobilecontactslibrary.Contact.Contact> listItem = importContacts.getContacts();
        new ImportContactsAsync(this, new ImportContactsAsync.ICallback() {
            @Override
            public void mobileContacts(ArrayList<jagerfield.mobilecontactslibrary.Contact.Contact> arrayList) {

                for (int i = 0; i < arrayList.size(); i++) {
                    ContactHelper helper = new ContactHelper();
                    if (arrayList.get(i).getNumbers().toString().length() > 0) {
                        helper.name = arrayList.get(i).getDisplaydName();
                        if (arrayList.get(i).getEmails().size() > 0) {
                            helper.email = arrayList.get(i).getEmails().get(0).getEmail();
                        }
                        if (arrayList.get(i).getNumbers().size() > 0) {
                            String phoneNumber = arrayList.get(i).getNumbers().get(0).getNormalizedNumber();
                            phoneNumber = phoneNumber.replace("(", "");
                            phoneNumber = phoneNumber.replace(")", "");
                            phoneNumber = phoneNumber.replace("[", "");
                            phoneNumber = phoneNumber.replace("]", "");
                            phoneNumber = phoneNumber.replace("-", "");
                            phoneNumber = phoneNumber.replace(" ", "");
                            if (phoneNumber.startsWith("+")) {
                                if (phoneNumber.length() > 12) {
                                    phoneNumber = phoneNumber.substring(3);
                                } else if (phoneNumber.length() > 11) {
                                    phoneNumber = phoneNumber.substring(2);
                                } else {
                                    phoneNumber = phoneNumber.substring(1);
                                }

                            }
                            helper.phoneNumber = phoneNumber;
                        }
                        recipients.add(helper);
                    }

                }
                ArrayList<ContactHelper> recipients2 = new ArrayList<>();
                recipients2 = recipients;
                for (int i = 0; i < recipients.size(); i++) {
                    if (recipients.get(i).email == null && recipients.get(i).phoneNumber == null) {
                        recipients2.remove(i);
                    }
                }
                _recipientEditText.setAdapter(new ArrayAdapter<ContactHelper>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, recipients2));
            }

        }).execute();


    }

    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }

    private Date convertToDateTime(String input) {

        Date result = null;
        try {
            result = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(input);
        } catch (ParseException ex) {
            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
        }
        return result;
    }

    public void createNewQuickKey(int deliverTo) {
        DigitalKey key = new DigitalKey();
        key.setQuickKey(true);

        for (int i = 0; i < serviceTypes.serviceTypes.size(); i++) {
            if (spnServiceType.getSelectedItem().toString().equals(serviceTypes.serviceTypes.get(i).name)) {
                key.setServiceTypeId(serviceTypes.serviceTypes.get(i).id);
                break;
            }
        }
        key.setToPackageCenter(deliverTo != 0);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(NewDigitalKeyActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().createQuickDigitalKey(key, NewDigitalKeyActivity.this);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                dialogForQuickKey();
            }
        }.execute();
    }


    public void setString(QuickDigitalKeyResponse digitalKeyResponse) {
        quickDigitalKeyResponse = digitalKeyResponse;
    }

    private void dialogForQuickKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewDigitalKeyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quick_key, null);
        builder.setView(view)
                .setTitle(quickDigitalKeyResponse.recipient);

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(quickDigitalKeyResponse.createdUtc));
        ((TextView) view.findViewById(R.id.tvQuickKey)).setText(quickDigitalKeyResponse.key);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("COPY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ClipboardManager clipboard =
                        (ClipboardManager) NewDigitalKeyActivity.this
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                long diff = quickDigitalKeyResponse.toUtc.getTime()
                        - quickDigitalKeyResponse.fromUtc.getTime();
                int hours = (int) (diff / (1000 * 60 * 60));
                String keyText = HomeFragment.userName + " sent you this quick key:\n"
                        + quickDigitalKeyResponse.key + " \n" +
                        "Find " + HomeFragment.userName + " using the directory button of the" +
                        " kiosk to enter this key. \n"
                        + "Key expires in " + hours + " hours ("
                        + convertTime(quickDigitalKeyResponse.toUtc) + ")";
                ClipData clip = ClipData.newPlainText("Key", keyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(NewDigitalKeyActivity.this,
                        "Enter this quick key in my DIRECTORY PROFILE on the kiosk.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setCancelable(false).create().show();
    }

    private String convertTime(Date datePasssed) {
        /*"10:30 PM"*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm a");
        return displayFormat.format(datePasssed);
    }

    private String convertDate(Date datePasssed) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        return displayFormat.format(datePasssed);
    }

    private void setServiceTypes() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(NewDigitalKeyActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    serviceTypes = MobileDataProvider.getInstance().getDigitalServiceType();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                if (serviceTypes.serviceTypes != null && serviceTypes.keyDurationMessage != null) {
                    tvQuickKeyMessage.setText(serviceTypes.keyDurationMessage);
                    ArrayList<String> catString = new ArrayList<>();
                    for (int i = 0; i < serviceTypes.serviceTypes.size(); i++) {
                        catString.add(serviceTypes.serviceTypes.get(i).name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, catString);
                    spnServiceType.setAdapter(adapter);
                    spnServiceType.setSelection(0);
                    _buttonQuickKey.setEnabled(true);
                }


                if (!success) {
                    Toast.makeText(getApplicationContext(), "Unable to get service types. Please try again later.", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        }.execute();


    }

}
