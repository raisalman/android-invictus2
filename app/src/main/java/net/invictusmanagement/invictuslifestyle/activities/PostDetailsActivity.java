package net.invictusmanagement.invictuslifestyle.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rd.PageIndicatorView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.ImageAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.ImageVisibleOther;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceImage;
import net.invictusmanagement.invictuslifestyle.models.MySpannable;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity implements ImageVisibleOther {


    private BulletinBoard bulletinBoard;
    private TextView tvProductCondition, tvServiceTitle, tvServiceDateTime, tvProductServicePrice, tvPerSerice, tvContactName, tvContactMobileNumber, tvContactEmail, tvCloseSoldOut;
    private ReadMoreTextView tvDescription;
    private ViewPager viewPager;
    private PageIndicatorView dots_indicator;
    private LinearLayout llProductCondition;
    private ImageView imgFavorite;
    private Boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psot_details);

        initViews();
        getBundleData();
        setToolbar();
        onClickListeners();
    }

    private void onClickListeners() {

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite) {
                    postAsUnFavorite(bulletinBoard);
                } else {
                    postAsFavorite(bulletinBoard);
                }
            }
        });

        tvContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailSender();
            }
        });

        tvContactMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialPad();
            }
        });
    }

    private void tvCloseSoldOutApi() {
        new AsyncTask<AccessPoint, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(PostDetailsActivity.this);
            }

            @Override
            protected Boolean doInBackground(AccessPoint... params) {
                try {

                    MobileDataProvider.getInstance().markAsCloseOrSoldOut(Integer.valueOf(String.valueOf(bulletinBoard.id)));
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                finish();
                if (success)
                    if (bulletinBoard.isService) {
                        Toast.makeText(PostDetailsActivity.this, "Successfully close", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PostDetailsActivity.this, "Successfully sold out", Toast.LENGTH_LONG).show();
                    }

                else if (bulletinBoard.isService) {
                    Toast.makeText(PostDetailsActivity.this, "Failed to close", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Failed to sold out", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void openDialPad() {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(bulletinBoard.phone));

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CALL_PHONE)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", bulletinBoard.phone, null));
                            startActivity(intent);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(getApplicationContext(), "From settings please grant Call Phone permission.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void openEmailSender() {
        Utilities.sendEmail(PostDetailsActivity.this, bulletinBoard.email, "", "");
    }

    private void initViews() {
        imgFavorite = findViewById(R.id.imgFavorite);
        dots_indicator = findViewById(R.id.dots_indicator);
        viewPager = findViewById(R.id.viewPager);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvProductCondition = findViewById(R.id.tvProductCondition);
        tvCloseSoldOut = findViewById(R.id.tvCloseSoldOut);
        tvContactMobileNumber = findViewById(R.id.tvContactMobileNumber);
        tvContactName = findViewById(R.id.tvContactName);
        llProductCondition = findViewById(R.id.llProductCondition);
        tvDescription = findViewById(R.id.tvDescription);
        tvPerSerice = findViewById(R.id.tvPerSerice);
        tvProductServicePrice = findViewById(R.id.tvProductServicePrice);
        tvServiceDateTime = findViewById(R.id.tvServiceDateTime);
        tvServiceTitle = findViewById(R.id.tvServiceTitle);
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            bulletinBoard = new Gson().fromJson(getIntent().getStringExtra("GeneralDetails"), new TypeToken<BulletinBoard>() {
            }.getType());

            setData(bulletinBoard);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setData(BulletinBoard bulletinBoard) {
        isFavourite = bulletinBoard.isFavorite;
        changePostFavUnFav();

        if (bulletinBoard.isService) {
            if (bulletinBoard.isClosed) {
                imgFavorite.setVisibility(View.GONE);
            } else {
                imgFavorite.setVisibility(View.VISIBLE);
            }
        } else {
            if (bulletinBoard.isSoldOut) {
                imgFavorite.setVisibility(View.GONE);
            } else {
                imgFavorite.setVisibility(View.VISIBLE);
            }
        }

        if (bulletinBoard.marketPlaceImages.size() > 0) {
            ImageAdapter imageAdapter = new ImageAdapter(PostDetailsActivity.this,
                    bulletinBoard.marketPlaceImages, this,
                    true, true, false);
            viewPager.setAdapter(imageAdapter);
            viewPager.setCurrentItem(0);
            viewPager.setVisibility(View.VISIBLE);
            dots_indicator.setCount(bulletinBoard.marketPlaceImages.size());
            dots_indicator.setSelection(0);

            if (bulletinBoard.marketPlaceImages.size() != 1) {
                dots_indicator.setVisibility(View.VISIBLE);
            } else {
                dots_indicator.setVisibility(View.GONE);
            }

        } else {
            viewPager.setVisibility(View.GONE);
            dots_indicator.setVisibility(View.GONE);
        }

        tvServiceTitle.setText(bulletinBoard.title);
        tvDescription.setText(bulletinBoard.description);
        tvDescription.setTrimExpandedText("\nShow less...");
        tvDescription.setTrimCollapsedText("\nShow more...");
        tvDescription.setColorClickableText(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        if (bulletinBoard.contectType == 1) {
            tvContactEmail.setText(bulletinBoard.email);
            tvContactMobileNumber.setVisibility(View.GONE);
        } else if (bulletinBoard.contectType == 2) {
            tvContactMobileNumber.setText(bulletinBoard.phone);
            tvContactEmail.setVisibility(View.GONE);
        } else if (bulletinBoard.contectType == 3) {
            tvContactMobileNumber.setText(bulletinBoard.phone);
            tvContactEmail.setText(bulletinBoard.email);
            tvContactEmail.setVisibility(View.VISIBLE);
            tvContactMobileNumber.setVisibility(View.VISIBLE);
        }

        tvProductServicePrice.setText("$" + bulletinBoard.price);
        tvContactName.setText(bulletinBoard.residentName);
        if (bulletinBoard.isService) {
            if (bulletinBoard.availableDate != null) {
                String strCurrentDate = bulletinBoard.availableDate;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date newDate = null;
                try {
                    newDate = format.parse(strCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                format = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                String datee = format.format(newDate);
                String currentString = bulletinBoard.contactTime;
                if (currentString.contains("to")) {
                    String[] separated = currentString.split("to");
                    tvServiceDateTime.setText(datee + ", " + convertReversDate(separated[0]).trim() + " to " + convertReversDate(separated[1]).trim());
                } else {
                    tvServiceDateTime.setText(datee + ", " + bulletinBoard.contactTime);
                }

            }

            tvCloseSoldOut.setText("Close");
            tvServiceTitle.setVisibility(View.VISIBLE);
            llProductCondition.setVisibility(View.GONE);
            tvServiceDateTime.setVisibility(View.VISIBLE);
            tvPerSerice.setVisibility(View.VISIBLE);

            if (bulletinBoard.isHourPrice) {
                tvPerSerice.setText("per hour");
            } else {
                tvPerSerice.setText("per service");
            }

        } else {
            tvCloseSoldOut.setText("Sold Out");
            tvServiceTitle.setVisibility(View.VISIBLE);
            llProductCondition.setVisibility(View.VISIBLE);
            tvProductCondition.setText(bulletinBoard.condition);
            tvServiceDateTime.setVisibility(View.GONE);
            tvPerSerice.setVisibility(View.GONE);

        }

        if (bulletinBoard.isMyPost) {
            tvCloseSoldOut.setVisibility(View.VISIBLE);
        } else {
            tvCloseSoldOut.setVisibility(View.INVISIBLE);
        }

        if (bulletinBoard.isSoldOut || bulletinBoard.isClosed) {
            tvCloseSoldOut.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this, R.drawable.squarebutton_lightred));
            tvCloseSoldOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            tvCloseSoldOut.setBackground(ContextCompat.getDrawable(PostDetailsActivity.this, R.drawable.squarebutton_red));
            tvCloseSoldOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String string = "";
                    if (bulletinBoard.isService) {
                        string = "Are you sure you want to close this service?";
                    } else {
                        string = "Are you sure you want to make this product sold out?";
                    }
                    new AlertDialog.Builder(PostDetailsActivity.this)
                            .setCancelable(false)
                            .setMessage(string)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    tvCloseSoldOutApi();
                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss dialog
                        }
                    }).create().show();

                }
            });
        }

    }

    private void changePostFavUnFav() {
        if (isFavourite) {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
        } else {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfavorite));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvToolBarTitle = findViewById(R.id.tvToolBarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        if (bulletinBoard.isMyPost) {
            if (bulletinBoard.isService) {
                if (!bulletinBoard.isClosed) {
                    toolbar.inflateMenu(R.menu.menu_edit);
                }
            } else {
                if (!bulletinBoard.isSoldOut) {
                    toolbar.inflateMenu(R.menu.menu_edit);
                }
            }
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit) {
                        Intent intent = new Intent();
                        int requestCode;
                        if (bulletinBoard.isService) {
                            intent = new Intent(getApplicationContext(), ServiceActivity.class);
                            requestCode = 1;
                        } else {
                            intent = new Intent(getApplicationContext(), SellActivity.class);
                            requestCode = 2;
                        }
                        intent.putExtra("forEdit", new Gson().toJson(bulletinBoard));

                        startActivityForResult(intent, requestCode);
                        return true;
                    } else {
                        return PostDetailsActivity.super.onOptionsItemSelected(item);
                    }
                }
            });
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(PostDetailsActivity.this);
            }
        });

        if (bulletinBoard.isService) {
            tvToolBarTitle.setText("Service Detail");
        } else {
            tvToolBarTitle.setText("Sell Detail");
        }


    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " \n" + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " \n" + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " \n" + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Show less... ", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 10, "Show more...", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (data != null) {
                bulletinBoard = new Gson().fromJson(data.getStringExtra("bulletinBoard"), new TypeToken<BulletinBoard>() {
                }.getType());

                setData(bulletinBoard);
            }
        } else if (resultCode == 2) {
            if (data != null) {
                bulletinBoard = new Gson().fromJson(data.getStringExtra("bulletinBoard"), new TypeToken<BulletinBoard>() {
                }.getType());

                setData(bulletinBoard);
            }
        }
    }


    public void postAsFavorite(final BulletinBoard item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(PostDetailsActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().markPostAsFav(Integer.valueOf(String.valueOf(item.id)));
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
                    isFavourite = true;
                    changePostFavUnFav();
                }
            }
        }.execute();
    }

    public void postAsUnFavorite(final BulletinBoard item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(PostDetailsActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().markPostAsUnFav(Integer.valueOf(String.valueOf(item.id)));
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
                    isFavourite = false;
                    changePostFavUnFav();
                }
            }
        }.execute();
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
    public void showImage(List<MarketPlaceImage> galImages, int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("selectedItem", position);
        intent.putExtra("imageList", new Gson().toJson(galImages));
        startActivity(intent);
    }
}