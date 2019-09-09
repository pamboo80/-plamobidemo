package in.bangbit.plamobidemo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class ListMessagesFragment extends Fragment {

    CardView searchSettingsContainer;
    RecyclerView recyclerView;
    EditText edToolbarSearch;

    public ListMessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_messages, container, false);
        searchSettingsContainer = (CardView)rootView.findViewById(R.id.cvSearchSettings);
        edToolbarSearch = ((MainActivity)getActivity()).getToolbarEditText();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.rvListPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //-----inflating Search Settings ------//
        Switch filtersSwitch = (Switch)searchSettingsContainer.findViewById(R.id.switch_filters);
        filtersSwitch.setChecked(
                preferences.getBoolean(String.valueOf(R.id.switch_filters), false)
        );
        filtersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(String.valueOf(buttonView.getId()), isChecked).apply();
                updateList();
            }
        });
        RadioGroup rgMapFilters = (RadioGroup)searchSettingsContainer.findViewById(R.id.rgMapFilters);
        rgMapFilters.check(
                rgMapFilters.getChildAt(preferences.getInt(String.valueOf(rgMapFilters.getId()), 0)).getId()
        );
        rgMapFilters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(group.findViewById(checkedId)); //indexes are equal to constants from Message class.
                preferences.edit().putInt(String.valueOf(group.getId()), index).apply();
                updateList();
            }
        });
        searchSettingsContainer.findViewById(R.id.label_remove_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new FiltersFragment()).commit();
            }
        });
        //----------------------------------//
        updateList();
        return rootView;
    }

    private void updateList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<Message> messageList = new ArrayList<>();
        if(MainSingleton.messages!=null) {
            for(Message message : MainSingleton.messages) {
                boolean equalsMessageType = preferences.getInt(String.valueOf(R.id.rgMapFilters),0)==Message.MESSAGE_TYPE_GENERAL
                        || preferences.getInt(String.valueOf(R.id.rgMapFilters),0)==message.getMessageType();
                if(!(equalsMessageType && MapFragment.equalFilters(getContext(), message.getText()))) {
                    continue;
                }
                messageList.add(message);
            }
            recyclerView.setAdapter(new MessagesAdapter(getContext(),messageList));
        }
    }

    private void searchByText(String text) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.btnSearchSettings) {
            searchSettingsContainer.setVisibility(
                    searchSettingsContainer.getVisibility() == View.VISIBLE ?
                            View.GONE :
                            View.VISIBLE
            );
        } else if(item.getItemId()==R.id.btnSearch) {
            edToolbarSearch.setVisibility(
                    edToolbarSearch.getVisibility()==View.VISIBLE ?
                            View.GONE :
                            View.VISIBLE
            );
        }
        return super.onOptionsItemSelected(item);
    }

}
