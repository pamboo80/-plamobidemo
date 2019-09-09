package in.bangbit.plamobidemo;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FiltersFragment extends Fragment {


    public FiltersFragment() {
        // Required empty public constructor
    }

    public static final String FILTERS_KEY = "filters";
    private FlowLayout flowLayout;
    private View.OnClickListener removeFilterOnClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_filters, container, false);
        //final GreyItemAdapter adapter = new GreyItemAdapter(getContext(),FILTERS_KEY);
        /*final RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.rvFilters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);*/
        flowLayout = (FlowLayout)rootView.findViewById(R.id.flowLayoutFilters);
        final EditText edFilters = (EditText)rootView.findViewById(R.id.edFilters);
        Set<String> filtersSet = PreferenceManager.getDefaultSharedPreferences(getContext()).getStringSet(FILTERS_KEY, new HashSet<String>());
        addFiltersToLayout(filtersSet.toArray(new String[filtersSet.size()]));
        rootView.findViewById(R.id.btnAddFilters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFiltersToLayout(edFilters.getText().toString().split(","));
                //adapter.addItems(edFilters.getText().toString().split(","));
                edFilters.setText("");
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)) //hideKeyboard
                        .hideSoftInputFromWindow(edFilters.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });
        return rootView;
    }

    private void addFiltersToLayout(final String[] filters) {
        if(removeFilterOnClick==null) {
            removeFilterOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String removedFilter = ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString();
                    Set<String> filtersSet = PreferenceManager.getDefaultSharedPreferences(getContext()).getStringSet(FILTERS_KEY, new HashSet<String>());
                    filtersSet.remove(removedFilter);
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putStringSet(FILTERS_KEY, filtersSet).apply();
                    flowLayout.removeView(v);
                }
            };
        }
        for(String filter : filters) {
            LinearLayout item = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.item_grey,flowLayout,false);
            ((TextView)item.getChildAt(0)).setText(filter);
            item.setOnClickListener(removeFilterOnClick);
            flowLayout.addView(item);
        }
        Set<String> filtersSet = new HashSet<>();
        Collections.addAll(filtersSet, filters);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putStringSet(FILTERS_KEY, filtersSet).apply();
    }

}
