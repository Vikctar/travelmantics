package com.vikctar.vikcandroid.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final int PICTURE_RESULT = 42;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    EditText editTitle;
    EditText editDescription;
    EditText editPrice;
    ImageView imageView;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        editTitle = findViewById(R.id.txt_title);
        editDescription = findViewById(R.id.txt_description);
        editPrice = findViewById(R.id.txt_price);
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null)
            deal = new TravelDeal();
        this.deal = deal;

        editTitle.setText(deal.getTitle());
        editDescription.setText(deal.getDescription());
        editPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_delete).setVisible(true);
            menu.findItem(R.id.menu_save).setVisible(true);
        } else {
            menu.findItem(R.id.menu_delete).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            saveDeal();
            Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
            clean();
            backToList();
            return true;
        } else if (item.getItemId() == R.id.menu_delete) {
            deleteDeal();
            Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
            backToList();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void clean() {
        editTitle.setText("");
        editDescription.setText("");
        editPrice.setText("");

        editTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(editTitle.getText().toString());
        deal.setDescription(editDescription.getText().toString());
        deal.setPrice(editPrice.getText().toString());

        if (deal.getId() == null) {
            databaseReference.push().setValue(deal);
        } else {
            databaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Deal not found", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
    }

    private void backToList() {
        startActivity(new Intent(this, ListActivity.class));
    }

    private void enableEditTexts(boolean isEnabled) {
        editTitle.setEnabled(isEnabled);
        editDescription.setEnabled(isEnabled);
        editPrice.setEnabled(isEnabled);
    }
}
