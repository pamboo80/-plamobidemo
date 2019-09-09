package in.bangbit.plamobidemo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public MapFragment() {
        // Required empty public constructor
    }

    private CardView searchSettingsContainer;
    private CardView messageContainer;
    private MapView mapView;

    private GoogleMap googleMap;
    private HashMap<Marker, Message> markerMessageMap;

    public static MapFragment newInstanceWithPopup() {

        Bundle args = new Bundle();
        args.putBoolean("popup",true);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        searchSettingsContainer = (CardView) rootView.findViewById(R.id.cvSearchSettings);
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
                onMapReady(googleMap);
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
                onMapReady(googleMap);
            }
        });
        searchSettingsContainer.findViewById(R.id.label_remove_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new FiltersFragment()).commit();
            }
        });
        //----------------------------------//
        messageContainer = (CardView) rootView.findViewById(R.id.cvMarkerMessage);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if(getArguments()!=null) {
            Picasso.with(getContext())
                    .load(R.mipmap.ic_launcher)
                    .transform(new CircleTransform())
                    .into((ImageView) messageContainer.findViewById(R.id.imMessagePhoto));
            ((TextView) messageContainer.findViewById(R.id.tvMessageUsername)).setText("user123");
            try {
                ((TextView) messageContainer.findViewById(R.id.tvMessageDistance)).setText(String.format(getString(R.string.n_miles_away), 4f));
            } catch (NullPointerException e) {}
            //--------------------
            ((TextView) messageContainer.findViewById(R.id.tvMessageText)).setText("I want a cab to the airport");
            ((TextView) messageContainer.findViewById(R.id.tvMessageTime))
                    .setText(String.format(getString(R.string.posted_on_n_days_ago),
                            0));
            messageContainer.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if(this.googleMap==null) {
            this.googleMap = googleMap;
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                messageContainer.setVisibility(View.GONE);
                searchSettingsContainer.setVisibility(View.GONE);
            }
        });
        googleMap.setOnMarkerClickListener(this);
        if (MainSingleton.messages == null) return;
        googleMap.clear();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        markerMessageMap = new HashMap<>();
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        for (Message message : MainSingleton.messages) {
            //Search Settings
            boolean equalsMessageType = preferences.getInt(String.valueOf(R.id.rgMapFilters),0)==Message.MESSAGE_TYPE_GENERAL
                    || preferences.getInt(String.valueOf(R.id.rgMapFilters),0)==message.getMessageType();
            if(!(equalsMessageType && equalFilters(getContext(),message.getText()))) {
                continue;
            }
            //-----------------------------------------------//
            latLngBoundsBuilder.include(message.getLatLng());
            int markerIconId = R.drawable.ic_marker_general;
            if (message.getMessageType()==Message.MESSAGE_TYPE_SELL) {
                markerIconId = R.drawable.ic_marker_sell;
            } else if (message.getMessageType()==Message.MESSAGE_TYPE_SEEK) {
                markerIconId = R.drawable.ic_marker_seek;
            }
            markerMessageMap.put(
                    googleMap.addMarker(new MarkerOptions()
                            .position(message.getLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(markerIconId))),
                    message
            );
        }
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), (int) MainSingleton.convertDpToPixel(32)));
        } catch (IllegalStateException e) {
            //No points included!
            Toast.makeText(getContext(),R.string.no_points_for_filters,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        Message message = markerMessageMap.get(marker);
        Picasso.with(getContext())
                .load(message.getImgUrl())
                .transform(new CircleTransform())
                .into((ImageView) messageContainer.findViewById(R.id.imMessagePhoto));
        ((TextView) messageContainer.findViewById(R.id.tvMessageUsername)).setText(message.getUsername());
        try {
            ((TextView) messageContainer.findViewById(R.id.tvMessageDistance)).setText(String.format(getString(R.string.n_miles_away),
                    MainSingleton.calculateDistanceFromMeInMiles(message.getLatLng())));
        } catch (NullPointerException e) {}
        //--------------------
        ((TextView) messageContainer.findViewById(R.id.tvMessageText)).setText(message.getText());
        ((TextView) messageContainer.findViewById(R.id.tvMessageTime))
                .setText(String.format(getString(R.string.posted_on_n_days_ago),
                        MainSingleton.getDaysAgoFromNow(message.getMessageTimeStamp())));
        messageContainer.setVisibility(View.VISIBLE);
        return false;
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
            EditText edToolbarSearch = ((MainActivity)getActivity()).getToolbarEditText();
            edToolbarSearch.setVisibility(
                    edToolbarSearch.getVisibility()==View.VISIBLE ?
                            View.GONE :
                            View.VISIBLE
            );
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean equalFilters(Context context, String description) {
        if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(String.valueOf(R.id.switch_filters), false)) {
            return true;
        }
        Set<String> filterSet = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(FiltersFragment.FILTERS_KEY, new HashSet<String>());
        for(String filter : filterSet) {
            if(description.contains(filter)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
