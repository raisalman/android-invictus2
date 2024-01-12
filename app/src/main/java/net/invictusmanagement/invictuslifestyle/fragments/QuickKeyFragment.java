package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.QuickDigitalKeyResponse;
import net.invictusmanagement.invictuslifestyle.models.Service;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuickKeyFragment extends Fragment {

    private Service serviceTypes = new Service();
    private Spinner spnServiceType;
    private QuickDigitalKeyResponse quickDigitalKeyResponse;
    private Button _buttonQuickKey;
    private LinearLayout llLastKey;
    private TextView tvCreatedDate, tvKey, tvCopyText;
    private Button btnCopy;
    private TextView tvQuickKeyMessage;
    private ProgressBar _progressBar;
    private SharedPreferences sharedPreferences;


    public QuickKeyFragment() {
    }

    @SuppressWarnings("unused")
    public static QuickKeyFragment newInstance() {
        return new QuickKeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quick_key, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        _buttonQuickKey = view.findViewById(R.id.buttonQuickKey);

        tvQuickKeyMessage = view.findViewById(R.id.tvQuickKeyMessage);
        _progressBar = view.findViewById(R.id.progress);
        spnServiceType = view.findViewById(R.id.spnServiceType);

        llLastKey = view.findViewById(R.id.llLastKey);
        tvCreatedDate = view.findViewById(R.id.tvCreateDate);
        tvKey = view.findViewById(R.id.tvKey);
        tvCopyText = view.findViewById(R.id.tvCopyText);
        btnCopy = view.findViewById(R.id.btnCopy);

        clickEvent();
        setServiceTypes();

        return view;
    }

    private void showAlertDialog(int i) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Deliver TO:");
        String[] items = {"Unit", "Package Center/Office"};
        int checkedItem = 0;
        final int[] selectedItem = {0};
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectedItem[0] = 0;
                        break;
                    case 1:
                        selectedItem[0] = 1;
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewQuickKey(selectedItem[0]);

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void clickEvent() {
        _buttonQuickKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(_buttonQuickKey);
                showAlertDialog(0);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard =
                        (ClipboardManager) getContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                String keyText = tvKey.getText().toString() + "\n" + tvCopyText.getText().toString();
                ClipData clip = ClipData.newPlainText("Key", keyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),
                        "Text copied. Enter this quick key in my DIRECTORY PROFILE on the kiosk.",
                        Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewQuickKey(int deliverTo) {
        DigitalKey key = new DigitalKey();
        key.setQuickKey(true);

        for (int i = 0; i < serviceTypes.serviceTypes.size(); i++) {
            if (spnServiceType.getSelectedItem().toString().equals(serviceTypes.serviceTypes.get(i).name)) {
                key.setServiceTypeId(serviceTypes.serviceTypes.get(i).id);
                break;
            }
        }
        key.setToPackageCenter(deliverTo != 0);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(getContext());
            }

            @Override
            protected String doInBackground(Void... args) {
                try {
                    return MobileDataProvider.getInstance().createQuickDigitalKey(key);
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String success) {
                ProgressDialog.dismissProgress();
                if (success != null) {
                    quickDigitalKeyResponse = new GsonBuilder().registerTypeAdapter(Date.class,
                                    new MobileDataProvider.DateDeserializer()).create()
                            .fromJson(success, new TypeToken<QuickDigitalKeyResponse>() {
                            }.getType());

//                    dialogForQuickKey();
                    showLastCreatedView();
                } else {
                    Toast.makeText(getContext(), "Error while creating quick key", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void showLastCreatedView() {
        llLastKey.setVisibility(View.VISIBLE);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        tvCreatedDate.setText("Created On: " + formatter.format(quickDigitalKeyResponse.createdUtc));
        tvKey.setText("Delivery Quick Key is " + quickDigitalKeyResponse.key);
        tvCopyText.setText("Find " + HomeFragment.userName + " in the DIRECTORY " +
                "of the kiosk and enter this key. Key expires in 90 mins.");

    }


    private void dialogForQuickKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quick_key, null);
        builder.setView(view)
                .setTitle(quickDigitalKeyResponse.recipient);

        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        ((TextView) view.findViewById(R.id.createdUtc)).setText(formatter.format(quickDigitalKeyResponse.createdUtc));
        ((TextView) view.findViewById(R.id.tvQuickKey)).setText(quickDigitalKeyResponse.key);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });


        builder.setNegativeButton("COPY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ClipboardManager clipboard =
                        (ClipboardManager) getContext()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                long diff = quickDigitalKeyResponse.toUtc.getTime()
                        - quickDigitalKeyResponse.fromUtc.getTime();
                int hours = (int) (diff / (1000 * 60 * 60));
                String keyText = HomeFragment.userName + " sent you this quick key:\n"
                        + quickDigitalKeyResponse.key + " \n" +
                        "Find " + HomeFragment.userName + " using the directory button of the" +
                        " kiosk to enter this key. \n"
                        + "Key expires in " + hours + " hours ("
                        + convertTime(quickDigitalKeyResponse.toUtc) + ")";
                ClipData clip = ClipData.newPlainText("Key", keyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),
                        "Enter this quick key in my DIRECTORY PROFILE on the kiosk.",
                        Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
        builder.setCancelable(false).create().show();
    }

    private String convertTime(Date datePasssed) {
        /*"10:30 PM"*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm a");
        return displayFormat.format(datePasssed);
    }

    private String convertDate(Date datePasssed) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        return displayFormat.format(datePasssed);
    }

    private void setServiceTypes() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(getContext());
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    serviceTypes = MobileDataProvider.getInstance().getDigitalServiceType();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                if (serviceTypes.serviceTypes != null && serviceTypes.keyDurationMessage != null) {
//                    tvQuickKeyMessage.setText(serviceTypes.keyDurationMessage);
                    ArrayList<String> catString = new ArrayList<>();
                    for (int i = 0; i < serviceTypes.serviceTypes.size(); i++) {
                        catString.add(serviceTypes.serviceTypes.get(i).name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, catString);
                    spnServiceType.setAdapter(adapter);
                    spnServiceType.setSelection(0);
                    _buttonQuickKey.setEnabled(true);
                }


                if (!success) {
                    Toast.makeText(getContext(), "Unable to get service types. Please try again later.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }

            }
        }.execute();
    }
}
