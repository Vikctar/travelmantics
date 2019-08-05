package com.vikctar.vikcandroid.travelmantics;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.insert_menu) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachAuthListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFirebaseReference("traveldeals", this);
        RecyclerView recyclerView = findViewById(R.id.rv_deals);
        final DealRecyclerAdapter dealRecyclerAdapter = new DealRecyclerAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(dealRecyclerAdapter);
        FirebaseUtil.attachAuthListener();
    }
}
