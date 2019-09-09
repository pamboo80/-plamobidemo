package in.bangbit.plamobidemo;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyPostFragment extends Fragment {


    public MyPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_my_post, container, false);

        final Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };
        final Pattern hashtagPattern = Pattern.compile("#([ء-يA-Za-z0-9_-]+)");

        ((EditText) rootView.findViewById(R.id.edPostText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) rootView.findViewById(R.id.tvSymbolsQuantity)).setText(
                        String.format(getString(R.string.characters_quantity_template), start + count, getContext().getResources().getInteger(R.integer.post_max_characters)));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Linkify.addLinks(s, hashtagPattern, "content://com.hashtag.jojo/", null, filter);
                Linkify.addLinks(s, Patterns.WEB_URL, null, null, filter);
            }
        });

        rootView.findViewById(R.id.container_btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), R.string.you_must_give_geo_permission, Toast.LENGTH_LONG).show();
                    return;
                }
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(MainSingleton.getApiClient());
                LatLng location = lastLocation==null ? new LatLng(0,0) : new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                try {
                    int chatAllowed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(String.valueOf(R.id.cbContactWithChat), false) ? 1 : 0;
                    //getting messageType
                    String messageType;
                    boolean saveAsSell = ((CheckBox) rootView.findViewById(R.id.cbSaveAsSell)).isChecked();
                    boolean saveAsSeek = ((CheckBox) rootView.findViewById(R.id.cbSaveAsSeek)).isChecked();
                    if (saveAsSell == saveAsSeek) {
                        messageType = "General";
                    } else {
                        messageType = saveAsSell ? "Sell" : "Seek";
                    }
                    //get index of checked button in Radiogroup (0 or 1)
                    RadioGroup rgTagThisMapOn = (RadioGroup)rootView.findViewById(R.id.rgTagThisMapOn);
                    RadioButton checkedButton = (RadioButton)rootView.findViewById(rgTagThisMapOn.getCheckedRadioButtonId());

                    JSONObject newUser = new JSONObject();
                    newUser.put("username", "Tom Parrot");
                    newUser.put("latitude", location.latitude);
                    newUser.put("longitude", location.longitude);
                    newUser.put("phone", "050 443 68 67");
                    newUser.put("email", "goodparrot@gmail.com");
                    newUser.put("chat", chatAllowed);
                    JSONObject postMessage = new JSONObject();
                    postMessage.put("message", ((EditText) rootView.findViewById(R.id.edPostText)).getText().toString());
                    postMessage.put("messageType", messageType);
                    postMessage.put("messageLocation", rgTagThisMapOn.indexOfChild(checkedButton));
                    newUser.put("postMessage", postMessage);
                    MainSingleton.volleyQueue.add(new JsonObjectRequest(
                            Request.Method.POST,
                            "http://api.plamobi.com:3000/users/adduser",
                            newUser,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getContext(),"request OK",Toast.LENGTH_LONG).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(),"request ERROR",Toast.LENGTH_LONG).show();
                                }
                            }
                    ));
                } catch (JSONException e) {
                }
                
            }
        });
        return rootView;
    }
}
