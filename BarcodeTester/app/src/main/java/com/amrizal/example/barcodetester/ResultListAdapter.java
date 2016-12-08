package com.amrizal.example.barcodetester;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by amrizal.zainuddin on 8/12/2016.
 */
public class ResultListAdapter extends BaseAdapter{

    private final LayoutInflater inflater;
    Context context;
    List<Result> resultList;
    public ResultListAdapter(Context context, List<Result> list) {
        this.context = context;
        this.resultList = list;

        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(resultList == null){
            return 0;
        }
        return resultList.size();
    }

    @Override
    public Result getItem(int i) {
        if(resultList == null || resultList.size() < i){
            return null;
        }
        return resultList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.result_item, null);
            holder = new ViewHolder();
            holder.title = (TextView)view.findViewById(R.id.title);
            holder.isbn = (TextView)view.findViewById(R.id.isbn);
            holder.author = (TextView)view.findViewById(R.id.author);
            holder.description = (TextView)view.findViewById(R.id.description);
            holder.thumbnail = (NetworkImageView)view.findViewById(R.id.thumbnail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Result result = resultList.get(i);
        if(result == null){
            return view;
        }

        holder.title.setText(result.getTitle());
        holder.isbn.setText(result.getIsbn());
        holder.author.setText(result.getAuthor());
        holder.description.setText(result.getDescription());
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        holder.thumbnail.setImageUrl(result.getThumbnailPath(), imageLoader);

        return view;
    }

    class ViewHolder{
        NetworkImageView thumbnail;
        TextView title;
        TextView isbn;
        TextView author;
        TextView description;
    }
}
