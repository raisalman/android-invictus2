package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.invictusmanagement.invictuslifestyle.ForbiddenException;
import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.PickFileFromDevice;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.models.Promotion;
import net.invictusmanagement.invictuslifestyle.models.RedeemCoupons;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RedeemActivity extends BaseActivity {

    public static final String EXTRA_BUSINESS_TYPE_JSON = "net.invictusmanagement.invictusmobile.business.type";
    public static final String EXTRA_BUSINESS_JSON = "net.invictusmanagement.invictusmobile.business";
    public static final String EXTRA_PROMOTION_ITEM = "net.invictusmanagement.invictusmobile.promotion";

    private Business _business;
    private Promotion promotionItem;
    private ConstraintLayout captureImage;
    private LinearLayout redeemButton;
    private ImageView imgReceipt;
    private PickFileFromDevice pickFileFromDevice;
    private TextView _tvUploadReceiptPhoto, tvReceiptPhotoWarning;
    private RedeemCoupons redeemCoupons;
    private EditText edReceiptNumber, edAmountPaid;
    private Boolean isReceiptCaptured = false;
    private File imageFile;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        setToolbar();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getDataFromBundle();
        initViews();
        onClickListeners();
    }

    private void getDataFromBundle() {
        if (getIntent().getExtras() != null) {
            _business = new Gson().fromJson(getIntent().getStringExtra(EXTRA_BUSINESS_JSON), new TypeToken<Business>() {
            }.getType());
            promotionItem = new Gson().fromJson(getIntent().getStringExtra(EXTRA_PROMOTION_ITEM), new TypeToken<Promotion>() {
            }.getType());
        }
    }

    private void onClickListeners() {
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _swipeRefreshLayout.setRefreshing(false);
            }
        });


        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(RedeemActivity.this);
                checkPermissions();
            }
        });

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(RedeemActivity.this);
                if (!isUploading) {
                    redeemCoupons();
                }
            }
        });

    }

    private void redeemCoupons() {
        if (edReceiptNumber.getText().toString().length() != 0) {
            if (edAmountPaid.getText().toString().length() != 0) {
                if (isReceiptCaptured) {
                    /*TODO call API*/
                    if (_business != null && promotionItem != null) {
                        redeemCoupons = new RedeemCoupons();
                        redeemCoupons.orderAmount = edAmountPaid.getText().toString();
                        redeemCoupons.promotionId = String.valueOf(promotionItem.id);
                        redeemCoupons.receiptNumber = edReceiptNumber.getText().toString();
                        redeemCoupons.businessId = String.valueOf(_business.id);
                        redeemCoupons.isApproved = false;
                        redeemCoupons.applicationUserId = String.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("userId", 0));
                        redeemCoupons.receiptImage = imageFile;
                        callRedeemCoupons();
                        isUploading = true;
                    }
                } else {
                    tvReceiptPhotoWarning.setVisibility(View.VISIBLE);
                }
            } else {
                edAmountPaid.setError("Please enter amount paid");
                edAmountPaid.requestFocus();
            }
        } else {
            edReceiptNumber.setError("Please enter receipt number");
            edReceiptNumber.requestFocus();
        }
    }

    private void initViews() {
        captureImage = findViewById(R.id.llReceipt);
        edAmountPaid = findViewById(R.id.edAmountPaid);
        edReceiptNumber = findViewById(R.id.edReceiptNumber);
        tvReceiptPhotoWarning = findViewById(R.id.tvReceiptPhotoWarning);
        redeemButton = findViewById(R.id.llRedeem);
        imgReceipt = findViewById(R.id.imgReceipt);
        _tvUploadReceiptPhoto = findViewById(R.id.tvUploadReceiptPhotoInside);
        pickFileFromDevice = new PickFileFromDevice(this, this);

        _swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    public void startActivityResult(Intent pictureIntent, int requestCode) {
        startActivityForResult(pictureIntent, requestCode);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUploading) {
                    onBackPressed();
                    Utilities.hideKeyboard(RedeemActivity.this);
                }

            }
        });

        setTitle("Redeem Coupon");
    }

    public void successFullyUploaded(String string) {
        isUploading = false;
        _swipeRefreshLayout.setRefreshing(false);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(string)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        RedeemActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    public void failToUpload(String string) {
        isUploading = false;
        _swipeRefreshLayout.setRefreshing(false);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = pickFileFromDevice.getImagePath();

        if (requestCode == PickFileFromDevice.CAPTURE_IMAGE && path != null && resultCode != 0) {
            _tvUploadReceiptPhoto.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            try {
                imgReceipt.setImageBitmap(getRotateImaged(path, myBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
            isReceiptCaptured = true;
            tvReceiptPhotoWarning.setVisibility(View.GONE);
            imageFile = new File(path);
        }

    }


    private void checkPermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {
                    pickFileFromDevice.showCameraIntent(PickFileFromDevice.CAPTURE_IMAGE);
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

    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 100);
    }

    private void callRedeemCoupons() {
        try {
            _swipeRefreshLayout.setRefreshing(true);
            MobileDataProvider.getInstance().redeemCoupon(getApplicationContext(), redeemCoupons, RedeemActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ForbiddenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isUploading) {
            Utilities.hideKeyboard(RedeemActivity.this);
            super.onBackPressed();
        }
    }


    private Bitmap getRotateImaged(String photoPath, Bitmap bitmap) throws IOException {

        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
        );

        Bitmap rotatedImage = null;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            rotatedImage = bitmap;
        }
        return rotatedImage;

    }

    private Bitmap rotateImage(Bitmap source, Float angle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
