package com.viscino.viscino.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.viscino.viscino.Models.GridItem;
import com.viscino.viscino.R;
import com.viscino.viscino.Shop.ShopActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by j on 12-10-2017.
 */

public class GridListAdapter extends ArrayAdapter<GridItem> implements GridAdapter {

    private final LayoutInflater layoutInflater;

    public GridListAdapter(Context context, List<GridItem> items) {
        super(context, 0, items);
        layoutInflater = LayoutInflater.from(context);
    }

    public GridListAdapter(Context context) {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder{
        ImageView image;
        ProgressBar mProgressBar;
        TextView text;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {


        GridItem item = getItem(position);



        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_grid_item , parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridProgressBar);
            holder.image = (ImageView) convertView.findViewById(R.id.gridImageView);
            holder.text = (TextView) convertView.findViewById(R.id.gridTextView);
            holder.text.setText(String.valueOf(item.getText()));
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();

        }
        final String name = item.getText();
        final String imgURL = item.getUrl();

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgURL, holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(holder.mProgressBar != null){
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });


        holder.image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v,
                                "scaleX", 0.9f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v,
                                "scaleY", 0.9f);
                        scaleDownX.setDuration(200);
                        scaleDownY.setDuration(200);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);

                        scaleDown.start();

                        break;

                    case MotionEvent.ACTION_UP:
                        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                                v, "scaleX", 1f);
                        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                                v, "scaleY", 1f);
                        scaleDownX2.setDuration(200);
                        scaleDownY2.setDuration(200);

                        AnimatorSet scaleDown2 = new AnimatorSet();
                        scaleDown2.play(scaleDownX2).with(scaleDownY2);

                        scaleDown2.start();

                        //v.setEnabled(false);
                        Intent intent = new Intent(getContext(), ShopActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("url", imgURL);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(),  holder.image, "profile");
                        getContext().startActivity(intent, options.toBundle());
                        break;
                }
                return true;
            }
        });
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void appendItems(List<GridItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<GridItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
}
