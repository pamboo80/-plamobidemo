package in.bangbit.plamobidemo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    Context context;
    ArrayList<Notification> dataList;

    public NotificationsAdapter(Context context) {
        super();
        this.context = context;
        int n=12;
        dataList = new ArrayList<>(n);
        for(int i=0;i<12;i++) {
            Notification notification;
            if(i%2==0) {
                notification = new Notification("User <b>John Green</b> sent you a message","John Green",true,i);
            } else {
                notification = new Notification("One of your search filters \'cab\' matches with <b>user123</b> post","user123",false,i);
            }
            dataList.add(notification);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.label.setText(Html.fromHtml(dataList.get(position).title));
        holder.label.setCompoundDrawablesWithIntrinsicBounds(
                dataList.get(position).type ? R.drawable.ic_message_blue : R.drawable.ic_person_blue, 0, 0, 0
        );
        holder.time.setText(String.valueOf(dataList.get(position).daysAgo).concat(" days ago"));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.label.getTranslationX() == 0) {
                    holder.label.animate().translationX(MainSingleton.convertDpToPixel(-128)).start();
                    holder.time.animate().translationX(MainSingleton.convertDpToPixel(-128)).start();
                    holder.btnRemove.animate().translationX(MainSingleton.convertDpToPixel(-128)).start();
                    holder.btnBlockUser.animate().translationX(MainSingleton.convertDpToPixel(-64)).start();
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.label.getTranslationX()!=0) {
                    holder.label.animate().translationX(0).start();
                    holder.time.animate().translationX(0).start();
                    holder.btnRemove.animate().translationX(0).start();
                    holder.btnBlockUser.animate().translationX(0).start();
                } else {
                    if(dataList.get(position).type) {
                        ((FragmentActivity)context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container,new ChatFragment())
                                .commit();
                    } else {
                        ((FragmentActivity)context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container,MapFragment.newInstanceWithPopup())
                                .commit();
                    }
                }
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.label.animate().translationX(0).start();
                holder.time.animate().translationX(0).start();
                holder.btnRemove.animate().translationX(0).start();
                holder.btnBlockUser.animate().translationX(0).start();
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, getItemCount());
            }
        });
        holder.btnBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove notification
                holder.btnRemove.performClick();
                //Block User
                Set<String> filtersSet = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(SettingsFragment.BLOCKED_USERS_KEY, new HashSet<String>());
                filtersSet.add(dataList.get(position).user);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(SettingsFragment.BLOCKED_USERS_KEY, filtersSet).apply();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label, time;
        private FrameLayout btnRemove,btnBlockUser;
        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView)itemView.findViewById(R.id.tvNotificationItemLabel);
            time = (TextView)itemView.findViewById(R.id.tvNotificationItemTime);
            btnRemove = (FrameLayout)itemView.findViewById(R.id.btnNotificationItemRemove);
            btnBlockUser = (FrameLayout)itemView.findViewById(R.id.btnNotificationItemBlockUser);
        }
    }

    private class Notification {
        String title, user;
        boolean type;
        int daysAgo;
        public Notification(String title, String user, boolean type,int daysAgo) {
            this.title = title;
            this.user = user;
            this.type = type;
            this.daysAgo = daysAgo;
        }
    }
}
