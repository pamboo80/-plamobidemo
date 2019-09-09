package in.bangbit.plamobidemo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Adapter for BlockedUsers in SettingsFragment AND CurrentFilters in FiltersFragment

public class GreyItemAdapter extends RecyclerView.Adapter<GreyItemAdapter.ViewHolder> {

    private Context context;
    private List<String> dataList;
    private String key; //for preferences

    public GreyItemAdapter(Context context, String key) {
        super();
        this.context = context;
        this.dataList = new ArrayList<>();
        if(PreferenceManager.getDefaultSharedPreferences(context).contains(key)) {
            Set<String> filtersSet = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(key,new HashSet<String>());
            String[] filters = filtersSet.toArray(new String[filtersSet.size()]);
            Collections.addAll(dataList,filters);
        }
        this.key = key;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grey,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(dataList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, getItemCount());
                updatePreferences();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.tvGreyItemLabel);
        }
    }

    public void addItems(String[] items) {
        Collections.addAll(dataList, items);
        updatePreferences();
        notifyDataSetChanged();
    }

    private void updatePreferences() {
        Set<String> filtersSet = new HashSet<>();
        Collections.addAll(filtersSet, dataList.toArray(new String[dataList.size()]));
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(key, filtersSet).apply();
    }
}
