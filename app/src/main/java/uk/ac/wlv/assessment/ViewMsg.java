package uk.ac.wlv.assessment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ViewMsg extends AppCompatActivity {
    EditText title_input, message_input;
    Button update_button, share_button;
    String id, title, message, imagePath;
    ImageView imageView;
    DBHelper dbHelper;
    FloatingActionButton btnPhotoViewCamera, btnPhotoViewGallery;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);

        title_input = findViewById(R.id.etTitleViewMsg);
        message_input = findViewById(R.id.etMessageViewMsg);
        update_button = findViewById(R.id.btnSaveMsgViewMsg);
        imageView = findViewById(R.id.ivPhotoPrevViewMsg);
        share_button = findViewById(R.id.btnShareEmail);
        btnPhotoViewCamera = findViewById(R.id.btnPhotoViewCamera);
        btnPhotoViewGallery = findViewById(R.id.btnPhotoViewGallery);

        dbHelper = new DBHelper(this);

        getAndSetIntentData(); // Retrieve data passed by the intent

        // Handle gallery button click
        btnPhotoViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery to pick an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*"); // Set type to image
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY); // Start gallery intent
            }
        });

        // Handle camera button click
        btnPhotoViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if permission for camera is granted
                if (ContextCompat.checkSelfPermission(ViewMsg.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(ViewMsg.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                } else {
                    openCamera(); // Open if permission granted
                }
            }
        });

        // Update button click listener
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Attempt to update the message
                boolean result = dbHelper.updateMessage(id, title_input.getText().toString(), message_input.getText().toString(), imagePath);
                if (result) {
                    Toast.makeText(ViewMsg.this, "Message Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewMsg.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Share button click listener
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get data to be shared
                String messageText = message_input.getText().toString();
                String subjectText = title_input.getText().toString();

                // Create share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                shareIntent.putExtra(Intent.EXTRA_TEXT, messageText);

                // Include image path if available
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Uri imageUri = Uri.fromFile(imageFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    }
                }
                // Try to open share chooser
                try {
                    startActivity(Intent.createChooser(shareIntent, "Choose an email app"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewMsg.this, "No email app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST); // start camera intent
    }

    // Save captured image to internal storage
    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = getApplicationContext().getFilesDir(); // Get the apps internal storage directory
            File file = new File(directory, "image_" + System.currentTimeMillis() + ".png"); // Create a new image file
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Save as PNG
            fos.close();
            return file.getAbsolutePath(); // return file path
        } catch (Exception e) {
            Log.e("SaveImage", "Error saving image: " + e.getMessage());
            return null; // If an error occurs, return null
        }
    }

    // Handle results from other activities (gallery/camera)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Gallery result
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData(); // Get uri
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream); // Decode to Bitmap
                imageView.setImageBitmap(selectedImage); // Set the image in imageView
                imagePath = saveImageToInternalStorage(selectedImage); // Save the image path
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }

        // Camera result
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data"); // Get captured photo
            imageView.setImageBitmap(photo); // Set to imageView
            imagePath = saveImageToInternalStorage(photo); // Save to external storage
        }
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("message")) {
            // Get the data from the intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            imagePath = getIntent().getStringExtra("imagePath");

            // Set the retrieved data into input fields
            title_input.setText(title);
            message_input.setText(message);

            // If image path is available, display the image
            if (imagePath != null && !imagePath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(bitmap != null ? bitmap : BitmapFactory.decodeResource(getResources(), R.drawable.image_placeholder));
            } else {
                imageView.setImageResource(R.drawable.image_placeholder); // Set default to placeholder
            }
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
