package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.ChooseSurveyItem;
import net.invictusmanagement.invictuslifestyle.models.SurveyList;

import java.util.ArrayList;
import java.util.List;

public class ChooseServeyAdapter extends RecyclerView.Adapter<ChooseServeyAdapter.ViewHolder> {

    private final List<SurveyList> _dataSource = new ArrayList<>();
    private final ChooseSurveyItem _listener;
    private Context context;

    public ChooseServeyAdapter(Context context, ChooseSurveyItem _listener) {
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_survey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        holder.tvSurveyName.setText(holder.item.name);

        holder.glMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.selectedSurvey(holder.getAdapterPosition(), holder.item);
            }
        });


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<SurveyList> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        private final TextView tvSurveyName;
        public SurveyList item;
        public GridLayout glMain;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvSurveyName = view.findViewById(R.id.tvSurveyName);
            glMain = view.findViewById(R.id.glMain);
        }
    }

}
