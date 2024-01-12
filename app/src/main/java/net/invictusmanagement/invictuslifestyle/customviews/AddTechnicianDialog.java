package net.invictusmanagement.invictuslifestyle.customviews;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddTechnicianDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddVendorDialogClick;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnBottomDialogButtonClick;
import net.invictusmanagement.invictuslifestyle.models.AddTechnician;
import net.invictusmanagement.invictuslifestyle.utils.AnimationUtils;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import okhttp3.ResponseBody;


public class AddTechnicianDialog extends DialogFragment implements View.OnClickListener {
    private SetOnAddTechnicianDialogClick listener;
    private EditText name;
    private MaterialTextView tvCancel, tvSave;
    private long vendorMappingId;

    public AddTechnicianDialog() {
    }

    @SuppressLint("ValidFragment")
    public AddTechnicianDialog(SetOnAddTechnicianDialogClick listener) {
        this.listener = listener;
    }

    private View mRootView;

    public void setVendorMappingId(long id) {
        vendorMappingId = id;
    }

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
        mRootView = inflater.inflate(R.layout.dialog_add_technician, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        name = view.findViewById(R.id.name);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                AddTechnicianDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSave:
                name.setError(null);
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("This field is required!");
                    name.requestFocus();
                } else {
                    callAPIAddTechnician();
                }
                break;
            case R.id.tvCancel:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void callAPIAddTechnician() {
        ProgressDialog.showProgress(getContext());
        AddTechnician addTechnician = new AddTechnician();
        addTechnician.technicianName = name.getText().toString();
        addTechnician.vendorMappingId = vendorMappingId;
        WebService.getInstance().addTechnicians(addTechnician, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    Toast.makeText(getContext(), "Technician has been added successfully!", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    listener.onAddTechnicianClicked(vendorMappingId);
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