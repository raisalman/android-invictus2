package net.invictusmanagement.invictuslifestyle.customviews;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.utils.AnimationUtils;


public class BottomMRSDialog extends DialogFragment implements View.OnClickListener {
    private SetOnBottomDialogButtonClick setOnBottomDialogButtonClick;
    private String role;

    public BottomMRSDialog() {
    }

    @SuppressLint("ValidFragment")
    public BottomMRSDialog(SetOnBottomDialogButtonClick setOnBottomDialogButtonClick, String role) {
        this.setOnBottomDialogButtonClick = setOnBottomDialogButtonClick;
        this.role = role;
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
        mRootView = inflater.inflate(R.layout.bottom_filter_mrs_layout, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        /*rgFilterType = view.findViewById(R.id.rgFilterType);*/
        view.findViewById(R.id.tvAll).setOnClickListener(this);
        view.findViewById(R.id.tvRequested).setOnClickListener(this);
        view.findViewById(R.id.tvActive).setOnClickListener(this);
        view.findViewById(R.id.tvClose).setOnClickListener(this);
        view.findViewById(R.id.tvCancel).setOnClickListener(this);
        if (role.equals(getString(R.string.role_vendor))
                || role.equals(getString(R.string.role_facility))
                || role.equals(getString(R.string.role_property_manager))
                || role.equals(getString(R.string.role_leasing_officer))) {
            view.findViewById(R.id.tvRequestToClose).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvRequestToClose).setOnClickListener(this);

            view.findViewById(R.id.tvNotSolve).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvNotSolve).setOnClickListener(this);
        }
    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                BottomMRSDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAll:
                setOnBottomDialogButtonClick.setFilter(1);
                dismissDialog();
                break;
            case R.id.tvRequested:
                setOnBottomDialogButtonClick.setFilter(2);
                dismissDialog();
                break;
            case R.id.tvActive:
                setOnBottomDialogButtonClick.setFilter(3);
                dismissDialog();
                break;
            case R.id.tvClose:
                setOnBottomDialogButtonClick.setFilter(4);
                dismissDialog();
                break;
            case R.id.tvRequestToClose:
                setOnBottomDialogButtonClick.setFilter(5);
                dismissDialog();
                break;

            case R.id.tvNotSolve:
                setOnBottomDialogButtonClick.setFilter(6);
                dismissDialog();
                break;
            case R.id.tvCancel:
                dismissDialog();
                break;
            default:
                break;
        }
    }
}