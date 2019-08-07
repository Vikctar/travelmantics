package com.vikctar.vikcandroid.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class DealActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_deal);

        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        editTitle = findViewById(R.id.txt_title);
        editDescription = findViewById(R.id.txt_description);
        editPrice = findViewById(R.id.txt_price);
        imageView = findViewById(R.id.img_deal_upload);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null)
            deal = new TravelDeal();
        this.deal = deal;

        editTitle.setText(deal.getTitle());
        editDescription.setText(deal.getDescription());
        editPrice.setText(deal.getPrice());

        showImage(deal.getImageUrl());

        Button uploadButton = findViewById(R.id.button_image);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Pick an Image"),
                        PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_delete).setVisible(true);
            menu.findItem(R.id.menu_save).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.button_image).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_delete).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.button_image).setEnabled(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            ImageView uploadImage = findViewById(R.id.img_deal_upload);
            uploadImage.setImageURI(imageUri);
            final StorageReference storageReference =
                    FirebaseUtil.storageReference.child("images/" + imageUri.getLastPathSegment());
            storageReference.putFile(imageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final String imageName = taskSnapshot.getStorage().getPath();
                            storageReference.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            deal.setImageUrl(uri.toString());
                                            deal.setImageName(imageName);
                                            Log.d("Url", uri.toString());
                                            Log.d("Name", imageName);
                                            showImage(uri.toString());
                                        }
                                    }
                            );
                        }
                    });
        }
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
        if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            StorageReference imageRef = FirebaseUtil.firebaseStorage.getReference().child(
                    deal.getImageName());

            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }
    }

    private void backToList() {
        startActivity(new Intent(this, ListActivity.class));
        finish();
    }

    private void enableEditTexts(boolean isEnabled) {
        editTitle.setEnabled(isEnabled);
        editDescription.setEnabled(isEnabled);
        editPrice.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.with(this)
                    .load(url)
                    .into(imageView);
        }
    }
}
