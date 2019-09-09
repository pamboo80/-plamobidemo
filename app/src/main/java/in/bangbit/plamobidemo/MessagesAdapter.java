package in.bangbit.plamobidemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;

    public MessagesAdapter(Context context, List<Message> messageList) {
        super();
        this.context = context;
        this.messages = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        Picasso.with(context)
                .load(message.getImgUrl())
                .transform(new CircleTransform())
                .into(holder.imgUserPhoto);
        holder.tvUsername.setText(message.getUsername());
        holder.tvDistance.setText(String.format(context.getString(R.string.n_miles_away),
                MainSingleton.calculateDistanceFromMeInMiles(message.getLatLng())));
        holder.tvMessageText.setText(message.getText());
        holder.tvDaysAgo.setText(String.format(context.getString(R.string.posted_on_n_days_ago),
                MainSingleton.getDaysAgoFromNow(message.getMessageTimeStamp())));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserPhoto;
        TextView tvUsername;
        TextView tvDistance;
        TextView tvDaysAgo;
        TextView tvMessageText;

        public ViewHolder(View itemView) {
            super(itemView);
            imgUserPhoto = (ImageView)itemView.findViewById(R.id.imListMessagePhoto);
            tvUsername = (TextView)itemView.findViewById(R.id.tvListMessageUsername);
            tvDistance = (TextView)itemView.findViewById(R.id.tvListMessageDistance);
            tvDaysAgo = (TextView)itemView.findViewById(R.id.tvListMessageTime);
            tvMessageText = (TextView)itemView.findViewById(R.id.tvListMessageText);
        }
    }
}
