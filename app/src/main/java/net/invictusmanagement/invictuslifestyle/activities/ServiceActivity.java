package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.ForbiddenException;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ProductImagesAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.DecimalDigitsInputFilter;
import net.invictusmanagement.invictuslifestyle.customviews.FileUtils;
import net.invictusmanagement.invictuslifestyle.customviews.InputFilterMinMax;
import net.invictusmanagement.invictuslifestyle.customviews.PickFileFromDevice;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.DeleteSingleProductImage;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;
import net.invictusmanagement.invictuslifestyle.models.MarketPlacePost;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServiceActivity extends AppCompatActivity implements DeleteSingleProductImage {

    private Spinner spnCategory;
    private TextView tvSelectContactDetails, tvSubmit;
    private List<MarketPlaceCategories> marketPlaceCategories;
    private EditText from_date, from_time, to_time, edTitle, edDescription, edPrice;
    private RadioGroup rgContactDetails;
    private Calendar _toDateTime, _fromDateTime, _fromDateOnly;
    private MarketPlacePost marketPlacePost;
    private PickFileFromDevice pickFileFromDevice;
    private LinearLayout rlProductUplaod;
    private RecyclerView rvProductImages;
    private final ArrayList<File> productImageList = new ArrayList();
    private final ArrayList<File> productImageListEdit = new ArrayList();
    private final ArrayList<Long> productIdListEdit = new ArrayList();
    private ArrayList<Integer> productIdDeleteListEdit = new ArrayList();
    private List<String> fileListUrl = new ArrayList<>();
    private ProductImagesAdapter productImagesAdapter;
    private BulletinBoard bulletinBoard;
    private Boolean isForEdit;
    private RadioButton rbBoth, rbEmail, rbPhone;
    private CheckBox chkIsHour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        setToolbar();
        initViews();
        getBundleData();
        setCategories();
        onClickListeners();
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            isForEdit = true;
            bulletinBoard = new Gson().fromJson(getIntent().getStringExtra("forEdit"), new TypeToken<BulletinBoard>() {
            }.getType());

            setData();
        } else {
            isForEdit = false;
        }
    }

    private void setData() {
        tvSubmit.setText("UPDATE");
        edTitle.setText(bulletinBoard.title);
        edDescription.setText(bulletinBoard.description);
        edPrice.setText(bulletinBoard.price);
        if (bulletinBoard.marketPlaceImages.size() > 0) {
            fileListUrl = new ArrayList<>();
            for (int i = 0; i < bulletinBoard.marketPlaceImages.size(); i++) {
                fileListUrl.add(bulletinBoard.marketPlaceImages.get(i).marketPlaceImageUrl);
            }
            for (int i = 0; i < bulletinBoard.marketPlaceImages.size(); i++) {
                productImageList.add(downloadImageFile(fileListUrl.get(i)));
                productImageListEdit.add(downloadImageFile(fileListUrl.get(i)));
                productIdListEdit.add(bulletinBoard.marketPlaceImages.get(i).id);
            }

            productImagesAdapter.refresh(productImageList, fileListUrl);
            if (productImageList.size() >= 5) {
                rlProductUplaod.setVisibility(View.GONE);
            }

        }
        if (bulletinBoard.contectType == 1) {
            rbEmail.setChecked(true);
        } else if (bulletinBoard.contectType == 2) {
            rbPhone.setChecked(true);
        } else if (bulletinBoard.contectType == 3) {
            rbBoth.setChecked(true);
        }

        String currentString = bulletinBoard.contactTime;
        if (currentString.contains("to")) {
            String[] separated = currentString.split("to");
            from_time.setText(convertReversDate(separated[0]).trim());
            to_time.setText(convertReversDate(separated[1]).trim());

            String[] separatedTimeFrom = separated[0].trim().split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(separatedTimeFrom[0]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(separatedTimeFrom[1]));
            calendar.set(Calendar.SECOND, 0);
            _fromDateOnly = calendar;
        }

        if (isValidDate(bulletinBoard.availableDate)) {
            Utilities.converDateWithFormatter(bulletinBoard.availableDate, from_date, "yyyy-MM-dd'T'HH:mm:ss", "MM/dd/yy");
        }

        chkIsHour.setChecked(bulletinBoard.isHourPrice);

    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
        super.onBackPressed();
    }

    private void onClickListeners() {
        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(true);
            }
        });
        to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(false);
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSubmit();
            }
        });
        rlProductUplaod.setOnClickListener(new View.OnClickListener() {
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
        dialog.show();
        dialog.findViewById(R.id.uploadDocumentUsingCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickFileFromDevice.showCameraIntent(PickFileFromDevice.CAPTURE_IMAGE);
            }
        });

        dialog.findViewById(R.id.uploadDocumentUsingFilePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickFileFromDevice.showFileChooser(PickFileFromDevice.PICK_IMAGE);
            }
        });

        dialog.findViewById(R.id.dialogDocUploadCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        String path = pickFileFromDevice.getImagePath();

        if (requestCode == PickFileFromDevice.CAPTURE_IMAGE && path != null && resultCode != 0) {
            productImageList.add(Utilities.compressedFile(new File(path)));
            /*productImageList.add(new File(Utilities.compressedFile(new File(path))));*/
            productImagesAdapter.refresh(productImageList);
            if (productImageList.size() >= 5) {
                rlProductUplaod.setVisibility(View.GONE);
            }
        } else if (requestCode == PickFileFromDevice.PICK_IMAGE && data != null) {
            File file = FileUtils.getFile(this, data.getData());
            if (file != null) {
                if (((file.length() / 1024) / 1024) < 9) {
                    productImageList.add(Utilities.compressedFile(file));
                    /*productImageList.add(new File(Utilities.compressedFile(file)));*/
                    productImagesAdapter.refresh(productImageList);
                } else {
                    Toast.makeText(this, "File must me less then or equal to 10 MB", Toast.LENGTH_LONG).show();
                }
                if (productImageList.size() >= 5) {
                    rlProductUplaod.setVisibility(View.GONE);
                }
            }

        }

    }

    private void openTimePicker(boolean isForFrom) {

        if (isForFrom) {
            Utilities.showTimePickerTo(false, getSupportFragmentManager(), ServiceActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
                @Override
                public void dateTimeChanged(Calendar date) {
                    _fromDateTime = date;
                    _fromDateOnly = date;
                    from_time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));

                    _toDateTime = date;
                    _toDateTime.add(Calendar.DATE, 1);
                    to_time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
                }
            });
        } else {
            Utilities.showTimePickerFrom(getSupportFragmentManager(), ServiceActivity.this, _fromDateOnly, new Utilities.onDateTimePickerChangedListener() {
                @Override
                public void dateTimeChanged(Calendar date) {
                    _toDateTime = date;
                    to_time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));
                }
            });
        }

    }

    private void openDatePicker() {

        Utilities.showDatePickerWithMinDate(ServiceActivity.this, _fromDateTime, new Utilities.onDateTimePickerChangedListener() {
            @Override
            public void dateTimeChanged(Calendar date) {
                _fromDateTime = date;
                from_date.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(date.getTime()));
            }
        });
    }

    private void initViews() {
        pickFileFromDevice = new PickFileFromDevice(ServiceActivity.this, ServiceActivity.this);
        chkIsHour = findViewById(R.id.chkIsHour);
        rbPhone = findViewById(R.id.rbPhone);
        rbEmail = findViewById(R.id.rbEmail);
        rbBoth = findViewById(R.id.rbBoth);
        rlProductUplaod = findViewById(R.id.rlProductUplaod);
        tvSubmit = findViewById(R.id.tvSubmit);
        rvProductImages = findViewById(R.id.rvProductImages);
        tvSelectContactDetails = findViewById(R.id.tvSelectContactDetails);
        rgContactDetails = findViewById(R.id.rgContactDetails);
        edPrice = findViewById(R.id.edPrice);
        edDescription = findViewById(R.id.edDescription);
        edTitle = findViewById(R.id.edTitle);
        spnCategory = findViewById(R.id.spnCategory);
        from_date = findViewById(R.id.from_date);
        from_time = findViewById(R.id.from_time);
        to_time = findViewById(R.id.to_time);
        _fromDateTime = Calendar.getInstance();
        _fromDateOnly = Calendar.getInstance();
        _toDateTime = Calendar.getInstance();
        _toDateTime.add(Calendar.DATE, 1);
        from_date.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime()));
        from_time.setInputType(InputType.TYPE_NULL);
        from_time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_fromDateTime.getTime()));
        to_time.setInputType(InputType.TYPE_NULL);
        to_time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(_toDateTime.getTime()));
        productImagesAdapter = new ProductImagesAdapter(ServiceActivity.this, productImageList, this);
        rvProductImages.setAdapter(productImagesAdapter);
        edPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2), new InputFilterMinMax(0.00, 100000.00), new InputFilter.LengthFilter(9)});

    }


    private void postServices() {
        try {
            if (!ProgressDialog.checkProgressOpen()) {
                ProgressDialog.showProgress(ServiceActivity.this);
            }
            MobileDataProvider.getInstance().postMarketItem(getApplicationContext(),
                    marketPlacePost, null, this, isForEdit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }
    }


    private void setCategories() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(ServiceActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    marketPlaceCategories = MobileDataProvider.getInstance().getMarketPlaceCategories();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                ArrayList<String> catString = new ArrayList<>();
                for (int i = 0; i < marketPlaceCategories.size(); i++) {
                    if (marketPlaceCategories.get(i).isService) {
                        catString.add(marketPlaceCategories.get(i).name);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, catString);
                spnCategory.setAdapter(adapter);

                if (isForEdit) {
                    for (int i = 0; i < catString.size(); i++) {
                        if (catString.get(i).equals(bulletinBoard.marketPlaceCategoryName)) {
                            spnCategory.setSelection(i);
                            break;
                        }
                    }
                }

                if (!success)
                    Toast.makeText(getApplicationContext(), "Unable to refresh voicemail. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();


    }

    private void deleteImageFromServer(List<Integer> item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(ServiceActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().deleteMarketPlaceImage(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                /*ProgressDialog.dismissProgress();*/
                postServices();
                productIdDeleteListEdit = new ArrayList<>();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Unable to refresh voicemail. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();


    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(ServiceActivity.this);
            }
        });

        setTitle("Service Form");
    }


    private void checkAndSubmit() {
        boolean cancel = false;
        View focusView = null;

        edPrice.setError(null);
        edDescription.setError(null);
        edTitle.setError(null);
        to_time.setError(null);
        from_time.setError(null);
        from_date.setError(null);
        tvSelectContactDetails.setVisibility(View.INVISIBLE);


        if (TextUtils.isEmpty(edTitle.getText().toString())) {
            edTitle.setError(getString(R.string.error_field_required));
            focusView = edTitle;
            cancel = true;
        } else if (TextUtils.isEmpty(edDescription.getText().toString())) {
            edDescription.setError(getString(R.string.error_field_required));
            focusView = edDescription;
            cancel = true;
        } else if (TextUtils.isEmpty(edPrice.getText().toString())) {
            edPrice.setError(getString(R.string.error_field_required));
            focusView = edPrice;
            cancel = true;
        } else if (rgContactDetails.getCheckedRadioButtonId() == -1) {
            tvSelectContactDetails.setVisibility(View.VISIBLE);
            cancel = true;
        }

        if (!cancel) {
            int selectedId = rgContactDetails.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);
            int contactType = 0;
            if (radioButton.getText().equals("Show My Email And Phone")) {
                contactType = 3;
            } else if (radioButton.getText().equals("Show My Email")) {
                contactType = 1;
            } else if (radioButton.getText().equals("Show My Phone")) {
                contactType = 2;
            }


            String strCurrentDate = from_date.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
            Date newDate = null;
            try {
                newDate = format.parse(strCurrentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            /*format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");*/
            String date = format.format(newDate);


            marketPlacePost = new MarketPlacePost();
            if (isForEdit) {
                marketPlacePost.id = bulletinBoard.id;
            } else {
                marketPlacePost.id = 0;
            }
            if (isForEdit) {
                productImageList.removeAll(productImageListEdit);
            }

            marketPlacePost.MarketPlaceImages = productImageList;
            marketPlacePost.AvailableDate = date;
            marketPlacePost.ConditionType = 0;
            marketPlacePost.ContactType = contactType;
            marketPlacePost.Description = edDescription.getText().toString();
            marketPlacePost.Title = edTitle.getText().toString();
            marketPlacePost.Price = edPrice.getText().toString();
            marketPlacePost.ContactTime = convertDate(from_time.getText().toString()) + " to " + convertDate(to_time.getText().toString());
            marketPlacePost.IsClosed = false;
            marketPlacePost.IsApproved = false;
            marketPlacePost.IsSoldOut = false;
            marketPlacePost.IsService = true;
            marketPlacePost.IsHourPrice = chkIsHour.isChecked();
            for (int i = 0; i < marketPlaceCategories.size(); i++) {
                if (spnCategory.getSelectedItem().toString().equals(marketPlaceCategories.get(i).name)) {
                    marketPlacePost.MarketPlaceCategoryId = String.valueOf(marketPlaceCategories.get(i).id);
                    break;
                }
            }

            if (productIdDeleteListEdit.size() > 0) {
                deleteImageFromServer(productIdDeleteListEdit);
            } else {
                postServices();
            }


        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }


    public void successFullyUploaded(String string, BulletinBoard bulletinBoard) {
        ProgressDialog.dismissProgress();
        showSuccessDialog(string, bulletinBoard);

    }

    private void setResultAndFinish(BulletinBoard bulletinBoard) {
        Intent intent = new Intent();
        intent.putExtra("bulletinBoard", new Gson().toJson(bulletinBoard));
        setResult(2, intent);
        finish();
    }

    private void showSuccessDialog(String string, BulletinBoard bulletinBoard) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        setResultAndFinish(bulletinBoard);
                    }
                }).create().show();
    }

    public void failToUpload(String string) {
        /*isUploading=false;*/
        ProgressDialog.dismissProgress();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();
    }

    private String convertDate(String datePasssed) {
        /*"10:30 PM"*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = parseFormat.parse(datePasssed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }

    private String convertReversDate(String datePasssed) {
        /*"10:30 PM"*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = parseFormat.parse(datePasssed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }

    @Override
    public void deleteImage(int position, File item) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (isForEdit) {
                            for (int i = 0; i < productImageListEdit.size(); i++) {
                                if (item.equals(productImageListEdit.get(i))) {
                                    /*  deleteImageFromServer(Integer.parseInt(String.valueOf(productIdListEdit.get(i))));*/
                                    productIdDeleteListEdit.add(Integer.valueOf(String.valueOf(productIdListEdit.get(i))));
                                    productImageListEdit.remove(i);
                                    fileListUrl.remove(i);
                                    productIdListEdit.remove(i);
                                    break;
                                }
                            }
                        }
                        productImageList.remove(position);
                        productImagesAdapter.refresh(productImageList, fileListUrl);
                        productImagesAdapter.notifyDataSetChanged();

                        if (productImageList.size() < 5) {
                            rlProductUplaod.setVisibility(View.VISIBLE);
                        }
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
            }
        }).create().show();
    }


    private static File downloadImageFile(String url) {
        URL urll = null;
        try {
            urll = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new File(urll.getFile());
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}