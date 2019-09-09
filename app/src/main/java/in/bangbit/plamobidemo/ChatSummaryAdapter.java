package in.bangbit.plamobidemo;

import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ChatSummaryAdapter extends RecyclerView.Adapter<ChatSummaryAdapter.ViewHolder> {

    private ArrayList<Dialog> dialogs;

    public ChatSummaryAdapter() {
        this.dialogs = new ArrayList<>();
        for(int i=0;i<10;i++) {
            dialogs.add(new Dialog("John Green","Hello, I am looking for a some thing",3*1000*60*60*24));
        }
    }

    @Override
    public ChatSummaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_summary,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChatSummaryAdapter.ViewHolder holder, final int position) {
        final Dialog dialog = dialogs.get(position);
        Picasso.with(holder.itemView.getContext()).load(R.drawable.profile_icon_grey).transform(new CircleTransform()).into(holder.photo);
        holder.name.setText(dialog.getUsername());
        holder.message.setText(dialog.getMessage());
        holder.time.setText(dialog.getWhen());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.time.getTranslationX() == 0) {
                    holder.photo.animate().translationX(MainSingleton.convertDpToPixel(-128)).start();
                    holder.itemView.findViewById(R.id.llDialogNameAndMessage).animate().translationX(MainSingleton.convertDpToPixel(-128)).start();
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
                if(holder.time.getTranslationX() != 0) {
                    holder.photo.animate().translationX(0).start();
                    holder.itemView.findViewById(R.id.llDialogNameAndMessage).animate().translationX(0).start();
                    holder.time.animate().translationX(0).start();
                    holder.btnRemove.animate().translationX(0).start();
                    holder.btnBlockUser.animate().translationX(0).start();
                } else {
                    MainActivity.activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChatFragment()).commit();
                }
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.photo.animate().translationX(0).start();
                holder.itemView.findViewById(R.id.llDialogNameAndMessage).animate().translationX(0).start();
                holder.time.animate().translationX(0).start();
                holder.btnRemove.animate().translationX(0).start();
                holder.btnBlockUser.animate().translationX(0).start();
                dialogs.remove(position);
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
                Set<String> filtersSet = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext()).getStringSet(SettingsFragment.BLOCKED_USERS_KEY, new HashSet<String>());
                filtersSet.add(dialog.getUsername());
                PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext()).edit().putStringSet(SettingsFragment.BLOCKED_USERS_KEY, filtersSet).apply();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView name,message,time;
        FrameLayout btnRemove,btnBlockUser;


        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView)itemView.findViewById(R.id.imChatPhoto);
            name = (TextView)itemView.findViewById(R.id.tvChatUsername);
            message = (TextView)itemView.findViewById(R.id.tvChatMessage);
            time = (TextView)itemView.findViewById(R.id.tvChatTimePassed);
            btnRemove = (FrameLayout)itemView.findViewById(R.id.btnDialogItemRemove);
            btnBlockUser = (FrameLayout)itemView.findViewById(R.id.btnDialogItemBlockUser);
        }
    }

    public class Dialog {
        private String name, message;
        private long time;

        public Dialog(String name, String message, long time) {
            this.name = name;
            this.message = message;
            this.time = time;
        }

        public String getUsername() {
            return name;
        }

        public String getMessage() {
            return message;
        }

        public String getWhen() {
            long millisPassed = new Date().getTime() - time;
            String when = String.format("%d days ago",Math.round(millisPassed/(1000*60*60*24)));
            return when;
        }
    }
}
