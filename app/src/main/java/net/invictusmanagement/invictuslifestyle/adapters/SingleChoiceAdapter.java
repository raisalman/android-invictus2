package net.invictusmanagement.invictuslifestyle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.SurvayActivity;
import net.invictusmanagement.invictuslifestyle.models.Survey;
import net.invictusmanagement.invictuslifestyle.models.SurveyAnswer;

import java.util.ArrayList;
import java.util.List;

public class SingleChoiceAdapter extends RecyclerView.Adapter<SingleChoiceAdapter.ViewHolder> {


    private final List<SurveyAnswer> _dataSource = new ArrayList<>();

    private Survey survey = new Survey();
    private long id = 0;
    private int lastCheckedPos = 0;
    private SurvayActivity _context;
    private AppCompatRadioButton lastChecked = null;
    private int lastCheckedPosition = -1;

    public SingleChoiceAdapter(SurvayActivity context) {
        _context = context;
    }

    @Override
    public SingleChoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_single_choice, parent, false);
        return new SingleChoiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SingleChoiceAdapter.ViewHolder holder, int position) {


        holder.radiobutton.setText(_dataSource.get(position).answer);
        /*holder.radiobutton.setChecked(position == lastCheckedPosition);*/
        holder.radiobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedAnsId = 0;
                for (int i = 0; i < _dataSource.size(); i++) {
                    if (holder.radiobutton.getText().toString().equals(_dataSource.get(i).answer)) {
                        selectedAnsId = _dataSource.get(i).id;
                        break;
                    }
                }

                _context.setSingleChoiceAns(holder.radiobutton, String.valueOf(selectedAnsId));

                if (holder.radiobutton.isChecked()) {
                    if (id == survey.id) {
                        if (lastChecked != null) {
                            if (lastChecked != holder.radiobutton)
                                lastChecked.setChecked(false);
                        }
                    }

                    lastChecked = holder.radiobutton;
                    lastCheckedPos = position;
                } else
                    lastChecked = null;


            }
        });

    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public void setId(long id1) {
        id = id1;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey1) {
        survey = survey1;
    }

    public void refresh(int i, List<Survey> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list.get(i).answerList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final AppCompatRadioButton radiobutton;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            radiobutton = (AppCompatRadioButton) view.findViewById(R.id.radiobutton);

            /*radiobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _context.setSingleChoiceAns(radiobutton);
                    int copyOfLastCheckedPosition = lastCheckedPosition;
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemChanged(copyOfLastCheckedPosition);
                    notifyItemChanged(lastCheckedPosition);

                }
            });*/
        }

    }
}
