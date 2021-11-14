package com.example.news;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class NewsCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_category);

        List<String> list_category = new ArrayList<String>();
        list_category.add("business");
        list_category.add("entertainment");
        list_category.add("general");
        list_category.add("health");
        list_category.add("science");
        list_category.add("sports");
        list_category.add("technology");

        List<Integer> list_id_category = new ArrayList<Integer>();
        list_id_category.add(R.id.business);
        list_id_category.add(R.id.entertainment);
        list_id_category.add(R.id.general);
        list_id_category.add(R.id.health);
        list_id_category.add(R.id.science);
        list_id_category.add(R.id.sports);
        list_id_category.add(R.id.technology);

        for (int i = 0; i < list_category.size(); i++){
            int finalI = i;
            findViewById(list_id_category.get(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), NewsSource.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("category", list_category.get(finalI));
                    getApplicationContext().startActivity(intent);
                }
            });
        }
    }
}