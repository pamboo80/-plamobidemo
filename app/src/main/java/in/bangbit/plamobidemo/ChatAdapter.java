package in.bangbit.plamobidemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatMessage> messageList;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d h:m a", Locale.US);

    private static final int MESSAGE_IN = 0; //inbox
    private static final int MESSAGE_OUT = 1; //message from me

    public ChatAdapter(ArrayList<ChatMessage> messageList) {
        super();
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getUsername().equals("Tom Parrot") ? MESSAGE_OUT : MESSAGE_IN;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                viewType == MESSAGE_IN ? R.layout.item_chatmessage_in : R.layout.item_chatmessage_out,
                parent, false),viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.tvText.setText(message.getText());
        holder.tvTime.setText(dateFormat.format(new Date(message.getTime())));
        if(holder.viewType==MESSAGE_IN) {
            holder.tvUsername.setText(message.getUsername());
            //Picasso.with(holder.itemView.getContext()).load(R.drawable.demo_man_face).transform(new CircleTransform()).into(holder.imPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        ImageView imPhoto;
        TextView tvText;
        TextView tvTime;

        int viewType;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            tvUsername = (TextView)itemView.findViewById(R.id.tvUsername);
            imPhoto = (ImageView)itemView.findViewById(R.id.imPhoto);
            tvText = (TextView)itemView.findViewById(R.id.tvText);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
        }
    }
}
