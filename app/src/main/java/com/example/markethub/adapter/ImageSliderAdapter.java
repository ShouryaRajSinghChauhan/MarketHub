package com.example.markethub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markethub.R;
import com.example.markethub.utils.ImageUtils;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private final List<String> imageUris;
    private final boolean showRemoveButton;
    private final OnRemoveImageListener removeListener;

    public interface OnRemoveImageListener {
        void onRemoveImage(int position);
    }

    public ImageSliderAdapter(List<String> imageUris, boolean showRemoveButton, OnRemoveImageListener removeListener) {
        this.imageUris = imageUris;
        this.showRemoveButton = showRemoveButton;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String uri = imageUris.get(position);
        ImageUtils.loadImage(holder.itemView.getContext(), uri, holder.ivSliderImage);

        if (showRemoveButton) {
            holder.btnRemoveImage.setVisibility(View.VISIBLE);
            holder.btnRemoveImage.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && removeListener != null) {
                    removeListener.onRemoveImage(pos);
                }
            });
        } else {
            holder.btnRemoveImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return imageUris != null ? imageUris.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSliderImage;
        ImageButton btnRemoveImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSliderImage = itemView.findViewById(R.id.iv_slider_image);
            btnRemoveImage = itemView.findViewById(R.id.btn_remove_image);
        }
    }
}
