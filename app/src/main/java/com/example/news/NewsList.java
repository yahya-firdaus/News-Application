package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.news.adapter.NewsAdapter;
import com.example.news.adapter.SourceAdapter;
import com.example.news.global.Variables;
import com.example.news.model.News;
import com.example.news.model.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class NewsList extends AppCompatActivity {

    RecyclerView recyclerViewNews;
    NewsAdapter recyclerViewAdapterNews;

    ArrayList<News> temp_list_news = new ArrayList<>();
    ArrayList<News> list_news = new ArrayList<>();
    public String source;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        source = (String) getIntent().getSerializableExtra("source");

        new GetNews().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNews extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            URL url = null;
            try {
                url = new URL(Variables.API + "?sources="+source+"&apiKey=" + Variables.KEY);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection httpURLConnection = null;
            try {
                assert url != null;
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert httpURLConnection != null;
                httpURLConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            httpURLConnection.addRequestProperty("Accept", "application/json");
            httpURLConnection.addRequestProperty("Content-Type", "application/json");

            try {
                httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0");
                httpURLConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                int HttpResponse = httpURLConnection.getResponseCode();
                Log.d("response news ", HttpResponse+"");

                if (HttpResponse == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line;
                    JSONObject json = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line != null) {
                            json = new JSONObject(line);
                        }
                    }
                    bufferedReader.close();

                    JSONArray jsonArray = new JSONArray(json.getString("articles"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject= jsonArray.getJSONObject(i);
                        json = new JSONObject(jsonObject.getString("source"));
                        News news = new News(
                                json.getString("id"),
                                json.getString("name"),
                                jsonObject.getString("author"),
                                jsonObject.getString("title"),
                                jsonObject.getString("description"),
                                jsonObject.getString("url"),
                                jsonObject.getString("urlToImage"),
                                jsonObject.getString("publishedAt"),
                                jsonObject.getString("content"));
                        list_news.add(news);
                    }

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            recyclerViewNews = (RecyclerView) findViewById(R.id.rvNews);
            populateData();
            initAdapter();
            initScrollListener();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void populateData() {
        int i = 0;
        int end = 5;
        if (list_news.size() < 5){
            end = list_news.size();
        }
        while (i < end) {
            temp_list_news.add(list_news.get(i));
            i++;
        }
    }

    private void initAdapter() {
        recyclerViewAdapterNews = new NewsAdapter(temp_list_news, getApplicationContext());
        recyclerViewNews.setAdapter(recyclerViewAdapterNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initScrollListener() {
        recyclerViewNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == temp_list_news.size() - 1) {
                        //bottom of list!
                        if (temp_list_news.size() < list_news.size()) {
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        temp_list_news.add(null);
        recyclerViewNews.post(new Runnable() {
            public void run() {
                recyclerViewAdapterNews.notifyItemInserted(temp_list_news.size() - 1);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temp_list_news.remove(temp_list_news.size() - 1);
                int scrollPosition = temp_list_news.size();
                recyclerViewAdapterNews.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 5;

                if (nextLimit >= list_news.size()){
                    nextLimit = list_news.size() - currentSize;
                }

                while (currentSize - 1 < nextLimit) {
                    temp_list_news.add(list_news.get(currentSize));
                    currentSize++;
                }

                recyclerViewAdapterNews.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

}