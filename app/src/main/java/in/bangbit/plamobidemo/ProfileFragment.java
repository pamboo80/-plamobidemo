package in.bangbit.plamobidemo;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    private static final int GALLERY_REQUEST = 0;
    public static final String AVATAR_KEY = "avatar";
    private ImageView imgPhoto;
    TextView tvPhone;
    TextView tvMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        imgPhoto = (ImageView)rootView.findViewById(R.id.imProfilePhoto);
        tvPhone = (TextView)rootView.findViewById(R.id.tvPhone);
        tvPhone.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("phone","050 443 68 67"));
        tvMail = (TextView)rootView.findViewById(R.id.tvMail);
        tvMail.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("mail","goodparrot@gmail.com"));
        String imgUri = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(AVATAR_KEY, "android.resource://".concat(getContext().getPackageName()).concat("/".concat(String.valueOf(R.drawable.demo_man_face))));
        Picasso.with(getContext())
                .load(Uri.parse(imgUri))
                .transform(new CircleTransform())
                .into(imgPhoto);
        rootView.findViewById(R.id.btnProfileEditPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), GALLERY_REQUEST);
            }
        });
        ((EditText)rootView.findViewById(R.id.edPhone)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6/*enter button*/) {
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("phone", v.getText().toString()).apply();
                    tvPhone.setText(v.getText());
                    tvPhone.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                }
                return false;
            }
        });
        ((EditText)rootView.findViewById(R.id.edMail)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6/*enter button*/) {
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("mail",v.getText().toString()).apply();
                    tvMail.setText(v.getText());
                    tvMail.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                }
                return false;
            }
        });
        rootView.findViewById(R.id.imProfilePhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rootView.findViewById(R.id.edPhone).getVisibility()==View.VISIBLE) {
                    return;
                }
                tvPhone.setVisibility(View.GONE);
                rootView.findViewById(R.id.edPhone).setVisibility(View.VISIBLE);
            }
        });
        rootView.findViewById(R.id.imProfileMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rootView.findViewById(R.id.edMail).getVisibility()==View.VISIBLE) {
                    return;
                }
                tvMail.setVisibility(View.GONE);
                rootView.findViewById(R.id.edMail).setVisibility(View.VISIBLE);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode== Activity.RESULT_OK) {
            Picasso.with(getContext()).load(data.getData()).transform(new CircleTransform()).into(imgPhoto);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(AVATAR_KEY, data.getDataString()).apply();
        }
    }
}
