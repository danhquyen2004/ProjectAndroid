package com.example.tlupickleball.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<String> searchResults;
    private OnItemClickListener Listener;

    public interface OnItemClickListener {
        void onItemClick(String playerName);
    }

    public SearchAdapter(List<String> searchResults, OnItemClickListener listener) {
        this.searchResults = searchResults;
        this.Listener = listener;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return  new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        String name = searchResults.get(position);
        holder.textView.setText(name);
        holder.itemView.setOnClickListener(v -> Listener.onItemClick(name));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateSearchResults(List<String> newResults) {
        this.searchResults = newResults;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
