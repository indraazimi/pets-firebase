package com.example.android.pets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<Pet> mData;
    private ArrayList<Integer> mSelectedId;

    public CatalogAdapter(Context context, ArrayList<Pet> data, ClickHandler handler) {
        mContext = context;
        mData = data;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet pet = mData.get(position);
        holder.nameTextView.setText(pet.getName());
        holder.breedTextView.setText(pet.getBreed());
        holder.itemView.setSelected(mSelectedId.contains(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(int dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(Integer.valueOf(dataId));
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectedId() {
        return mSelectedId;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final TextView nameTextView;
        final TextView breedTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            breedTextView = itemView.findViewById(R.id.breed_text_view);

            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    interface ClickHandler {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}
