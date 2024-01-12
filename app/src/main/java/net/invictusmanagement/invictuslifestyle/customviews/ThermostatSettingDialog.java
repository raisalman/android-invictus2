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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.bozapro.circularsliderrange.CircularSliderRange;
import com.bozapro.circularsliderrange.ThumbEvent;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.SetOnAddThermostatSettingDialogClick;
import net.invictusmanagement.invictuslifestyle.models.AddBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.BrivoDeviceData;
import net.invictusmanagement.invictuslifestyle.models.Settings;
import net.invictusmanagement.invictuslifestyle.utils.AnimationUtils;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;
import net.invictusmanagement.invictuslifestyle.webservice.WebServiceBrivoSmarthHome;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import okhttp3.ResponseBody;


public class ThermostatSettingDialog extends DialogFragment implements View.OnClickListener {
    private SetOnAddThermostatSettingDialogClick listener;
    private AppCompatTextView tvCancel, tvSave, tvProgress;

    private boolean isFormatted = true;
    private boolean isUnFormatted = false;

    private CircularSeekBar circularSeekBar;
    private CircularSliderRange circularRangeSeekBar;
    private RadioGroup rgSelectMode, rgSelectFan;
    private RadioButton rbFanOff, rbFanOn, rbFanAuto, rbModeOff, rbModeHeat, rbModeCool, rbModeAuto;

    private int lastSelectedCool, lastSelectedHeat;
    private int lastSelectedMode;
    private int lastSelectedFan;
    private View viewDisable;

    public BrivoDeviceData brivoDeviceData;
    public String token;

    public ThermostatSettingDialog() {
    }

    @SuppressLint("ValidFragment")
    public ThermostatSettingDialog(SetOnAddThermostatSettingDialogClick listener) {
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
        mRootView = inflater.inflate(R.layout.dialog_set_thermostat, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);
        tvProgress = view.findViewById(R.id.tvProgress);
        circularSeekBar = view.findViewById(R.id.circularSeekBar);
        rgSelectFan = view.findViewById(R.id.rgSelectFan);
        rgSelectMode = view.findViewById(R.id.rgSelectMode);
        rbFanOff = view.findViewById(R.id.rbOffFan);
        rbFanOn = view.findViewById(R.id.rbOnFan);
        rbFanAuto = view.findViewById(R.id.rbAutoFan);
        rbModeOff = view.findViewById(R.id.rbOff);
        rbModeHeat = view.findViewById(R.id.rbHeat);
        rbModeCool = view.findViewById(R.id.rbCool);
        rbModeAuto = view.findViewById(R.id.rbAuto);


        viewDisable = view.findViewById(R.id.viewDisable);
        circularRangeSeekBar = view.findViewById(R.id.circularRangeSeekBar);


        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);

        setDatainUI();

//        set default heat progres
        circularSeekBar.setProgress(lastSelectedHeat);
        circularRangeSeekBar.setEndAngle((lastSelectedHeat * 360) / 100);
        circularRangeSeekBar.setStartAngle((lastSelectedCool * 360) / 100);
        tvProgress.setText("Heat : " + lastSelectedHeat + " Cool : " + lastSelectedCool);


        circularRangeSeekBar.setOnSliderRangeMovedListener(new CircularSliderRange.OnSliderRangeMovedListener() {
            @Override
            public void onStartSliderMoved(double pos) {
                lastSelectedCool = (int) Math.round((pos * 100) / 360);
                tvProgress.setText("Heat : " + lastSelectedHeat + " Cool : " + lastSelectedCool);
            }

            @Override
            public void onEndSliderMoved(double pos) {
                lastSelectedHeat = (int) Math.round((pos * 100) / 360);
                tvProgress.setText("Heat : " + lastSelectedHeat + " Cool : " + lastSelectedCool);

            }

            @Override
            public void onStartSliderEvent(ThumbEvent event) {

            }

            @Override
            public void onEndSliderEvent(ThumbEvent event) {

            }
        });

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@Nullable CircularSeekBar circularSeekBar, float v, boolean b) {
                if (lastSelectedMode == R.id.rbCool) {
                    tvProgress.setText("Cool : " + Math.round(v));
                    lastSelectedCool = Math.round(v);
                } else if (lastSelectedMode == R.id.rbHeat) {
                    tvProgress.setText("Heat : " + Math.round(v));
                    lastSelectedHeat = Math.round(v);
                }


            }

            @Override
            public void onStopTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onStartTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }
        });


        rgSelectMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                lastSelectedMode = checkedId;
                switch (checkedId) {
                    case R.id.rbOff:
                        viewDisable.setVisibility(View.VISIBLE);
                        circularRangeSeekBar.setVisibility(View.GONE);
                        circularSeekBar.setVisibility(View.VISIBLE);
                        tvProgress.setText("Off");
                        rbFanOff.setEnabled(true);
                        break;
                    case R.id.rbHeat:
                        viewDisable.setVisibility(View.GONE);
                        circularRangeSeekBar.setVisibility(View.GONE);
                        circularSeekBar.setVisibility(View.VISIBLE);
                        circularSeekBar.setProgress(lastSelectedHeat);
                        tvProgress.setText("Heat : " + lastSelectedHeat);
                        rbFanOff.setEnabled(false);
                        break;
                    case R.id.rbCool:
                        viewDisable.setVisibility(View.GONE);
                        circularRangeSeekBar.setVisibility(View.GONE);
                        circularSeekBar.setVisibility(View.VISIBLE);
                        circularSeekBar.setProgress(lastSelectedCool);
                        tvProgress.setText("Cool : " + lastSelectedCool);
                        rbFanOff.setEnabled(false);
                        break;
                    case R.id.rbAuto:
                        viewDisable.setVisibility(View.GONE);
                        circularRangeSeekBar.setVisibility(View.VISIBLE);
                        circularSeekBar.setVisibility(View.GONE);
                        rbFanOff.setEnabled(false);
                        circularRangeSeekBar.setStartAngle((lastSelectedCool * 360) / 100);
                        circularRangeSeekBar.setEndAngle((lastSelectedHeat * 360) / 100);
                        tvProgress.setText("Heat : " + lastSelectedHeat + " Cool : " + lastSelectedCool);
                        break;

                }
            }
        });

        rgSelectFan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                lastSelectedFan = checkedId;
                switch (checkedId) {
                    case R.id.rbOffFan:
                        break;
                    case R.id.rbOnFan:
                        break;
                    case R.id.rbAutoFan:
                        break;
                }
            }
        });

    }

    public void setDatainUI() {
        if (brivoDeviceData != null) {
            lastSelectedHeat = brivoDeviceData.getSettings().getLow();
            lastSelectedCool = brivoDeviceData.getSettings().getHigh();

            switch (brivoDeviceData.getSettings().getMode()) {
                case "auto":
                    rbModeAuto.setChecked(true);
                    lastSelectedMode = R.id.rbAuto;
                    break;

                case "heat":
                    rbModeHeat.setChecked(true);
                    lastSelectedMode = R.id.rbHeat;
                    break;

                case "cool":
                    rbModeCool.setChecked(true);
                    lastSelectedMode = R.id.rbCool;
                    break;

                case "off":
                    rbModeOff.setChecked(true);
                    lastSelectedMode = R.id.rbOff;
                    break;
            }

            switch (brivoDeviceData.getSettings().getFan()) {
                case "auto":
                    rbFanAuto.setChecked(true);
                    lastSelectedFan = R.id.rbAutoFan;
                    break;

                case "on":
                    rbFanOn.setChecked(true);
                    lastSelectedFan = R.id.rbOnFan;
                    break;


                case "off":
                    rbFanOff.setChecked(true);
                    lastSelectedFan = R.id.rbOffFan;
                    break;
            }

        }

    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                ThermostatSettingDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSave:
                callAPIForSaveThermostat();
                break;
            case R.id.tvCancel:
                dismissDialog();
                break;
            default:
                break;
        }
    }

    private void callAPIForSaveThermostat() {
        ProgressDialog.showProgress(getContext());
        Settings settings = new Settings();

        RadioButton fan = mRootView.findViewById(lastSelectedFan);
        RadioButton mode = mRootView.findViewById(lastSelectedMode);
        settings.setFan(fan.getText().toString().toLowerCase());
        settings.setMode(mode.getText().toString().toLowerCase());
        settings.setHigh(lastSelectedCool);
        settings.setLow(lastSelectedHeat);


        ProgressDialog.showProgress(requireContext());
        WebServiceBrivoSmarthHome.getInstance().setThermostatSetting(brivoDeviceData.getId(), token, settings, new RestCallBack<ResponseBody>() {
            @Override
            public void onResponse(ResponseBody response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    Toast.makeText(requireContext(), "Data saved successfully.", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                } else Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(getContext(), "Something went wrong, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setBrivoDeviceData(BrivoDeviceData data, String token) {
        this.brivoDeviceData = data;
        this.token = token;
    }
}