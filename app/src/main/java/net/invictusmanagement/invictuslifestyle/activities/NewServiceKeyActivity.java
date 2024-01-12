package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration;
import com.abedelazizshe.lightcompressorlibrary.config.Configuration;
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation;
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.GuestEntryDoorsAdapter;
import net.invictusmanagement.invictuslifestyle.adapters.MRSImagesVideosAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.AddTechnicianDialog;
import net.invictusmanagement.invictuslifestyle.customviews.AddVendorDialog;
import net.invictusmanagement.invictuslifestyle.customviews.FilePath;
import net.invictusmanagement.invictuslifestyle.customviews.FileUtils;
import net.invictusmanagement.invictuslifestyle.customviews.PickFileFromDevice;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.DeleteSingleMRSItem;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddTechnicianDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddVendorDialogClick;
import net.invictusmanagement.invictuslifestyle.models.GuestEntryDoor;
import net.invictusmanagement.invictuslifestyle.models.MRSItems;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.models.ServiceKeyRepeatOptions;
import net.invictusmanagement.invictuslifestyle.models.Vendors;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class NewServiceKeyActivity extends BaseActivity implements DeleteSingleMRSItem,
        SetOnAddVendorDialogClick, SetOnAddTechnicianDialogClick {

    private Button _buttonSend;
    private Calendar _fromDateTime;
    private EditText _fromDateEditText;
    private EditText _fromTimeEditText;
    private Calendar _toDateTime;
    private EditText _toDateEditText;
    private EditText _toTimeEditText;
    private EditText _notesEditText;
    private EditText _txtCompanyName;
    private EditText _txtTechnician;
    private ImageView _switchEndDate;
    private TextView _txtSwitchEndDate;
    private ImageView _imgInfoEndDate;
    private TextInputLayout _tilEndDate;
    private ImageView _switchTime;
    private TextView _txtSwitchTime;
    private ImageView _imgInfoTime;
    private LinearLayout _llTimeSection;
    private ImageView _imgInfoRepeat;
    private Spinner _spnRepeat;
    private TextView _txtRepeatOptions;
    private TextView _spnOptions;
    private View viewSpnOptions;
    private ProgressBar _progressBar;
    private SharedPreferences sharedPreferences;

    private boolean isEndDateActive = true;
    private boolean isTimeSelectionActive = true;
    private Boolean _changesMade = false;

    private boolean isEdit = false;
    private ServiceKey editServiceKey = new ServiceKey();
    private PickFileFromDevice pickFileFromDevice;
    private LinearLayout rlPriorUpload;
    private RecyclerView rvPriorImages;
    private final ArrayList<MRSItems> mrsItemsArrayList = new ArrayList();
    private final ArrayList<Uri> videoUri = new ArrayList();
    private MRSImagesVideosAdapter mrsImagesVideosAdapter;

    private GuestEntryDoorsAdapter adapter;
    private TextView txtTtlSelectDoor;
    private RecyclerView rvChooseDoor;
    private TextView tvAddVendor;
    private TextView tvAddTechnician;

    private AddVendorDialog vendorDialog = null;
    private AddTechnicianDialog technicianDialog = null;
    private long vendorMappingId;

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
        setContentView(R.layout.activity_new_service_key);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (getIntent().getExtras() != null) {
            isEdit = true;
            editServiceKey = (ServiceKey) getIntent().getSerializableExtra("SERVICE_KEY");
            vendorMappingId = Long.parseLong(editServiceKey.getCompanyId());
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isEdit) {
            toolbar.setTitle("Edit Service Key");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        setSpinnerRepeat();
        callWebServiceForGetDoors();
    }

    private void initViews() {
        _fromDateTime = Calendar.getInstance();
        _toDateTime = Calendar.getInstance();
        _toDateTime.add(Calendar.DATE, 1);

        tvAddTechnician = findViewById(R.id.tvAddTechnician);
        tvAddVendor = findViewById(R.id.tvAddVendor);

        SpannableString content = new SpannableString(tvAddVendor.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvAddVendor.setText(content);

        content = new SpannableString(tvAddTechnician.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvAddTechnician.setText(content);

        _buttonSend = findViewById(R.id.buttonSend);
        vendorDialog = new AddVendorDialog(this);
        technicianDialog = new AddTechnicianDialog(this);

        onClickListner();
        _progressBar = findViewById(R.id.progress);

        pickFileFromDevice = new PickFileFromDevice(NewServiceKeyActivity.this,
                NewServiceKeyActivity.this);
        rvPriorImages = findViewById(R.id.rvPriorImages);
        mrsImagesVideosAdapter = new MRSImagesVideosAdapter(this,
                mrsItemsArrayList, this);
        rvPriorImages.setAdapter(mrsImagesVideosAdapter);

        _txtCompanyName = findViewById(R.id.txtCompanyName);
        _txtCompanyName.setInputType(InputType.TYPE_NULL);
        _txtCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewServiceKeyActivity.this,
                        VendorSelectionActivity.class);
                intent.putExtra("isVendor", true);
                activityForResultLauncher.launch(intent);
            }
        });

        _fromDateEditText = findViewById(R.id.from_date);
        _fromDateEditText.setInputType(InputType.TYPE_NULL);
        _fromDateEditText.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(_fromDateTime.getTime()));
        _fromDateEditText.addTextChangedListener(_watcher);
        _fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showDatePicker(NewServiceKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
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
                Utilities.showTimePicker(NewServiceKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
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
                Utilities.showDatePickerWithMaxDate(NewServiceKeyActivity.this, _toDateTime, new Utilities.onDateTimePickerChangedListener() {
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
        _toDateTime.add(Calendar.HOUR, 2);
        _toTimeEditText.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
                .format(_toDateTime.getTime()));
        _toTimeEditText.addTextChangedListener(_watcher);
        _toTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.showTimePicker(NewServiceKeyActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
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
        _txtTechnician = findViewById(R.id.txtTechnician);
        _txtTechnician.setInputType(InputType.TYPE_NULL);
        _txtTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewServiceKeyActivity.this,
                        TechnicianSelectionActivity.class);
                intent.putExtra("vendorMappingId", vendorMappingId);
                intent.putExtra("isVendor", false);
                activityForResultLauncherTechnician.launch(intent);
            }
        });
        _switchEndDate = findViewById(R.id.switchEndDate);
        _txtSwitchEndDate = findViewById(R.id.txtSwitchEndDate);
        _imgInfoEndDate = findViewById(R.id.imgInfoEndDate);
        _tilEndDate = findViewById(R.id.tilEndDate);
        _switchTime = findViewById(R.id.switchTime);
        _txtSwitchTime = findViewById(R.id.txtSwitchTime);
        _imgInfoTime = findViewById(R.id.imgInfoTime);
        _llTimeSection = findViewById(R.id.llTimeSection);
        _imgInfoRepeat = findViewById(R.id.imgInfoRepeat);
        _spnRepeat = findViewById(R.id.spnRepeat);
        _txtRepeatOptions = findViewById(R.id.txtRepeatOptions);
        _spnOptions = findViewById(R.id.spnOptions);
        viewSpnOptions = findViewById(R.id.viewSpnOption);
        rlPriorUpload = findViewById(R.id.rlPriorUpload);

        txtTtlSelectDoor = findViewById(R.id.txtTtlSelectDoor);
        rvChooseDoor = findViewById(R.id.rvChooseDoor);

        _switchEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchEndDateVisibility(!isEndDateActive);
            }
        });

        _switchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTimeVisibility(!isTimeSelectionActive);
            }
        });

        if (isEdit) {
            setEditData();
        }
    }

    private void callWebServiceForGetDoors() {
        WebService.getInstance().getServiceKeyGuestEntry(new RestCallBack<List<GuestEntryDoor>>() {
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

    private ArrayList<Vendors.Technicians> technicianList;
    ActivityResultLauncher<Intent> activityForResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            _txtCompanyName.setText(data.getExtras().getString("NAME"));
                            _txtCompanyName.setTag(data.getExtras().getString("ID"));
                            vendorMappingId = Long.parseLong(data.getExtras().getString("ID"));
                            technicianList = new ArrayList<>();
                            technicianList = (ArrayList<Vendors.Technicians>) data.getExtras()
                                    .getSerializable("technicians");
                            _txtTechnician.setText("");
                            _txtTechnician.setTag("");
                        }
                    });

    ActivityResultLauncher<Intent> activityForResultLauncherTechnician =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            _txtTechnician.setText(data.getExtras().getString("NAME"));
                            _txtTechnician.setTag(data.getExtras().getString("ID"));
                        }
                    });


    private void setEditData() {
        _buttonSend.setText("Update");
        _txtCompanyName.setText(editServiceKey.getRecipient());
        _txtCompanyName.setTag(String.valueOf(editServiceKey.vendorMappingId));
        _txtTechnician.setText(editServiceKey.getTechnicianName());
        _notesEditText.setText(editServiceKey.getNotes());

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        _toDateEditText.setText(formatter.format(editServiceKey.getToUtc()));
        _fromDateEditText.setText(formatter.format(editServiceKey.getFromUtc()));
        isEndDateActive = !editServiceKey.isNoEndDate();
        switchEndDateVisibility(!isEndDateActive);

        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm a");
        _toTimeEditText.setText(simpleDate.format(editServiceKey.getToUtc()));
        _fromTimeEditText.setText(simpleDate.format(editServiceKey.getFromUtc()));

        isTimeSelectionActive = editServiceKey.isFullDay();
        switchTimeVisibility(isTimeSelectionActive);

        if (editServiceKey.getMapUrls() != null) {
            if (editServiceKey.getMapUrls().length > 0) {
                mrsItemsArrayList.clear();
                MRSItems mrsItems = new MRSItems();
                mrsItems.bitMap = null;
                mrsItems.imageUrl = editServiceKey.getMapUrls()[0];
                mrsItemsArrayList.add(mrsItems);
                mrsImagesVideosAdapter.refresh(mrsItemsArrayList);

                if (mrsItemsArrayList.size() >= 1) {
                    rlPriorUpload.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setSpinnerRepeat() {
        ArrayList<String> catString = new ArrayList<>();
        catString.add("Daily");
        catString.add("Weekly");
        catString.add("Monthly");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, catString);
        _spnRepeat.setAdapter(adapter);
        if (isEdit) {
            _spnRepeat.setSelection(Integer.parseInt(editServiceKey.getRepeatType()) - 1);
        } else {
            _spnRepeat.setSelection(0);
        }

        _spnRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    _spnOptions.setVisibility(View.VISIBLE);
                    _txtRepeatOptions.setVisibility(View.VISIBLE);
                    viewSpnOptions.setVisibility(View.VISIBLE);
                    setSpinnerRepeatOption(position);
                } else {
                    _spnOptions.setVisibility(View.GONE);
                    _txtRepeatOptions.setVisibility(View.GONE);
                    viewSpnOptions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _imgInfoEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(1);
            }
        });

        _imgInfoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(2);
            }
        });

        _imgInfoRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(3);
            }
        });

        rlPriorUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {
                    uploadDocument();
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(getApplicationContext(),
                            "Please accept all permissions from settings.",
                            Toast.LENGTH_LONG).show();
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

    public void startActivityResult(Intent pictureIntent, int requestCode) {
        startActivityForResult(pictureIntent, requestCode);
    }

    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }

    private void uploadDocument() {
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_product_upload);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.color.transparent));
        ((TextView) dialog.findViewById(R.id.uploadDocumentUsingFilePicker))
                .setText("Photo/Video Upload");
        dialog.show();
        dialog.findViewById(R.id.uploadDocumentUsingCamera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        askPhotoVideo();
                    }
                });

        dialog.findViewById(R.id.uploadDocumentUsingFilePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickFileFromDevice.showFileChooserWithVideo(PickFileFromDevice.PICK_IMAGE);
            }
        });

        dialog.findViewById(R.id.dialogDocUploadCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void askPhotoVideo() {
        String[] choice = {"Photo", "Video"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Choose to capture");
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Photo".equals(choice[which])) {
                    pickFileFromDevice.showCameraIntent(PickFileFromDevice.CAPTURE_IMAGE);
                } else if ("Video".equals(choice[which])) {
                    pickFileFromDevice.showVideoIntent(PickFileFromDevice.CAPTURE_IMAGE);
                }
            }
        });
        builder.show();

    }

    //compress video
    private void videoCompressorGallery(String videoPath, @NotNull File desFile, Uri selectedMediaUri) {
        List<Uri> uriList = new ArrayList<>();
        uriList.add(selectedMediaUri);

        SharedStorageConfiguration sharedStorageConfiguration = new SharedStorageConfiguration(videoPath, SaveLocation.movies);

        AppSpecificStorageConfiguration appSpecificStorageConfiguration = new AppSpecificStorageConfiguration(videoPath, desFile.getName());

        Configuration configuration = new Configuration(VideoQuality.MEDIUM, true, 5, false, false, 360.0, 480.0);

        VideoCompressor.start(this, uriList, false, sharedStorageConfiguration, appSpecificStorageConfiguration, configuration, new CompressionListener() {
            @Override
            public void onSuccess(int i, long l, @Nullable String s) {
                // On Compression success
                ProgressDialog.dismissProgress();
                Uri videoLocalUri = Uri.parse(new File(desFile.getPath()).toString());
                File file = desFile;
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = file;
                mrsItems.bitMap = getThumbnailFromVideo(NewServiceKeyActivity.this, selectedMediaUri);
                mrsItems.isImage = false;
                mrsItems.videoUri = videoLocalUri;

                videoUri.add(videoLocalUri);

                if (file != null) {
                    if (((file.length() / 1024) / 1024) < 49) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(NewServiceKeyActivity.this,
                                "File must be less then or equal to 50 MB",
                                Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 1) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onStart(int i) {
                // Compression start
                ProgressDialog.showProgress(NewServiceKeyActivity.this);
            }

            @Override
            public void onProgress(int i, float v) {
                // Update UI with progress value
                /*runOnUiThread(new Runnable() {
                    public void run() {
                        progress.setText(progressPercent + "%");
                        progressBar.setProgress((int) progressPercent);
                    }
                });*/
            }

            @Override
            public void onFailure(int i, @NonNull String s) {

            }

            @Override
            public void onCancelled(int i) {

            }


        });
    }

    private void videoCompressorCamera(String videoPath, File desFile) {
        List<Uri> uriList = new ArrayList<>();
        Uri videoLocalUri = Uri.parse(new File(desFile.getPath()).toString());
        uriList.add(videoLocalUri);

        SharedStorageConfiguration sharedStorageConfiguration = new SharedStorageConfiguration(videoPath, SaveLocation.movies);

        AppSpecificStorageConfiguration appSpecificStorageConfiguration = new AppSpecificStorageConfiguration(videoPath, desFile.getName());

        Configuration configuration = new Configuration(VideoQuality.MEDIUM, true, 5, false, false, 360.0, 480.0);


        VideoCompressor.start(this, uriList, false, sharedStorageConfiguration, appSpecificStorageConfiguration, configuration, new CompressionListener() {
            @Override
            public void onSuccess(int i, long l, @Nullable String s) {
                // On Compression success
                ProgressDialog.dismissProgress();
                Uri videoLocalUri = Uri.parse(new File(desFile.getPath()).toString());
                File file = new File(desFile.getPath());
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = file;
                mrsItems.bitMap = getThumbnailFromVideo(NewServiceKeyActivity.this, videoLocalUri);
                mrsItems.isImage = true;
                mrsItems.videoUri = videoLocalUri;

                videoUri.add(videoLocalUri);

                if (file != null) {
                    if (((file.length() / 1024) / 1024) < 49) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(NewServiceKeyActivity.this,
                                "File must be less then or equal to 50 MB", Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 1) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onStart(int i) {
                // Compression start
                ProgressDialog.showProgress(NewServiceKeyActivity.this);
            }

            @Override
            public void onProgress(int i, float v) {
                // Update UI with progress value
                /*runOnUiThread(new Runnable() {
                    public void run() {
                        progress.setText(progressPercent + "%");
                        progressBar.setProgress((int) progressPercent);
                    }
                });*/
            }

            @Override
            public void onFailure(int i, @NonNull String s) {

            }

            @Override
            public void onCancelled(int i) {

            }


        });
    }

    //compressedVideoFileCreate
    private File createVideoFile() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "VID_" + timeStamp + "_";

        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Invictus";
        File NewStorageDir = new File(storagePath);
        if (!NewStorageDir.exists()) {
            File wallpaperDirectory = new File(storagePath);
            wallpaperDirectory.mkdirs();
        }

        File video = null;
        try {
            video = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".mp4",         /* suffix */
                    NewStorageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return video;
    }

    //retriveThumbnail
    public Bitmap getThumbnailFromVideo(Context context, Uri uri) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(context, uri);
        return mMMR.getFrameAtTime();
    }

    private void setSpinnerRepeatOption(int position) {
        ArrayList<String> catString = new ArrayList<>();
        String[] weeks = {"Sunday", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday"};
        _spnOptions.setText("");
        if (position == 1) {
            _txtRepeatOptions.setText("Select Week Days");
            catString = new ArrayList<>();
            Collections.addAll(catString, weeks);
        } else if (position == 2) {
            _txtRepeatOptions.setText("Select Month Days");
            catString = new ArrayList<>();
            for (int i = 1; i < 32; i++) {
                ServiceKeyRepeatOptions options = new ServiceKeyRepeatOptions();
                catString.add(i + "");
            }
        }

        if (isEdit && editServiceKey.getRepeatValueList() != null) {
            _spnOptions.setTag(editServiceKey.getRepeatValueList());
            String[] selected = editServiceKey.getRepeatValueList().split(",");
            if (selected.length > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < selected.length; i++) {
                    stringBuilder.append(catString.get(Integer.parseInt(selected[i]) - 1));
                    if (i != selected.length - 1) {
                        stringBuilder.append(", ");
                    }
                }
                _spnOptions.setText(stringBuilder.toString());
            }
        }

        boolean[] selectedLanguage = new boolean[catString.size()];
        String[] choices = new String[catString.size()];

        for (int i = 0; i < choices.length; i++) {
            choices[i] = catString.get(i);
        }
        ArrayList<Integer> langList = new ArrayList<>();
        _spnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(NewServiceKeyActivity.this);

                // set title
                builder.setTitle("Select Days");

                // set dialog non cancelable
                builder.setCancelable(true);

                builder.setMultiChoiceItems(choices, selectedLanguage,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                // check condition

                                if (b) {
                                    // when checkbox selected
                                    // Add position  in lang list
                                    langList.add(i);
                                    // Sort array list
                                    Collections.sort(langList);
                                } else {
                                    // when checkbox unselected
                                    // Remove position from langList
                                    langList.remove(Integer.valueOf(i));
                                }
                            }
                        });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder stringBuilderID = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < langList.size(); j++) {
                            // concat array value
                            stringBuilder.append(choices[langList.get(j)]);
                            stringBuilderID.append((langList.get(j) + 1) + "");
                            // check condition
                            if (j != langList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                                stringBuilderID.append(",");
                            }
                        }
                        // set text on textView
                        _spnOptions.setText(stringBuilder.toString());
                        _spnOptions.setTag(stringBuilderID.toString());
                    }
                });

                builder.setNegativeButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Clear All
                        for (int j = 0; j < selectedLanguage.length; j++) {
                            // remove all selection
                            selectedLanguage[j] = false;
                            // clear language list
                            langList.clear();
                            // clear text view value
                            _spnOptions.setText("");
                        }
                    }
                });

                builder.setNeutralButton("Select All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //Select All
                        for (int i = 0; i < choices.length; i++) {
                            langList.add(i);
                            selectedLanguage[i] = true;
                        }
                        _spnOptions.setText("Selected All");
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }

    //Info: 1 - EndDate, 2- FullDay, 3- Repeat
    private void showInfoDialog(int info) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        if (info == 1) {
            alertDialog.setMessage("If you want to allow always then enable it.");
        } else if (info == 2) {
            alertDialog.setMessage("If you want to allow for specific time of the" +
                    " day then enable custom else enable full day");
        } else {
            alertDialog.setMessage("Select repeat type based on daily, weekly and monthly occurrence.");
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void switchEndDateVisibility(Boolean isUnActive) {
        if (isUnActive) {
            _switchEndDate.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_deactive_switch));

            _txtSwitchEndDate.setText("Allow Specific Days");
            _tilEndDate.setEnabled(true);
            isEndDateActive = true;
        } else {
            _switchEndDate.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_active_switch));

            _txtSwitchEndDate.setText("Allow Always");
            _tilEndDate.setEnabled(false);
            isEndDateActive = false;
        }
    }

    private void switchTimeVisibility(Boolean isUnActive) {
        if (isUnActive) {
            _switchTime.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_deactive_switch));

            _txtSwitchTime.setText("Full Day");
            _llTimeSection.setVisibility(View.GONE);

            isTimeSelectionActive = true;
        } else {
            _switchTime.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_active_switch));

            _txtSwitchTime.setText("Custom");
            _llTimeSection.setVisibility(View.VISIBLE);
            isTimeSelectionActive = false;
        }
    }

    private void onClickListner() {
        _buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewServiceKey();
            }
        });

        tvAddVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vendorDialog != null) {
                    if (!vendorDialog.isHidden()) {
                        vendorDialog.show(getSupportFragmentManager(), "addVendorDialog");
                        vendorDialog.setCancelable(false);
                    } else {
                        vendorDialog.dismiss();
                    }

                }
            }
        });

        tvAddTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (technicianDialog != null) {
                    if (!technicianDialog.isHidden()) {
                        technicianDialog.setVendorMappingId(vendorMappingId);
                        technicianDialog.show(getSupportFragmentManager(), "addTechnicianDialog");
                        technicianDialog.setCancelable(false);
                    } else {
                        technicianDialog.dismiss();
                    }

                }
            }
        });

    }

    private void createNewServiceKey() {
        boolean cancel = false;
        View focusView = null;
        View focusView2 = null;

        _txtCompanyName.setError(null);
        _txtTechnician.setError(null);
        _fromDateEditText.setError(null);
        _fromTimeEditText.setError(null);
        _toDateEditText.setError(null);
        _toTimeEditText.setError(null);


        if (TextUtils.isEmpty(_txtCompanyName.getText().toString())) {
            _txtCompanyName.setError(getString(R.string.error_field_required));
            focusView = _txtCompanyName;
            cancel = true;
        } else if (TextUtils.isEmpty(_txtTechnician.getText().toString())) {
            _txtTechnician.setError(getString(R.string.error_field_required));
            focusView = _txtTechnician;
            cancel = true;
        } else if (TextUtils.isEmpty(_fromDateEditText.getText().toString())) {
            _fromDateEditText.setError(getString(R.string.error_field_required));
            focusView = _fromDateEditText;
            cancel = true;
        } else if (isEndDateActive && TextUtils.isEmpty(_toDateEditText.getText().toString())) {
            _toDateEditText.setError(getString(R.string.error_field_required));
            focusView = _toDateEditText;
            cancel = true;
        } else if (isEndDateActive
                && convertToDateTime(_fromDateEditText.getText() + " "
                + _fromTimeEditText.getText())
                .after(convertToDateTime(_toDateEditText.getText() + " "
                        + _toTimeEditText.getText()))) {
            _toDateEditText.setError(getString(R.string.error_from_date_time_after_to_date_time));
            _toTimeEditText.setError(getString(R.string.error_from_date_time_after_to_date_time));
            focusView = _toDateEditText;
            cancel = true;
        } else if (!isTimeSelectionActive && TextUtils.isEmpty(_fromTimeEditText.getText().toString())) {
            _fromTimeEditText.setError(getString(R.string.error_field_required));
            focusView = _fromTimeEditText;
            cancel = true;
        } else if (!isTimeSelectionActive && TextUtils.isEmpty(_toTimeEditText.getText().toString())) {
            _toTimeEditText.setError(getString(R.string.error_field_required));
            focusView = _toTimeEditText;
            cancel = true;
        }

        if (!cancel) {
            ServiceKey key = new ServiceKey();
            key.setCompanyId(String.valueOf(vendorMappingId));
            key.setRecipient(_txtCompanyName.getText().toString());
            key.setTechnicianName(_txtTechnician.getText().toString());
            key.setFromUtc(convertToDateTime(_fromDateEditText.getText() + " " + _fromTimeEditText.getText()));
            key.setNoEndDate(isEndDateActive);
            if (isEndDateActive) {
                key.setToUtc(convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText()));
            } else {
                key.setToUtc(new Date());
            }

            key.setFullDay(isTimeSelectionActive);
            key.setStart(convertToDateTime(_fromDateEditText.getText() + " " + _fromTimeEditText.getText()));
            key.setEnd(convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText()));
            key.setRepeatType(_spnRepeat.getSelectedItem().toString());
            if (_spnRepeat.getSelectedItemPosition() == 0) {
                key.setRepeatValueList("");
            } else {
                key.setRepeatValueList(_spnOptions.getTag().toString());
            }
            key.setNotes(_notesEditText.getText().toString());
            if (mrsItemsArrayList.size() > 0) {
                if (mrsItemsArrayList.get(0).file != null) {
                    key.setMapFile(mrsItemsArrayList.get(0).file);
                    key.setFileName(mrsItemsArrayList.get(0).file.getName());
                }
            } else {
                key.setMapImage(null);
            }
            key.setMapUrl("");

            if (adapter == null) {
                key.setSelectedEntryJSON("[]");
            } else if (adapter.getSelectedEntry() == null) {
                key.setSelectedEntryJSON("[]");
            } else if (adapter.getSelectedEntry().size() > 0) {
                String json = new Gson().toJson(adapter.getSelectedEntry());
                key.setSelectedEntryJSON(json);
            } else {
                key.setSelectedEntryJSON("[]");
            }

            if (isEdit) {
                key.setId(editServiceKey.getId());
            }

            ProgressDialog.showProgress(this);
            WebService.getInstance().createServiceKey(key, new RestEmptyCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(NewServiceKeyActivity.this, "Service key created successfully", Toast.LENGTH_LONG).show();
                    setResult(1);
                    finish();
                }

                @Override
                public void onFailure(WSException wse) {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(NewServiceKeyActivity.this, "Error while creating Service Key", Toast.LENGTH_LONG).show();
//                    setResult(1);
//                    finish();
                }
            });

        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (_changesMade)
                Utilities.showDiscardChangesDialog(this);
            else
                NavUtils.navigateUpFromSameTask(NewServiceKeyActivity.this);
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

    private Date convertToDateTime(String input) {

        Date result = null;
        try {
            result = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(input);
        } catch (ParseException ex) {
            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
        }
        return result;
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

    @Override
    public void deleteImage(int position, MRSItems item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (videoUri.size() > 0) {
                            for (int i = 0; i < videoUri.size(); i++) {
                                if (item.videoUri.equals(videoUri.get(i))) {
                                    videoUri.remove(i);
                                    break;
                                }
                            }
                        }

                        mrsItemsArrayList.remove(position);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                        mrsImagesVideosAdapter.notifyDataSetChanged();

                        if (mrsItemsArrayList.size() < 1) {
                            rlPriorUpload.setVisibility(View.VISIBLE);
                        }
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
            }
        }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path = pickFileFromDevice.getImagePath();
        String videoPath = pickFileFromDevice.getVideoPath();

        if (requestCode == PickFileFromDevice.CAPTURE_IMAGE && resultCode != 0) {
            if (!path.equals("")) {

                File file = Utilities.compressedFile(new File(path));
                /*File file = new File(Utilities.compressedFile(new File(path)));*/
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = file;
                mrsItems.bitMap = BitmapFactory.decodeFile(Utilities.compressedFile(new File(path)).getAbsolutePath());
                /*mrsItems.bitMap = BitmapFactory.decodeFile(new File(Utilities.compressedFile(new File(path))).getAbsolutePath());*/
                mrsItems.isImage = true;
                mrsItems.videoUri = Uri.parse("");

                if (((file.length() / 1024) / 1024) < 9) {
                    mrsItemsArrayList.add(mrsItems);
                    mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                }
                if (mrsItemsArrayList.size() >= 1) {
                    rlPriorUpload.setVisibility(View.GONE);
                }

            } else if (!videoPath.equals("")) {
                File compressedFile = createVideoFile();
                videoCompressorCamera(videoPath, compressedFile);
            }

        } else if (requestCode == PickFileFromDevice.PICK_IMAGE && data != null) {
            Uri selectedMediaUri = data.getData();
            if (selectedMediaUri.toString().contains("image")) {

                //handle image
                File file = FileUtils.getFile(this, data.getData());
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = Utilities.compressedFile(file);
                /*mrsItems.file = new File(Utilities.compressedFile(file));*/
                mrsItems.bitMap = BitmapFactory.decodeFile(Utilities.compressedFile(file).getAbsolutePath());
                mrsItems.isImage = true;
                mrsItems.videoUri = Uri.parse("");


                if (((file.length() / 1024) / 1024) < 9) {
                    mrsItemsArrayList.add(mrsItems);
                    mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB",
                            Toast.LENGTH_LONG).show();
                }
                if (mrsItemsArrayList.size() >= 1) {
                    rlPriorUpload.setVisibility(View.GONE);
                }

            } else if (selectedMediaUri.toString().contains("video")) {
                //handle video
                File compressedFile = createVideoFile();
                videoCompressorGallery(FilePath.getPath(NewServiceKeyActivity.this,
                        selectedMediaUri), compressedFile, selectedMediaUri);
            }
        }
    }

    @Override
    public void onAddVendorClicked() {
        WebService.getInstance().getVendors(
                new RestCallBack<List<Vendors>>() {
                    @Override
                    public void onResponse(List<Vendors> response) {
                        if (response != null) {
                            if (response.size() > 0) {
                                Vendors vendor = response.get(response.size() - 1);
                                _txtCompanyName.setText(vendor.name);
                                _txtCompanyName.setTag(String.valueOf(vendor.id));
                                vendorMappingId = vendor.id;
                                technicianList = vendor.technicians;
                                _txtTechnician.setText("");
                                _txtTechnician.setTag("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        Log.e("Error >> ", wse.getServerMessage());
                    }
                });
    }

    @Override
    public void onAddTechnicianClicked(long vendorMappingId) {
        WebService.getInstance().getTechnicians(vendorMappingId,
                new RestCallBack<List<Vendors.Technicians>>() {
                    @Override
                    public void onResponse(List<Vendors.Technicians> response) {
                        if (response != null) {
                            if (response.size() > 0) {
                                Vendors.Technicians technicians = response.get(response.size() - 1);
                                _txtTechnician.setText(technicians.technicianName);
                                _txtTechnician.setTag(String.valueOf(technicians.id));
                            }
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        Log.e("Error >> ", wse.getServerMessage());
                    }
                });
    }
}
