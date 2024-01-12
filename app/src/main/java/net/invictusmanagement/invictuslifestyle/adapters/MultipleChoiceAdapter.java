package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.MultipleChoiceInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Survey;
import net.invictusmanagement.invictuslifestyle.models.SurveyAnswer;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceAdapter extends RecyclerView.Adapter<MultipleChoiceAdapter.ViewHolder> {

    private final List<SurveyAnswer> _dataSource = new ArrayList<>();
    private final MultipleChoiceInteractionListener _listener;
    public Survey survey = new Survey();
    private long id = 0;
    private Context _context;

    public MultipleChoiceAdapter(Context context, MultipleChoiceInteractionListener _listener, Survey survey) {
        _context = context;
        this._listener = _listener;
        this.survey = survey;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_multiple_choice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.checkBox.setText(_dataSource.get(position).answer);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String selectedAns = holder.checkBox.getText().toString();
                int selectedAnsId = 0;
                for (int i = 0; i < _dataSource.size(); i++) {
                    if (selectedAns.equals(_dataSource.get(i).answer)) {
                        selectedAnsId = _dataSource.get(i).id;
                        break;
                    }
                }
                if (isChecked) {
                    survey.checkboxCount++;
                    survey.multiChoiceAns.add(selectedAns);
                    survey.multiChoiceAnsId.add(String.valueOf(selectedAnsId));
                } else {
                    survey.checkboxCount--;
                    survey.multiChoiceAns.remove(selectedAns);
                    survey.multiChoiceAnsId.remove(String.valueOf(selectedAnsId));
                }
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
        return this.survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = new Survey();
        this.survey = survey;
    }

    public void refresh(int i, List<Survey> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list.get(i).answerList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }

    }
}
