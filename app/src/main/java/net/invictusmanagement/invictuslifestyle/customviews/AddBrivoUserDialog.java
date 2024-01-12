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
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddBrivoUserDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddVendorDialogClick;
import net.invictusmanagement.invictuslifestyle.models.AddBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.AddVendor;
import net.invictusmanagement.invictuslifestyle.utils.AnimationUtils;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import okhttp3.ResponseBody;


public class AddBrivoUserDialog extends DialogFragment implements View.OnClickListener {
    private SetOnAddBrivoUserDialogClick listener;
    private EditText tvUserName, tvPassword;
    private MaterialTextView tvCancel, tvSave;

    private boolean isFormatted = true;
    private boolean isUnFormatted = false;

    public AddBrivoUserDialog() {
    }

    @SuppressLint("ValidFragment")
    public AddBrivoUserDialog(SetOnAddBrivoUserDialogClick listener) {
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
        mRootView = inflater.inflate(R.layout.dialog_add_brivo_user, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);


        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                AddBrivoUserDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSave:
                tvUserName.setError(null);
                tvPassword.setError(null);
                if (tvUserName.getText().toString().length() == 0) {
                    tvUserName.setError("All fields are required!");
                    tvUserName.requestFocus();
                } else if (tvPassword.getText().toString().length() == 0) {
                    tvPassword.setError("All fields are required!");
                    tvPassword.requestFocus();
                } else {
                    callAPIAddBrivoUser();
                }
                break;
            case R.id.tvCancel:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void callAPIAddBrivoUser() {
        ProgressDialog.showProgress(getContext());
        AddBrivoSmartHomeUser brivoSmartHomeUser = new AddBrivoSmartHomeUser();
        brivoSmartHomeUser.bshPassword = tvPassword.getText().toString();
        brivoSmartHomeUser.bshUsername = tvUserName.getText().toString();
        WebService.getInstance().addBrivoSmartHomeUser(brivoSmartHomeUser, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    Toast.makeText(getContext(), "Brivo user has been added successfully!", Toast.LENGTH_SHORT).show();
                    tvUserName.setText("");
                    tvPassword.setText("");
                    listener.onAddBrivoUserClicked();
                } else Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
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