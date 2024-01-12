package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.models.ServiceKeyRepeatOptions;

import java.util.ArrayList;
import java.util.List;

public class SpinnerRepeatAdapter extends ArrayAdapter<ServiceKeyRepeatOptions> {
    private Context mContext;
    private ArrayList<ServiceKeyRepeatOptions> listState;
    private SpinnerRepeatAdapter spinnerRepeatAdapter;
    private boolean isFromView = false;

    public SpinnerRepeatAdapter(Context context, int resource, List<ServiceKeyRepeatOptions> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<ServiceKeyRepeatOptions>) objects;
        this.spinnerRepeatAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_checkbox_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView
                    .findViewById(R.id.txtRepeat);
            holder.mCheckBox = convertView
                    .findViewById(R.id.chkRepeat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getItem());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

//        if ((position == 0)) {
//            holder.mCheckBox.setVisibility(View.INVISIBLE);
//        } else {
//            holder.mCheckBox.setVisibility(View.VISIBLE);
//        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}