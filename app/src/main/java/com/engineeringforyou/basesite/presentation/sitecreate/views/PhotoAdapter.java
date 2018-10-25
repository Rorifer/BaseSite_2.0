package com.engineeringforyou.basesite.presentation.sitecreate.views;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.engineeringforyou.basesite.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public interface RemoveListener {
        void isRemoveItem();
    }

    private List<Uri> mList;
    private RemoveListener mListener;
    private boolean isDeleteMode;

    public PhotoAdapter(RemoveListener listener) {
        mListener = listener;
        mList = new ArrayList<>();
    }

    public void addImage(Uri image) {
        mList.add(image);
        notifyItemInserted(mList.size() - 1);
    }

    public List<Uri> getUriList() {
        return mList;
    }

    public ArrayList<String> getUriStringList() {
        ArrayList<String> list = new ArrayList<>(mList.size());
        for (Uri uri : mList) {
            if (uri != null) list.add(uri.toString());
        }
        return list;
    }

    public void setListFromString(List<String> list) {
        if (list == null) return;
        mList.clear();
        for (String uriString : list) mList.add(Uri.parse(uriString));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        holder.icon.setImageURI(mList.get(position));
        holder.deleteIcon.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private ImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageView);
            deleteIcon = itemView.findViewById(R.id.imageDelete);

            icon.setOnLongClickListener(v -> {
                isDeleteMode = !isDeleteMode;
                notifyDataSetChanged();
                return true;
            });

            deleteIcon.setOnClickListener(view -> {
                int pos = this.getAdapterPosition();
                mList.remove(pos);
                notifyItemRemoved(pos);
                mListener.isRemoveItem();
            });
        }
    }
}
