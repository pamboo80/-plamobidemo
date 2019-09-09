package in.bangbit.plamobidemo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SettingsFragment extends Fragment {

    public static final String BLOCKED_USERS_KEY = "blocked_users";
    public static final String MILES_KEY = "miles";
    public static final String GEO_NOTIFICATION_KEY = "geo_notification";

    private FlowLayout flowLayout;
    private View.OnClickListener removeFilterOnClick;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        //RadioButton (miles)
        final int[] buttonIds = new int[] {R.id.rb2miles,R.id.rb5miles,R.id.rb10miles,R.id.rb50miles};
        final int[] buttonsWeights = new int[] {2,5,10,50};
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        RadioButton.OnCheckedChangeListener onRadioMilesChanged = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    for(int i = 0; i<buttonIds.length; i++) {
                        if(buttonIds[i]==buttonView.getId()) {
                            preferences.edit().putInt(MILES_KEY,buttonsWeights[i]).apply();
                            continue;
                        }
                        ((RadioButton)rootView.findViewById(buttonIds[i])).setChecked(false);
                    }
                }
            }
        };
        ((RadioButton)rootView.findViewById(R.id.rb2miles)).setOnCheckedChangeListener(onRadioMilesChanged);
        ((RadioButton)rootView.findViewById(R.id.rb5miles)).setOnCheckedChangeListener(onRadioMilesChanged);
        ((RadioButton)rootView.findViewById(R.id.rb10miles)).setOnCheckedChangeListener(onRadioMilesChanged);
        ((RadioButton)rootView.findViewById(R.id.rb50miles)).setOnCheckedChangeListener(onRadioMilesChanged);
        for(int i = 0; i<buttonsWeights.length; i++) {
            if(buttonsWeights[i]==preferences.getInt(MILES_KEY,0)) {
                ((RadioButton)rootView.findViewById(buttonIds[i])).setChecked(true);
            }
        }
        //enable geoNotification
        boolean isChecked = preferences.getBoolean(GEO_NOTIFICATION_KEY,false);
        ((CheckBox)rootView.findViewById(R.id.cbGeoNotification)).setChecked(isChecked);
        rootView.findViewById(R.id.rb2miles).setEnabled(isChecked);
        rootView.findViewById(R.id.rb5miles).setEnabled(isChecked);
        rootView.findViewById(R.id.rb10miles).setEnabled(isChecked);
        rootView.findViewById(R.id.rb50miles).setEnabled(isChecked);
        ((CheckBox)rootView.findViewById(R.id.cbGeoNotification)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(GEO_NOTIFICATION_KEY, isChecked).apply();
                rootView.findViewById(R.id.rb2miles).setEnabled(isChecked);
                rootView.findViewById(R.id.rb5miles).setEnabled(isChecked);
                rootView.findViewById(R.id.rb10miles).setEnabled(isChecked);
                rootView.findViewById(R.id.rb50miles).setEnabled(isChecked);
            }
        });
        //Don't allow users contact me...
        CompoundButton.OnCheckedChangeListener onContactThroughCheckedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(String.valueOf(buttonView.getId()),isChecked).apply();
            }
        };
        final int[] checkboxesIds = new int[] {R.id.cbContactWithNumber,R.id.cbContactWithMail,R.id.cbContactWithChat};
        for(int boxId : checkboxesIds) {
            CheckBox checkBox = ((CheckBox) rootView.findViewById(boxId));
            checkBox.setChecked(
                    preferences.getBoolean(String.valueOf(boxId), false)
            );
            checkBox.setOnCheckedChangeListener(onContactThroughCheckedListener);
        }
        //BlockedUsersList
        /*RecyclerView rvBlockedUsers = (RecyclerView)rootView.findViewById(R.id.rvBlockedUsers);
        rvBlockedUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBlockedUsers.setAdapter(new GreyItemAdapter(getContext(),BLOCKED_USERS_KEY));*/
        flowLayout = (FlowLayout)rootView.findViewById(R.id.flowLayoutBlockedUsers);
        Set<String> blockedUsersSet = PreferenceManager.getDefaultSharedPreferences(getContext()).getStringSet(BLOCKED_USERS_KEY, new HashSet<String>());
        addFiltersToLayout(blockedUsersSet.toArray(new String[blockedUsersSet.size()]));
        return rootView;
    }

    private void addFiltersToLayout(final String[] blockedUsers) {
        if(removeFilterOnClick==null) {
            removeFilterOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String removedUser = ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString();
                    Set<String> blockedUsersSet = PreferenceManager.getDefaultSharedPreferences(getContext()).getStringSet(BLOCKED_USERS_KEY, new HashSet<String>());
                    blockedUsersSet.remove(removedUser);
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putStringSet(BLOCKED_USERS_KEY, blockedUsersSet).apply();
                    flowLayout.removeView(v);
                }
            };
        }
        for(String blockUser : blockedUsers) {
            LinearLayout item = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.item_grey,flowLayout,false);
            ((TextView)item.getChildAt(0)).setText(blockUser);
            item.setOnClickListener(removeFilterOnClick);
            flowLayout.addView(item);
        }
        Set<String> blockedUsersSet = new HashSet<>();
        Collections.addAll(blockedUsersSet, blockedUsers);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putStringSet(BLOCKED_USERS_KEY, blockedUsersSet).apply();
    }
}
