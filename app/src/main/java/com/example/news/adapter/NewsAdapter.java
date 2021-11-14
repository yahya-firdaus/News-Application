package com.example.news.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.NewsDetail;
import com.example.news.NewsSource;
import com.example.news.R;
import com.example.news.model.News;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<News> list_news;

    public NewsAdapter( ArrayList<News> vlist_news, Context vcontext) {
        list_news = vlist_news;
        context = vcontext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return list_news == null ? 0 : list_news.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list_news.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView source_name;
        public TextView title;
        public TextView published_at;
        public TextView author;
        public ConstraintLayout news;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            source_name = itemView.findViewById(R.id.source_name);
            title = itemView.findViewById(R.id.title);
            published_at = itemView.findViewById(R.id.published_at);
            author = itemView.findViewById(R.id.author);
            news = itemView.findViewById(R.id.news);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        String published_at = "-";
        if (list_news.get(position).getPublishedAt() != null && !list_news.get(position).getPublishedAt().equals("") && !list_news.get(position).getPublishedAt().equals("null")) {
            String[] separated = list_news.get(position).getPublishedAt().replace("Z", "").split("T");
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = null;
            try {
                newDate = spf.parse(separated[0]);
                spf = new SimpleDateFormat("dd MMM yyyy");
                separated[0] = spf.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            published_at = separated[0]+", "+separated[1];
        }
        String author = "-";
        if (list_news.get(position).getAuthor() != null && !list_news.get(position).getAuthor().equals("") && !list_news.get(position).getAuthor().equals("null")){
            author = list_news.get(position).getAuthor();
        }

        viewHolder.source_name.setText(list_news.get(position).getSourceName());
        viewHolder.title.setText(list_news.get(position).getTitle());
        viewHolder.published_at.setText(published_at);
        viewHolder.author.setText(author);
        if (list_news.get(position).getUrlImage() != null && !list_news.get(position).getUrlImage().equals("")) {
            new DownloadImageTask(viewHolder.image).execute(list_news.get(position).getUrlImage());
        }
        ((ItemViewHolder) viewHolder).news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", list_news.get(position).getUrl());
                context.startActivity(intent);
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}