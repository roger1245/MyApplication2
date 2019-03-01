package com.example.q.myapplication2.adapter;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.q.myapplication2.R;

import java.net.URI;
import java.util.List;

public class ZhiHuArticleAdapter extends RecyclerView.Adapter<ZhiHuArticleAdapter.ViewHolder> {
//    private static final int NORMAL_ITEM = 0;
//    private static final int GROUP_ITEM = 1;
    private List<ZhiHuArticle> mZhiHuArticleList;

//    static class NormalItemViewHolder extends RecyclerView.ViewHolder {
//        ImageView articleImage;
//        TextView articleTitle;
//        public NormalItemViewHolder( View itemView) {
//            super(itemView);
//            articleImage = (ImageView) itemView.findViewById(R.id.base_swipe_item_image);
//            articleTitle = (TextView) itemView.findViewById(R.id.base_swipe_item_title);
//        }
//    }
//    public class GroupItemHolder extends NormalItemViewHolder {
//        TextView articleTime;
//        public GroupItemHolder(View itemView) {
//            super(itemView);
//            articleTime = (TextView) itemView.findViewById(R.id.base_swipe_item_time);
//        }
//    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView articleTitle;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        articleTitle = itemView.findViewById(R.id.base_swipe_item_title);
        articleImage = itemView.findViewById(R.id.base_swipe_item_image);
    }
}


    public ZhiHuArticleAdapter(List<ZhiHuArticle> mZhiHuArticleList) {
        this.mZhiHuArticleList = mZhiHuArticleList;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_base_swipe_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ZhiHuArticle zhiHuArticle = mZhiHuArticleList.get(i);
        Glide.with(viewHolder.itemView.getContext()).load(zhiHuArticle.getImageId()).into(viewHolder.articleImage);
        viewHolder.articleTitle.setText(zhiHuArticle.getTitle());
    }

    @Override
    public int getItemCount() {
        return mZhiHuArticleList.size();
    }
}
