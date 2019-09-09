package in.bangbit.plamobidemo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {

    private EditText edToolbarSearch;
    private Drawer navigationDrawer;

    public static MainActivity activity; //dirty hack.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        activity = this;
        //Creating menu
        String imgUri = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(ProfileFragment.AVATAR_KEY, "android.resource://".concat(getPackageName()).concat("/".concat(String.valueOf(R.drawable.demo_man_face))));
        AccountHeader profileHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withOnlyMainProfileImageVisible(true)
                .withHeaderBackground(R.drawable.background_account)
                .addProfiles(
                        new ProfileDrawerItem().withName(getString(R.string.demo_username)).withEmail(getString(R.string.demo_email)).withIcon(Uri.parse(imgUri))
                ).build();
        BadgeStyle badgeStyle = new BadgeStyle().withTextColor(Color.WHITE).withBadgeBackground(getResources().getDrawable(R.drawable.background_badge));
        navigationDrawer = new DrawerBuilder(this)
                .withToolbar((Toolbar)findViewById(R.id.toolbar))
                .withAccountHeader(profileHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.my_profile).withIdentifier(R.string.my_profile).withIcon(R.drawable.ic_profile),
                        new PrimaryDrawerItem().withName(R.string.notifications).withIdentifier(R.string.notifications).withIcon(R.drawable.ic_notifications).withBadge("12").withBadgeStyle(badgeStyle),
                        new PrimaryDrawerItem().withName(R.string.chat).withIdentifier(R.string.chat).withIcon(R.drawable.ic_chat2).withBadge("7").withBadgeStyle(badgeStyle),
                        new PrimaryDrawerItem().withName(R.string.filters).withIdentifier(R.string.filters).withIcon(R.drawable.ic_filters),
                        new PrimaryDrawerItem().withName(R.string.settings).withIdentifier(R.string.settings).withIcon(R.drawable.ic_settings)
                ).withOnDrawerItemClickListener(this).build();
        //Binding events to bottom menu
        findViewById(R.id.menuMap).setOnClickListener(onBottomMenuItemClickListener);
        findViewById(R.id.menuList).setOnClickListener(onBottomMenuItemClickListener);
        findViewById(R.id.menuNotifications).setOnClickListener(onBottomMenuItemClickListener);
        findViewById(R.id.menuMessages).setOnClickListener(onBottomMenuItemClickListener);
        //Center buttons with 2 floating buttons
        final View btnDeletePost = findViewById(R.id.btnDeletePost);
        final View btnAddPost = findViewById(R.id.btnAddPost);
        final View btnPlus = findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getRotation() == 0) {
                    v.animate().rotation(225);
                    float distance = MainSingleton.convertDpToPixel(50);
                    btnDeletePost.setVisibility(View.VISIBLE);
                    btnDeletePost.animate().translationX(-distance).translationY(-distance);
                    btnAddPost.setVisibility(View.VISIBLE);
                    btnAddPost.animate().translationX(distance).translationY(-distance);
                } else {
                    v.animate().rotation(0);
                    long duration = btnDeletePost.animate().translationX(0).translationY(0).getDuration();
                    btnAddPost.animate().translationX(0).translationY(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnDeletePost.setVisibility(View.GONE);
                            btnAddPost.setVisibility(View.GONE);
                        }
                    }, duration);
                }
            }
        });
        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edToolbarSearch.setVisibility(View.GONE); //hide searchEditText
                navigationDrawer.setSelection(-1, false); //remove selection from NavigationDrawer
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MyPostFragment())
                        .commit();
                if(getSupportActionBar()!=null) {
                    getSupportActionBar().setTitle(R.string.my_post);
                }
                btnPlus.performClick();
            }
        });
        //Filter markers by word in toolbar
        edToolbarSearch = (EditText)findViewById(R.id.edToolbarSearch);
        edToolbarSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER) {
                    String filterText = ((EditText)v).getText().toString();
                    Toast.makeText(getApplicationContext(), filterText, Toast.LENGTH_SHORT).show();
                    v.setVisibility(View.GONE);
                }
                return false;
            }
        });
        //Open map
        onBottomMenuItemClickListener.onClick(findViewById(R.id.menuMap));
    }

    //NavigationDrawer Menu clicks
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Fragment newFragment = null;
        switch (drawerItem.getIdentifier()) {
            case R.string.my_profile:
                newFragment = new ProfileFragment();
                break;
            case R.string.notifications:
                newFragment = new NotificationsFragment();
                break;
            case R.string.chat:
                newFragment = new ChatSummaryFragment();
                break;
            case R.string.filters:
                newFragment = new FiltersFragment();
                break;
            case R.string.settings:
                newFragment = new SettingsFragment();
                break;
        }
        if(newFragment!=null && getSupportActionBar()!=null) {
            edToolbarSearch.setVisibility(View.GONE); //hide searchEditText
            getSupportFragmentManager().beginTransaction().replace(R.id.container,newFragment).commit();
            getSupportActionBar().setTitle(drawerItem.getIdentifier()); //id = string res
        }
        return false;
    }

    View.OnClickListener onBottomMenuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment newFragment = null;
            int titleId = R.string.app_name; //default
            switch (v.getId()) {
                case R.id.menuMap:
                    newFragment = new MapFragment();
                    titleId = R.string.map_view;
                    break;
                case R.id.menuList:
                    newFragment = new ListMessagesFragment();
                    titleId = R.string.list_view;
                    break;
                case R.id.menuNotifications:
                    newFragment = new NotificationsFragment();
                    titleId = R.string.notifications;
                    break;
                case R.id.menuMessages:
                    newFragment = new ChatSummaryFragment();
                    titleId = R.string.chat_summary;
                    break;
            }
            if(newFragment!=null && getSupportActionBar()!=null) {
                navigationDrawer.setSelection(-1, false); //remove selection from NavigationDrawer
                edToolbarSearch.setVisibility(View.GONE); //hide searchEditText
                getSupportFragmentManager().beginTransaction().replace(R.id.container,newFragment).commit();
                getSupportActionBar().setTitle(titleId);
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public EditText getToolbarEditText() {
        return edToolbarSearch;
    }
}
