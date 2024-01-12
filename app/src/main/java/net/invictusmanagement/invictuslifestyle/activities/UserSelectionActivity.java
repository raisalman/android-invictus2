package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.UserSelectionAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionActivity extends AppCompatActivity implements IRefreshableFragment {

    private Context _context;
    private RecyclerView rvCategory;
    private TextView tvNoDataFound;
    private SearchView searchView;
    private Button btnDone;
    private UserSelectionAdapter adapter;
    private ArrayList<ResidentChat> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_multi_selection);

        initControls();
    }

    private void initControls() {
        _context = UserSelectionActivity.this;
        toolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        rvCategory = findViewById(R.id.rvCategory);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        searchView = findViewById(R.id.searchView);
        btnDone = findViewById(R.id.btnDone);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                filter(newText);
                return false;
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ids = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                if (adapter != null) {
                    List<ResidentChat> list = adapter.getList();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isSelected) {
                            ids.add(String.valueOf(list.get(i).id));
                            names.add(list.get(i).displayName);
                        }
                    }
                }

                String[] selectedIds = ids.toArray(new String[0]);
                String[] selectedNames = names.toArray(new String[0]);


                Intent intent = new Intent();
                intent.putExtra("NAME", selectedNames);
                intent.putExtra("ID", selectedIds);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<ResidentChat> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (ResidentChat item : arrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.displayName.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Select Recipients");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void refresh() {
        ProgressDialog.showProgress(_context);
        WebService.getInstance().getNotificationUsers(
                new RestCallBack<List<ResidentChat>>() {
                    @Override
                    public void onResponse(List<ResidentChat> response) {
                        ProgressDialog.dismissProgress();
                        setPaymentData(response);
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        ProgressDialog.dismissProgress();
                    }
                });
    }

    private void setPaymentData(List<ResidentChat> paymentData) {
        arrayList = (ArrayList<ResidentChat>) paymentData;
        adapter = new UserSelectionAdapter(_context, paymentData);

        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.addItemDecoration(new DividerItemDecoration(rvCategory.getContext(),
                DividerItemDecoration.VERTICAL));
        rvCategory.setAdapter(adapter);

        if (paymentData.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvCategory.setVisibility(View.VISIBLE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
            rvCategory.setVisibility(View.GONE);
        }
    }

    public void onClick(ResidentChat item) {
        Intent intent = new Intent();
        intent.putExtra("NAME", item.displayName);
        intent.putExtra("ID", String.valueOf(item.id));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}