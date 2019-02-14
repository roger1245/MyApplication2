package com.example.q.myapplication2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.q.myapplication2.R;

import org.w3c.dom.Text;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private int resourceId;

    public ArticleAdapter(Context context, int resource, List<Article> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.collect_title);
            viewHolder.author = (TextView) view.findViewById(R.id.collect_author);
            viewHolder.date = (TextView) view.findViewById(R.id.collect_date);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setText(article.getTitle());
        viewHolder.author.setText(article.getAuthor());
        viewHolder.date.setText(article.getDate());
        return view;
    }
    class ViewHolder {
        TextView title;
        TextView author;
        TextView date;
    }
}
