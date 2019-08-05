package com.vikctar.vikcandroid.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    EditText editTitle;
    EditText editDescription;
    EditText editPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("traveldeals");

        editTitle = findViewById(R.id.txt_title);
        editDescription = findViewById(R.id.txt_description);
        editPrice = findViewById(R.id.txt_price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            saveDeal();
            Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
            clean();
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
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        String price = editPrice.getText().toString();

        TravelDeal travelDeal = new TravelDeal(title, description, price, "");
        databaseReference.push().setValue(travelDeal);


    }
}
