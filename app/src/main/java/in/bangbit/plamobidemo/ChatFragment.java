package in.bangbit.plamobidemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView)inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new ChatAdapter(getDemoMessages()));
        return recyclerView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText edToolbarSearch = ((MainActivity)getActivity()).getToolbarEditText();
        edToolbarSearch.setVisibility(
                edToolbarSearch.getVisibility() == View.VISIBLE ?
                        View.GONE :
                        View.VISIBLE
        );
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ChatMessage> getDemoMessages() {
        ArrayList<ChatMessage> messages = new ArrayList<>();
        for(int i=0;i<=10;i++) {
            ChatMessage message = null;
            if(i%2==1) {
                message = new ChatMessage(
                        "Tom Parrot","",
                        "Hi! That would be amazing! I haven't seen you for a while. Where can we met?",
                        System.currentTimeMillis()
                );
            } else {
                message = new ChatMessage(
                        "John Green","",
                        "Hey! Can we meet today? I have some great story to share!",
                        System.currentTimeMillis()
                );
            }
            messages.add(message);
        }
        return messages;
    }

}
