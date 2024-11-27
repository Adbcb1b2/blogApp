package uk.ac.wlv.assessment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

public class NewMsg extends AppCompatActivity {

    FloatingActionButton btnPhotoAddCamera;
    FloatingActionButton btnPhotoAddGallery;
    EditText etTitle;
    EditText etMessage;
    ImageView imageViewPhoto;
    Button btnSaveMessage;

    private static final int REQUEST_IMAGE_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);

        imageViewPhoto = findViewById(R.id.ivPhotoPrevAdd);
        btnPhotoAddCamera = findViewById(R.id.btnPhotoAddCamera);
        btnPhotoAddGallery = findViewById(R.id.btnPhotoAddGallery);


        // Get userID from the intent
        int userID = getIntent().getIntExtra("USER_ID", -1);
        Log.d("NewMsgActivity", "User ID: " + userID); //TEST

        // Gallery button click listener
        btnPhotoAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Open file picker to select an image
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            }
        });




        /* Camera button click listener
        btnPhotoAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logic
            }
        });*/



        /* Save button click listener
        btnSaveMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logic
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NewMsgActivity", "onActivityResult called"); //TEST

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            if (data == null) {
                Log.d("NewMsgActivity", "Intent data is null");
                return;
            }
            Uri selectedImageUri = data.getData(); // Get the URI of the selected image

            try {
                // Open an InputStream to load the selected image
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Set the selected image to the ImageView
                imageViewPhoto.setImageBitmap(selectedImage);

                // Save the image path as a string for database insertion
                String imagePath = selectedImageUri.toString();
                Log.d("NewMsgActivity", "Image Path: " + imagePath); // TEST

            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image to preview", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
