package com.engineeringforyou.basesite.presentation.sitedetails.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.engineeringforyou.basesite.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoDetailsAdapter extends RecyclerView.Adapter<PhotoDetailsAdapter.ViewHolder> {

    private final Context mContext;
    private List<Uri> mList;
    private OnPhotoClickListener mListener;

    public interface OnPhotoClickListener {
        void onPhotoSelected(Uri uri);
    }

    public PhotoDetailsAdapter(OnPhotoClickListener listener, Context context) {
        mContext  = context;
        mList = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public PhotoDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoDetailsAdapter.ViewHolder holder, int position) {
        Uri uri = mList.get(position);
        holder.icon.setImageURI(null);

        CircularProgressDrawable progress = new CircularProgressDrawable(mContext);
        progress.setStrokeWidth(5f);
        progress.setCenterRadius(30f);
        progress.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN );
        progress.start();

        if (uri != null) {
            Glide.with(holder.icon.getContext())
                    .load(uri)
                    .placeholder(progress)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.icon);
            holder.icon.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onPhotoSelected(uri);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void showPhotosFromUri(List<? extends Uri> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imageView);
        }
    }
}
