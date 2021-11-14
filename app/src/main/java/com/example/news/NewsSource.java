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

import com.example.news.adapter.SourceAdapter;
import com.example.news.global.Variables;
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

public class NewsSource extends AppCompatActivity {

    RecyclerView recyclerView;
    SourceAdapter recyclerViewAdapter;

    ArrayList<Source> list_source = new ArrayList<>();
    ArrayList<Source> temp_list_source = new ArrayList<>();
    public String category;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_source);

        category = (String) getIntent().getSerializableExtra("category");

        new GetSource().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSource extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            URL url = null;
            try {
                url = new URL(
                        Variables.API + "/sources?category="+category+"&apiKey=" + Variables.KEY);
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
                Log.d("response source ", HttpResponse+"");

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

                    JSONArray jsonArray = new JSONArray(json.getString("sources"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject= jsonArray.getJSONObject(i);
                        Source source = new Source(
                                jsonObject.getString("id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("description"),
                                jsonObject.getString("url"),
                                jsonObject.getString("category"),
                                jsonObject.getString("language"),
                                jsonObject.getString("country"));
                        list_source.add(source);
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

            recyclerView = (RecyclerView) findViewById(R.id.rvSource);
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
        int end = 10;
        if (list_source.size() < 10){
            end = list_source.size();
        }
        while (i < end) {
            temp_list_source.add(list_source.get(i));
            i++;
        }
    }

    private void initAdapter() {
        recyclerViewAdapter = new SourceAdapter(temp_list_source, getApplicationContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == temp_list_source.size() - 1) {
                        //bottom of list!
                        if (temp_list_source.size() < list_source.size()) {
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        temp_list_source.add(null);
        recyclerView.post(new Runnable() {
            public void run() {
                recyclerViewAdapter.notifyItemInserted(temp_list_source.size() - 1);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temp_list_source.remove(temp_list_source.size() - 1);
                int scrollPosition = temp_list_source.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 5;

                if (nextLimit >= list_source.size()){
                    nextLimit = list_source.size() - currentSize;
                }

                while (currentSize - 1 < nextLimit) {
                    temp_list_source.add(list_source.get(currentSize));
                    currentSize++;
                }

                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

}