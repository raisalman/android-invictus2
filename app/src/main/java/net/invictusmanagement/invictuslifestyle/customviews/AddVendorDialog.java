package net.invictusmanagement.invictuslifestyle.customviews;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textview.MaterialTextView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddVendorDialogClick;
import net.invictusmanagement.invictuslifestyle.models.AddVendor;
import net.invictusmanagement.invictuslifestyle.utils.AnimationUtils;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import okhttp3.ResponseBody;


public class AddVendorDialog extends DialogFragment implements View.OnClickListener {
    private SetOnAddVendorDialogClick listener;
    private EditText companyName, phone, email;
    private MaterialTextView tvCancel, tvSave;

    private boolean isFormatted = true;
    private boolean isUnFormatted = false;

    public AddVendorDialog() {
    }

    @SuppressLint("ValidFragment")
    public AddVendorDialog(SetOnAddVendorDialogClick listener) {
        this.listener = listener;
    }

    private View mRootView;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = inflater.inflate(R.layout.dialog_add_vendor, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        companyName = view.findViewById(R.id.companyName);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);

        phone.addTextChangedListener(new TextWatcher() {
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
                        phone.setText(s.toString().replaceAll("[^0-9]", ""));
                        phone.setSelection(phone.getText().length());
                    }

                }

                if (filteredString == 10) {
                    if (!isFormatted) {
                        isUnFormatted = false;
                        isFormatted = true;
                        phone.setText(Utilities.formatPhone(phone.getText().toString().trim()));
                        phone.setSelection(phone.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                AddVendorDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSave:
                companyName.setError(null);
                email.setError(null);
                phone.setError(null);
                if (companyName.getText().toString().length() == 0) {
                    companyName.setError("All fields are required!");
                    companyName.requestFocus();
                } else if (email.getText().toString().length() == 0) {
                    email.setError("All fields are required!");
                    email.requestFocus();
                } else if (!Utilities.isValidEmail(email.getText())) {
                    email.setError("Please input valid email");
                    email.requestFocus();
                } else if (phone.getText().toString().length() != 14) {
                    phone.setError("Please enter proper phone number");
                    phone.requestFocus();
                } else {
                    callAPIAddVendor();
                }
                break;
            case R.id.tvCancel:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void callAPIAddVendor() {
        ProgressDialog.showProgress(getContext());
        AddVendor vendor = new AddVendor();
        vendor.email = email.getText().toString();
        vendor.name = companyName.getText().toString();
        vendor.phoneNumber = phone.getText().toString();
        WebService.getInstance().addVendors(vendor, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    Toast.makeText(getContext(), "Vendor has been added successfully!", Toast.LENGTH_SHORT).show();
                    companyName.setText("");
                    email.setText("");
                    phone.setText("");
                    listener.onAddVendorClicked();
                } else
                    Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }
}