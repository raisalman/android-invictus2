package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.AmenitiesActivity;
import net.invictusmanagement.invictuslifestyle.activities.AmenitiesBookingActivity;
import net.invictusmanagement.invictuslifestyle.activities.ChooseSurveyActivity;
import net.invictusmanagement.invictuslifestyle.activities.EENVideoListActivity;
import net.invictusmanagement.invictuslifestyle.activities.FernishActivity;
import net.invictusmanagement.invictuslifestyle.activities.MaintenanceRequestActivity;
import net.invictusmanagement.invictuslifestyle.activities.PaymentHistoryActivity;
import net.invictusmanagement.invictuslifestyle.activities.RentalInsuranceActivity;
import net.invictusmanagement.invictuslifestyle.activities.RentalPaymentActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class RentalToolFragment extends Fragment implements IRefreshableFragment {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private LinearLayout glMaintReq, glRentalInsu, glFernish, glAmenities, glAmenitiesBooking,
            glSurvey, glPaymentHistory, glRentalPay, glPeek;

    public boolean isRWTRenterTools;
    private Context _context;
    private String role;
    private static RentalToolFragment instance;

    public RentalToolFragment() {
    }

    @SuppressWarnings("unused")
    public static RentalToolFragment newInstance() {
        if (instance != null)
            return instance;
        return new RentalToolFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rental_tool, container, false);
        instance = this;
        glMaintReq = view.findViewById(R.id.glMaintReq);
        glRentalInsu = view.findViewById(R.id.glRentalInsu);
        glFernish = view.findViewById(R.id.glFernish);
        glAmenities = view.findViewById(R.id.glAmenities);
        glAmenitiesBooking = view.findViewById(R.id.glAmenitiesBooking);
        glSurvey = view.findViewById(R.id.glSurvey);
        glRentalPay = view.findViewById(R.id.glRentalPay);
        glPaymentHistory = view.findViewById(R.id.glPaymentHistory);
        glPeek = view.findViewById(R.id.glPeek);

        if (HomeFragment.isSurveyAvailable) {
            glSurvey.setVisibility(View.VISIBLE);
        } else {
            glSurvey.setVisibility(View.GONE);
        }


        if (sharedPreferences.getBoolean("allowMaintenanceRequest", true)) {
            glMaintReq.setVisibility(View.VISIBLE);
        } else {
            glMaintReq.setVisibility(View.GONE);
        }

        if (sharedPreferences.getBoolean("enableRentPayment", true)) {
            glRentalPay.setVisibility(View.VISIBLE);
        } else {
            glRentalPay.setVisibility(View.GONE);
        }


        //allowInsuranceRequest
        if (sharedPreferences.getBoolean("allowInsuranceRequest", true)) {
            glRentalInsu.setVisibility(View.VISIBLE);
        } else {
            glRentalInsu.setVisibility(View.GONE);
        }

        //allowInsuranceRequest
        if (sharedPreferences.getBoolean("enableEENIntegration", false)
                || sharedPreferences.getBoolean("enableAVAIntegration", false)) {
            glPeek.setVisibility(View.VISIBLE);
        } else {
            glPeek.setVisibility(View.GONE);
        }

        role = sharedPreferences.getString("userRole", "");
        if (role.equals(getString(R.string.role_property_manager))
                || role.equals(getString(R.string.role_leasing_officer))) {

            glRentalInsu.setVisibility(View.GONE);
            glFernish.setVisibility(View.GONE);
        }

        if (sharedPreferences.getBoolean("allowAmenitiesBooking", true)) {
            if (role.equals(getString(R.string.role_property_manager))
                    || role.equals(getString(R.string.role_leasing_officer))) {
                glAmenitiesBooking.setVisibility(View.VISIBLE);
                glAmenities.setVisibility(View.GONE);
            } else {
                glAmenitiesBooking.setVisibility(View.GONE);
                glAmenities.setVisibility(View.VISIBLE);
            }
        } else {
            glAmenitiesBooking.setVisibility(View.GONE);
            glAmenities.setVisibility(View.GONE);
        }

        //Payment History moved to Pay rent option
        glPaymentHistory.setVisibility(View.GONE);

        if (role.equals(getString(R.string.role_vendor)) || role.equals(getString(R.string.role_facility))) {
            glPeek.setVisibility(View.GONE);
            glAmenities.setVisibility(View.GONE);
            glRentalInsu.setVisibility(View.GONE);
            glRentalPay.setVisibility(View.GONE);
            glFernish.setVisibility(View.GONE);
            glAmenitiesBooking.setVisibility(View.GONE);
            glSurvey.setVisibility(View.GONE);

            glMaintReq.setVisibility(View.VISIBLE);
        }

        glMaintReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, MaintenanceRequestActivity.class));
            }
        });
        glRentalInsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, RentalInsuranceActivity.class));
            }
        });
        glFernish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, FernishActivity.class));
            }
        });

        glAmenities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, AmenitiesActivity.class));
            }
        });

        glAmenitiesBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, AmenitiesBookingActivity.class));
            }
        });

        glSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, ChooseSurveyActivity.class));
            }
        });

        glRentalPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(_context, RentalPaymentActivity.class));
            }
        });

        glPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(_context, PaymentHistoryActivity.class));
            }
        });

        glPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_context, EENVideoListActivity.class));
            }
        });
        return view;
    }

    public void displayWalkThrough() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
        isRWTRenterTools = sharedPreferences.getBoolean("isRWTRenterTools", true);
        if (isRWTRenterTools) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (glMaintReq != null && glRentalInsu != null && glFernish != null && glAmenities != null)
                        walkThroughHighlight(glMaintReq, glRentalInsu, glFernish, glAmenities);
                }
            }, 500);

            sharedPreferences.edit().putBoolean("isRWTRenterTools", false).apply();
            isRWTRenterTools = sharedPreferences.getBoolean("isRWTRenterTools", true);
        }
    }

    private void walkThroughHighlight(View view1, View view2, View view3, View view4) {
        Lighter lighter = Lighter.with(TabbedActivity.tabbedActivity);
        if (view1.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(view1)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_rt_maintanance)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0,
                            30, 0))
                    .build());
        }

        if (view2.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(view2)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_rt_insurance)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0,
                            30, 0))
                    .build());
        }
        if (view3.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(view3)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_rt_furnish)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0,
                            30, 0))
                    .build());
        }

        if (view4.getVisibility() == View.VISIBLE) {
            lighter.addHighlight(new LighterParameter.Builder()
                    .setHighlightedView(view4)
                    .setLighterShape(new RectShape())
                    .setTipLayoutId(R.layout.layout_tab_amenities)
                    .setTipViewRelativeDirection(Direction.BOTTOM)
                    .setTipViewRelativeOffset(new MarginOffset(130, 0,
                            30, 0))
                    .build());
        }
        lighter.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void refresh() {

    }
}