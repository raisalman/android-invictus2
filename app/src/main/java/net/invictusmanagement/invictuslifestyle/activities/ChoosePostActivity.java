package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

public class ChoosePostActivity extends AppCompatActivity {


    private TextView tvSell, tvService;
    private LinearLayout llSell, llService, llInsideSell, llInsideService;
    private ImageView imgSell, imgService;
    private ConstraintLayout clMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_post);

        setToolbar();
        initViews();
        onClickListeners();
    }

    private void onClickListeners() {

        llSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchVisibilityForSell(true);
                switchVisibilityForService(false);
                startActivityForResult(new Intent(ChoosePostActivity.this, SellActivity.class), 1);
            }
        });

        llService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchVisibilityForSell(false);
                switchVisibilityForService(true);
                startActivityForResult(new Intent(ChoosePostActivity.this, ServiceActivity.class), 2);
            }
        });

    }


    private void switchVisibilityForSell(Boolean isEnable) {

        if (isEnable) {
            llSell.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_choose_selected));
            llInsideSell.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_half_circle_selected));
            imgSell.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_sell_select));
            tvSell.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            llSell.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_choose_unselected));
            llInsideSell.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_half_circle_unselected));
            imgSell.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_sell_deselect));
            tvSell.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
        }

    }

    private void switchVisibilityForService(Boolean isEnable) {

        if (isEnable) {
            llService.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_choose_selected));
            llInsideService.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_half_circle_selected));
            imgService.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_service_select));
            tvService.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            llService.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_choose_unselected));
            llInsideService.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.layout_bg_half_circle_unselected));
            imgService.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_service_deselect));
            tvService.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
        }

    }

    private void initViews() {
        tvSell = findViewById(R.id.tvSell);
        clMain = findViewById(R.id.clMain);
        tvService = findViewById(R.id.tvService);
        llSell = findViewById(R.id.llSell);
        llService = findViewById(R.id.llService);
        llInsideSell = findViewById(R.id.llInsideSell);
        llInsideService = findViewById(R.id.llInsideService);
        imgSell = findViewById(R.id.imgSell);
        imgService = findViewById(R.id.imgService);
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(ChoosePostActivity.this);
            }
        });

        setTitle("Choose");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            finish();
        } else if (resultCode == 2) {
            finish();
        }
    }
}