package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCondition;
import net.invictusmanagement.invictuslifestyle.models.MarketPlacePost;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SellActivity extends AppCompatActivity implements DeleteSingleProductImage {

    private Spinner spnCondition;
    private TextView tvCategory;
    private TextView tvSelectContactDetails, tvSubmit;
    private List<MarketPlaceCategories> marketPlaceCategories;
    private List<MarketPlaceCondition> marketCondition;
    private EditText edTitle, edDescription, edPrice;
    private RadioGroup rgContactDetails;
    private MarketPlacePost marketPlacePost;
    private LinearLayout rlProductUplaod;
    private RecyclerView rvProductImages;
    private final ArrayList<File> productImageList = new ArrayList();
    private final ArrayList<File> productImageListEdit = new ArrayList();
    private final ArrayList<Long> productIdListEdit = new ArrayList();
    private List<String> fileListUrl = new ArrayList<>();
    private ProductImagesAdapter productImagesAdapter;
    private PickFileFromDevice pickFileFromDevice;
    private BulletinBoard bulletinBoard;
    private Boolean isForEdit;
    private RadioButton rbBoth, rbEmail, rbPhone;
    private ArrayList<Integer> productIdDeleteListEdit = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        setToolbar();
        initViews();
        getBundleData();
        setCondition();
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void onClickListeners() {
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellActivity.this, CategoryBillBoardActivity.class);
                activityForResultLauncher.launch(intent);
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

    ActivityResultLauncher<Intent> activityForResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            tvCategory.setTag(data.getExtras().getString("ID"));
                            tvCategory.setText(data.getExtras().getString("NAME"));
                        }
                    });

    private void initViews() {
        pickFileFromDevice = new PickFileFromDevice(SellActivity.this, SellActivity.this);
        tvSubmit = findViewById(R.id.tvSubmit);
        rbPhone = findViewById(R.id.rbPhone);
        rbEmail = findViewById(R.id.rbEmail);
        rbBoth = findViewById(R.id.rbBoth);
        rlProductUplaod = findViewById(R.id.rlProductUplaod);
        rvProductImages = findViewById(R.id.rvProductImages);
        tvSelectContactDetails = findViewById(R.id.tvSelectContactDetails);
        rgContactDetails = findViewById(R.id.rgContactDetails);
        edPrice = findViewById(R.id.edPrice);
        edDescription = findViewById(R.id.edDescription);
        edTitle = findViewById(R.id.edTitle);
//        spnCategory = findViewById(R.id.spnCategory);
        spnCondition = findViewById(R.id.spnCondition);
        tvCategory = findViewById(R.id.tvCategory);
        productImagesAdapter = new ProductImagesAdapter(SellActivity.this, productImageList, this);
        rvProductImages.setAdapter(productImagesAdapter);
        edPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2), new InputFilterMinMax(0.00, 100000.00), new InputFilter.LengthFilter(9)});
    }


    private void postSell() {
        try {
            if (!ProgressDialog.checkProgressOpen()) {
                ProgressDialog.showProgress(SellActivity.this);
            }
            MobileDataProvider.getInstance().postMarketItem(getApplicationContext(), marketPlacePost, this, null, isForEdit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }
    }


    public void startActivityResult(Intent pictureIntent, int requestCode) {
        startActivityForResult(pictureIntent, requestCode);
    }

    private void deleteImageFromServer(List<Integer> item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(SellActivity.this);
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
                postSell();
                productIdDeleteListEdit = new ArrayList<>();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Unable to refresh voicemail. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();


    }

    private void setCondition() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(SellActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    marketCondition = MobileDataProvider.getInstance().getMarketPlacePostCondition();
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
                for (int i = 0; i < marketCondition.size(); i++) {
                    catString.add(marketCondition.get(i).text);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, catString);
                spnCondition.setAdapter(adapter);

                if (isForEdit) {
                    for (int i = 0; i < marketCondition.size(); i++) {
                        if (marketCondition.get(i).text.equals(bulletinBoard.condition)) {
                            spnCondition.setSelection(i);
                            break;
                        }
                    }
                }

                if (!success)
                    Toast.makeText(getApplicationContext(), "Unable to refresh voicemail. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void setCategories() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {

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
                if (isForEdit) {
                    for (int i = 0; i < marketPlaceCategories.size(); i++) {
                        if (marketPlaceCategories.get(i).name
                                .equals(bulletinBoard.marketPlaceCategoryName)) {
                            tvCategory.setText(marketPlaceCategories.get(i).name);
                            tvCategory.setTag(marketPlaceCategories.get(i).id + "");
                            break;
                        }
                    }
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
                Utilities.hideKeyboard(SellActivity.this);
            }
        });

        setTitle("Sell Form");
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

    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }


    private void checkAndSubmit() {
        boolean cancel = false;
        View focusView = null;

        edPrice.setError(null);
        edDescription.setError(null);
        edTitle.setError(null);
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
            marketPlacePost.AvailableDate = "";
            marketPlacePost.ContactType = contactType;
            marketPlacePost.Description = edDescription.getText().toString();
            marketPlacePost.Title = edTitle.getText().toString();
            marketPlacePost.Price = edPrice.getText().toString();
            marketPlacePost.ContactTime = "";
            marketPlacePost.IsClosed = false;
            marketPlacePost.IsApproved = false;
            marketPlacePost.IsSoldOut = false;
            marketPlacePost.IsService = false;
            marketPlacePost.IsHourPrice = false;
            for (int i = 0; i < marketCondition.size(); i++) {
                if (spnCondition.getSelectedItem().toString().equals(marketCondition.get(i).text)) {
                    marketPlacePost.ConditionType = Integer.parseInt(String.valueOf(marketCondition.get(i).value));
                    break;
                }
            }

//            for (int i = 0; i < marketPlaceCategories.size(); i++) {
//                if (spnCategory.getSelectedItem().toString().equals(marketPlaceCategories.get(i).name)) {
//                    marketPlacePost.MarketPlaceCategoryId = String.valueOf(marketPlaceCategories.get(i).id);
//                    break;
//                }

//            }
            marketPlacePost.MarketPlaceCategoryId = tvCategory.getTag().toString();
            if (productIdDeleteListEdit.size() > 0) {
                deleteImageFromServer(productIdDeleteListEdit);
            } else {
                postSell();
            }


        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }

    public void successFullyUploaded(String string, BulletinBoard bulletinBoard) {
        /*isUploading=false;*/
        ProgressDialog.dismissProgress();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent();
                        intent.putExtra("bulletinBoard", new Gson().toJson(bulletinBoard));
                        setResult(1, intent);
                        finish();
                        /*SellActivity.super.onBackPressed();*/
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
                        /*RedeemActivity.super.onBackPressed();*/
                    }
                }).create().show();
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
}