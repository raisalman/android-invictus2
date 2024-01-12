package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration;
import com.abedelazizshe.lightcompressorlibrary.config.Configuration;
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation;
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.R;
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
import net.invictusmanagement.invictuslifestyle.models.MRSItems;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequest;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.models.Vendors;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.RestEmptyCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class NewMaintenanceRequestActivity extends BaseActivity implements DeleteSingleMRSItem,
        SetOnAddVendorDialogClick, SetOnAddTechnicianDialogClick {

    private MenuItem menuItem;
    private TextView tvPermissionTtl;
    private RadioGroup rgContactDetails;
    private LinearLayout rlPriorUpload;
    private LinearLayout rlAfterUpload;
    private LinearLayout llMainAfterUpload;
    private LinearLayout llEditResidentInfo;
    private EditText _residentEditText;
    private EditText _unitEditText;
    private LinearLayout llStatus;
    private Spinner spnStatus;
    private RecyclerView rvPriorImages;
    private RecyclerView rvAfterImages;
    private final ArrayList<MRSItems> mrsItemsArrayList = new ArrayList();
    private final ArrayList<MRSItems> mrsAfterItemsArrayList = new ArrayList();
    private final ArrayList<Uri> videoUri = new ArrayList();
    private MRSImagesVideosAdapter mrsImagesVideosAdapter;
    private MRSImagesVideosAdapter mrsAfterImagesVideosAdapter;
    private PickFileFromDevice pickFileFromDevice;
    private EditText _titleEditText;
    private EditText _descriptionEditText;
    private EditText _notesEditText;
    private ProgressBar _progressBar;
    private Boolean _changesMade = false;
    private boolean isEdit = false;
    private MaintenanceRequestResponse editMaintenanceRequest;

    private LinearLayout llCompanyDetails;
    private TextView _txtCompanyName;
    private EditText _txtTechnician;
    private long vendorMappingId;

    private boolean isBeforeUpload = true;

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
    private Toolbar toolbar;
    private String deletedIds = "";
    private SharedPreferences sharedPreferences;
    private String role;


    private TextView tvAddVendor;
    private TextView tvAddTechnician;

    private AddVendorDialog vendorDialog = null;
    private AddTechnicianDialog technicianDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_maintenance_request);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        role = sharedPreferences.getString("userRole", "");
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("MAINTENANCE_REQUEST") != null) {
                isEdit = true;
                editMaintenanceRequest = (MaintenanceRequestResponse) getIntent()
                        .getSerializableExtra("MAINTENANCE_REQUEST");
            }
        }
        toolBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        onClickListeners();
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

    private void onClickListeners() {
        _txtCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewMaintenanceRequestActivity.this,
                        VendorSelectionActivity.class);
                activityForResultLauncher.launch(intent);
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
                        technicianDialog.show(getSupportFragmentManager(), "addTechnicianDialog");
                        technicianDialog.setCancelable(false);
                    } else {
                        technicianDialog.dismiss();
                    }

                }
            }
        });

        rlPriorUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equals(getString(R.string.role_facility))
                        || role.equals(getString(R.string.role_leasing_officer))
                        || role.equals(getString(R.string.role_property_manager))
                        || role.equals(getString(R.string.role_vendor))) {

                    if (spnStatus.getSelectedItemPosition() != 2) {
                        isBeforeUpload = true;
                        checkPermissions();
                    } else {
                        Toast.makeText(NewMaintenanceRequestActivity.this,
                                "You can upload images only when status is Requested or Active",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    isBeforeUpload = true;
                    checkPermissions();
                }
            }
        });

        rlAfterUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equals(getString(R.string.role_vendor))) {
                    isBeforeUpload = false;
                    checkPermissions();
                } else {
                    if (spnStatus.getSelectedItemPosition() == 2) {
                        isBeforeUpload = false;
                        checkPermissions();
                    } else {
                        Toast.makeText(NewMaintenanceRequestActivity.this,
                                "You can upload images only when status is Closed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void initViews() {
        pickFileFromDevice = new PickFileFromDevice(NewMaintenanceRequestActivity.this, NewMaintenanceRequestActivity.this);
        llEditResidentInfo = findViewById(R.id.llEditResidentInfo);
        _residentEditText = findViewById(R.id.residentName);
        _unitEditText = findViewById(R.id.unitNbr);
        llStatus = findViewById(R.id.llStatus);
        spnStatus = findViewById(R.id.spnStatus);

        _titleEditText = findViewById(R.id.title);
        _titleEditText.addTextChangedListener(_watcher);

        _descriptionEditText = findViewById(R.id.description);
        _descriptionEditText.addTextChangedListener(_watcher);

        _notesEditText = findViewById(R.id.notes);
        _notesEditText.addTextChangedListener(_watcher);

        _progressBar = findViewById(R.id.progress);
        rlPriorUpload = findViewById(R.id.rlPriorUpload);
        rvPriorImages = findViewById(R.id.rvPriorImages);
        rlAfterUpload = findViewById(R.id.rlAfterUpload);
        rvAfterImages = findViewById(R.id.rvAfterImages);
        llMainAfterUpload = findViewById(R.id.llAfterImages);
        tvPermissionTtl = findViewById(R.id.tvPermissionTtl);

        llCompanyDetails = findViewById(R.id.llCompanyDetails);
        _txtCompanyName = findViewById(R.id.txtCompanyName);
        _txtTechnician = findViewById(R.id.txtTechnician);
        _txtTechnician.setInputType(InputType.TYPE_NULL);
        _txtTechnician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewMaintenanceRequestActivity.this,
                        TechnicianSelectionActivity.class);
                intent.putExtra("vendorMappingId", vendorMappingId);
                intent.putExtra("isVendor", false);
                activityForResultLauncherTechnician.launch(intent);
            }
        });
        tvAddTechnician = findViewById(R.id.tvAddTechnician);
        tvAddVendor = findViewById(R.id.tvAddVendor);

        SpannableString content = new SpannableString(tvAddVendor.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvAddVendor.setText(content);

        content = new SpannableString(tvAddTechnician.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvAddTechnician.setText(content);

        vendorDialog = new AddVendorDialog(this);
        technicianDialog = new AddTechnicianDialog(this);

        mrsImagesVideosAdapter = new MRSImagesVideosAdapter(NewMaintenanceRequestActivity.this,
                mrsItemsArrayList, this);
        rvPriorImages.setAdapter(mrsImagesVideosAdapter);
        mrsImagesVideosAdapter.setDelete(false);
        mrsImagesVideosAdapter.notifyDataSetChanged();

        mrsAfterImagesVideosAdapter = new MRSImagesVideosAdapter(NewMaintenanceRequestActivity.this,
                mrsAfterItemsArrayList, this);
        rvAfterImages.setAdapter(mrsAfterImagesVideosAdapter);

        rgContactDetails = findViewById(R.id.rgContactDetails);

        if (isEdit) {
            setEditableViews();
        } else {
            llEditResidentInfo.setVisibility(View.GONE);
            llStatus.setVisibility(View.GONE);
            llMainAfterUpload.setVisibility(View.GONE);
            llCompanyDetails.setVisibility(View.GONE);
        }
    }

    private void setEditableViews() {
        llCompanyDetails.setVisibility(View.VISIBLE);
        llEditResidentInfo.setVisibility(View.VISIBLE);
        llStatus.setVisibility(View.VISIBLE);
        _residentEditText.setText(editMaintenanceRequest.getResidentName());
        _unitEditText.setText(editMaintenanceRequest.getUnitNbr()
                == null ? "Not Assigned" : editMaintenanceRequest.getUnitNbr());
        _residentEditText.setEnabled(false);
        _unitEditText.setEnabled(false);
        _titleEditText.setText(editMaintenanceRequest.getTitle());
        _descriptionEditText.setText(editMaintenanceRequest.getDescription());

        if (!editMaintenanceRequest.getNeedPermission()) {
            rgContactDetails.check(R.id.rbFollowUp);
        } else {
            rgContactDetails.check(R.id.rbEnter);
        }

        _txtCompanyName.setText(editMaintenanceRequest.recipient);
        _txtTechnician.setText(editMaintenanceRequest.technicianName);
        _txtCompanyName.setTag(String.valueOf(editMaintenanceRequest.companyId));
        vendorMappingId = editMaintenanceRequest.companyId;

        if (role.equals(getString(R.string.role_vendor))) {
            _titleEditText.setEnabled(false);
            _descriptionEditText.setEnabled(false);
            spnStatus.setEnabled(false);
            tvPermissionTtl.setVisibility(View.GONE);
            rgContactDetails.setVisibility(View.GONE);
            rlPriorUpload.setVisibility(View.GONE);
            _notesEditText.setEnabled(true);
            llCompanyDetails.setEnabled(false);
        } else {
            _notesEditText.setEnabled(false);
            llCompanyDetails.setEnabled(true);
        }

        ArrayList<String> catString = new ArrayList<>();
        catString.add("Requested");
        catString.add("Active");
        catString.add("Request To Close");
        catString.add("Closed");
        catString.add("Not Solved");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, catString);
        spnStatus.setAdapter(adapter);
        switch (editMaintenanceRequest.getStatus()) {
            case Requested:
                spnStatus.setSelection(0);
                break;
            case Active:
                spnStatus.setSelection(1);
                break;
            case RequestedToClose:
                spnStatus.setSelection(2);
                break;
            case Closed:
                spnStatus.setSelection(3);
                break;
            case NotSolve:
                spnStatus.setSelection(4);
                break;
        }

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean found;
                switch (position) {
                    case 0:
                    case 1:
                        found = false;
                        for (int i = mrsAfterItemsArrayList.size() - 1; i >= 0; i--) {
                            if (mrsAfterItemsArrayList.get(i).bitMap != null) {
                                found = true;
                                mrsAfterItemsArrayList.remove(i);
                            }
                        }
                        mrsAfterImagesVideosAdapter.refresh(mrsAfterItemsArrayList);
                        mrsAfterImagesVideosAdapter.notifyDataSetChanged();

                        if (found) {
                            Toast.makeText(NewMaintenanceRequestActivity.this,
                                    "As you have changed status, newly uploaded " +
                                            "images for Closed Status are removed",
                                    Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case 2:
                        found = false;
                        for (int i = mrsItemsArrayList.size() - 1; i >= 0; i--) {
                            if (mrsItemsArrayList.get(i).bitMap != null) {
                                found = true;
                                mrsItemsArrayList.remove(i);
                            }
                        }
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                        mrsImagesVideosAdapter.notifyDataSetChanged();

                        if (found) {
                            Toast.makeText(NewMaintenanceRequestActivity.this,
                                    "As you have changed status, newly uploaded " +
                                            "images for Requested/Active Status are removed",
                                    Toast.LENGTH_SHORT).show();

                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //recyclerview set
        if (editMaintenanceRequest.getMaintenanceRequestFiles().size() > 0) {
            for (int i = 0; i < editMaintenanceRequest.getMaintenanceRequestFiles().size(); i++) {
                MaintenanceRequesFiles files = editMaintenanceRequest.getMaintenanceRequestFiles().get(i);
                MRSItems items = new MRSItems();
                items.maintenanceRequesFiles = files;
                items.isBeforeSolve = files.isBeforeSolve;
                if (files.isBeforeSolve) {
                    if (mrsItemsArrayList.size() < 3) {
                        items.imageUrl = files.maintenanceRequestImageSrc;
                        mrsItemsArrayList.add(items);
                    }
                } else {
                    if (mrsAfterItemsArrayList.size() < 3) {
                        items.imageUrl = files.maintenanceRequestImageSrc;
                        mrsAfterItemsArrayList.add(items);
                    }
                }
            }

            mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
            mrsImagesVideosAdapter.notifyDataSetChanged();

            mrsAfterImagesVideosAdapter.refresh(mrsAfterItemsArrayList);
            mrsAfterImagesVideosAdapter.notifyDataSetChanged();

            setUploadVisibility();
        }

    }

    private void setUploadVisibility() {
        if (mrsItemsArrayList.size() >= 3) {
            rlPriorUpload.setVisibility(View.GONE);
        }

        if (mrsAfterItemsArrayList.size() >= 3) {
            rlAfterUpload.setVisibility(View.GONE);
        }
    }

    public static void showDiscardChangesDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Discard changes?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.finish();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (role.equals(getString(R.string.role_vendor)))
            menu.findItem(R.id.action_send).setTitle(getString(R.string.action_request_to_close));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (_changesMade)
                    Utilities.showDiscardChangesDialog(this);
                else
                    NavUtils.navigateUpFromSameTask(NewMaintenanceRequestActivity.this);
                return true;

            case R.id.action_send:
                menuItem = item;
                item.setEnabled(false);
                boolean cancel = false;
                View focusView = null;

                Utilities.addHaptic(_titleEditText);
                _titleEditText.setError(null);
                _descriptionEditText.setError(null);

                if (TextUtils.isEmpty(_titleEditText.getText().toString())) {
                    _titleEditText.setError(getString(R.string.error_field_required));
                    focusView = _titleEditText;
                    cancel = true;
                } else if (TextUtils.isEmpty(_descriptionEditText.getText().toString())) {
                    _descriptionEditText.setError(getString(R.string.error_field_required));
                    focusView = _descriptionEditText;
                    cancel = true;
                }

                if (!cancel) {
                    MaintenanceRequest request = new MaintenanceRequest();
                    request.setTitle(_titleEditText.getText().toString());
                    request.setDescription(_descriptionEditText.getText().toString());
                    int selectedId = rgContactDetails.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    boolean needPermission = false;
                    if (radioButton.getText().equals("Ok to enter")) {
                        needPermission = true;
                    } else if (radioButton.getText().equals("No, follow up")) {
                        needPermission = false;
                    }

                    request.setNeedPermission(needPermission);

                    List<File> allFileList = new ArrayList<>();
                    for (int i = 0; i < mrsItemsArrayList.size(); i++) {
                        allFileList.add(mrsItemsArrayList.get(i).file);
                    }

                    request.setUploadMaintenanceRequestFiles(allFileList);

                    Utilities.hideKeyboard(NewMaintenanceRequestActivity.this);
                    ProgressDialog.showProgress(NewMaintenanceRequestActivity.this);
                    if (isEdit) {
                        request.setNotes(_notesEditText.getText().toString());
                        if (role.equals(getString(R.string.role_vendor))) {
                            request.setStatus(MaintenanceRequest.Status.RequestedToClose);
                        } else {
                            switch (spnStatus.getSelectedItemPosition()) {
                                case 0:
                                    request.setStatus(MaintenanceRequest.Status.Requested);
                                    break;
                                case 1:
                                    request.setStatus(MaintenanceRequest.Status.Active);
                                    break;
                                case 2:
                                    request.setStatus(MaintenanceRequest.Status.RequestedToClose);
                                    break;
                                case 3:
                                    request.setStatus(MaintenanceRequest.Status.Closed);
                                    break;
                                case 4:
                                    request.setStatus(MaintenanceRequest.Status.NotSolve);
                                    break;
                            }
                        }
                        request.setId(editMaintenanceRequest.getId());

                        ArrayList<MaintenanceRequesFiles> oldImages = new ArrayList<>();
                        allFileList = new ArrayList<>();

                        for (int j = 0; j < mrsItemsArrayList.size(); j++) {
                            if (mrsItemsArrayList.get(j).imageUrl != null) {
                                oldImages.add(mrsItemsArrayList.get(j).maintenanceRequesFiles);
                            } else {
                                allFileList.add(mrsItemsArrayList.get(j).file);
                            }
                        }

                        for (int j = 0; j < mrsAfterItemsArrayList.size(); j++) {
                            if (mrsAfterItemsArrayList.get(j).imageUrl != null) {
                                oldImages.add(mrsAfterItemsArrayList.get(j).maintenanceRequesFiles);
                            } else {
                                allFileList.add(mrsAfterItemsArrayList.get(j).file);
                            }
                        }
                        request.setUploadMaintenanceRequestFiles(allFileList);
                        request.setMaintenanceRequestFiles(oldImages);
                        if (deletedIds.length() > 0) {
                            deletedIds = deletedIds.substring(0, deletedIds.length() - 1);
                        }
                        request.setDeleteMaintenanceRequestFiles(deletedIds);
                        request.setCompanyId(_txtCompanyName.getTag().toString());
                        request.setTechnicianName(_txtTechnician.getText().toString());

                        _changesMade = false;
                        callAPIForEdit(request);
                    } else {
                        callAPIForCreate(request);
                    }

                } else {
                    item.setEnabled(true);
                    focusView.requestFocus();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callAPIForEdit(MaintenanceRequest request) {
        WebService.getInstance().editMaintenanceRequest(request, new RestEmptyCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    Toast.makeText(NewMaintenanceRequestActivity.this, "Request updated successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(NewMaintenanceRequestActivity.this, "Failed to update request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(NewMaintenanceRequestActivity.this, "Failed to update request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAPIForCreate(MaintenanceRequest request) {
        WebService.getInstance().createMaintenanceRequest(request,
                new RestEmptyCallBack<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {
                        if (response != null) {
                            successFullyUploaded("Maintenance request successfully sent");
                        } else {
                            failToUpload("Maintenance request creation failed.  Please try again later");
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        failToUpload("Maintenance request creation failed.  Please try again later");
                    }
                });
    }


    public void startActivityResult(Intent pictureIntent, int requestCode) {
        startActivityForResult(pictureIntent, requestCode);
    }

    private void toolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (isEdit) {
            toolbar.setTitle("Edit MRS");
        }
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
    public void onBackPressed() {

        if (_changesMade)
            showDiscardChangesDialog(this);
        else
            super.onBackPressed();
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


    private void uploadDocument() {
        Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_product_upload);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.color.transparent));
        ((TextView) dialog.findViewById(R.id.uploadDocumentUsingFilePicker)).setText("Photo/Video Upload");
        dialog.show();
        dialog.findViewById(R.id.uploadDocumentUsingCamera).setOnClickListener(new View.OnClickListener() {
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
                mrsItems.isBeforeSolve = isBeforeUpload;

                if (((file.length() / 1024) / 1024) < 9) {
                    if (mrsItems.isBeforeSolve) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        mrsAfterItemsArrayList.add(mrsItems);
                        mrsAfterImagesVideosAdapter.refresh(mrsAfterItemsArrayList);
                    }
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                }
                if (mrsItemsArrayList.size() >= 3) {
                    rlPriorUpload.setVisibility(View.GONE);
                }

            } else if (!videoPath.equals("")) {

                File compressedFile = createVideoFile();
                videoCompressorCamera(videoPath, compressedFile);

                //without video compress
                /*Uri videoLocalUri = Uri.parse(new File(videoPath).toString());
                File file = new File(videoPath);
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = file;
                mrsItems.bitMap = getPath(NewMaintenanceRequestActivity.this, videoLocalUri);
                mrsItems.isImage = true;
                mrsItems.videoUri = videoLocalUri;

                videoUri.add(videoLocalUri);

                if (file != null) {
                    if (((file.length() / 1024) / 1024) < 9) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 3) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }*/
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
                mrsItems.isBeforeSolve = isBeforeUpload;


                if (((file.length() / 1024) / 1024) < 9) {
                    if (mrsItems.isBeforeSolve) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        mrsAfterItemsArrayList.add(mrsItems);
                        mrsAfterImagesVideosAdapter.refresh(mrsAfterItemsArrayList);
                    }
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                }
                if (mrsItemsArrayList.size() >= 3) {
                    rlPriorUpload.setVisibility(View.GONE);
                }

            } else if (selectedMediaUri.toString().contains("video")) {
                //handle video

                File compressedFile = createVideoFile();
                videoCompressorGallery(FilePath.getPath(NewMaintenanceRequestActivity.this, selectedMediaUri), compressedFile, selectedMediaUri);

                //without video compress
                /*File file = getVideoThumbnail(NewMaintenanceRequestActivity.this,selectedMediaUri);*/
                /*File file = new File(FilePath.getPath(NewMaintenanceRequestActivity.this, selectedMediaUri));
                MRSItems mrsItems = new MRSItems();
                mrsItems.file = file;
                mrsItems.bitMap = getPath(NewMaintenanceRequestActivity.this, selectedMediaUri);
                mrsItems.isImage = false;
                mrsItems.videoUri = selectedMediaUri;

                videoUri.add(selectedMediaUri);*/

                /*if (file != null) {
                    if (((file.length() / 1024) / 1024) < 9) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(NewMaintenanceRequestActivity.this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 3) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }*/


            }

        }

    }

    //retriveThumbnail
    public Bitmap getThumbnailFromVideo(Context context, Uri uri) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(context, uri);
        return mMMR.getFrameAtTime();
    }


    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }

    @Override
    public void deleteImage(int position, MRSItems item) {
        new AlertDialog.Builder(this)
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

                        if (item.isBeforeSolve) {
                            if (item.imageUrl != null) {
                                deletedIds += item.maintenanceRequesFiles.id + ",";
                            }

                            mrsItemsArrayList.remove(position);
                            mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                            mrsImagesVideosAdapter.notifyDataSetChanged();

                            if (mrsItemsArrayList.size() < 3) {
                                rlPriorUpload.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (item.imageUrl != null) {
                                deletedIds += item.maintenanceRequesFiles.id + ",";
                            }
                            mrsAfterItemsArrayList.remove(position);
                            mrsAfterImagesVideosAdapter.refresh(mrsAfterItemsArrayList);
                            mrsAfterImagesVideosAdapter.notifyDataSetChanged();

                            if (mrsAfterItemsArrayList.size() < 3) {
                                rlAfterUpload.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
            }
        }).create().show();
    }

    //another wy to retrive thumb from video
    private File getVideoThumbnail(Context context, Uri videoUri) {
        return bitmapToFile(getThumbnailFromVideo(context, videoUri));
    }

    public static File bitmapToFile(Bitmap bitmap) {
        // File name like "image.png"
        //create a file to write bitmap data
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File filesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Invictus");

        File file = null;
        try {
            file = new File(filesDir + imageFileName + ".jpg");
            file.createNewFile();


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file; // it will return null
        }
    }

    public void successFullyUploaded(String string) {
        menuItem.setEnabled(true);
        ProgressDialog.dismissProgress();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    public void failToUpload(String string) {
        menuItem.setEnabled(true);
        ProgressDialog.dismissProgress();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //dismiss dialog
                    }
                }).create().show();
    }

    private void askPhotoVideo() {
        String[] choice = {"Photo", "Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    private void videoCompressorGallery(String videoPath, @NotNull File desFile, Uri
            selectedMediaUri) {

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
                mrsItems.bitMap = getThumbnailFromVideo(NewMaintenanceRequestActivity.this, selectedMediaUri);
                mrsItems.isImage = false;
                mrsItems.videoUri = videoLocalUri;

                videoUri.add(videoLocalUri);

                if (file != null) {
                    if (((file.length() / 1024) / 1024) < 49) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(NewMaintenanceRequestActivity.this, "File must be less then or equal to 50 MB", Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 3) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onStart(int i) {
                ProgressDialog.showProgress(NewMaintenanceRequestActivity.this);
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
                mrsItems.bitMap = getThumbnailFromVideo(NewMaintenanceRequestActivity.this, videoLocalUri);
                mrsItems.isImage = true;
                mrsItems.videoUri = videoLocalUri;

                videoUri.add(videoLocalUri);

                if (file != null) {
                    if (((file.length() / 1024) / 1024) < 49) {
                        mrsItemsArrayList.add(mrsItems);
                        mrsImagesVideosAdapter.refresh(mrsItemsArrayList);
                    } else {
                        Toast.makeText(NewMaintenanceRequestActivity.this, "File must be less then or equal to 50 MB", Toast.LENGTH_LONG).show();
                    }
                    if (mrsItemsArrayList.size() >= 3) {
                        rlPriorUpload.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onStart(int i) {
                // Compression start
                ProgressDialog.showProgress(NewMaintenanceRequestActivity.this);
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
