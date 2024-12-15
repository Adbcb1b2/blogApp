package uk.ac.wlv.assessment;

import android.Manifest;
import android.content.ActivityNotFoundException;
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

public class NewMsg extends AppCompatActivity {

    FloatingActionButton btnPhotoAddGallery, btnPhotoAddCamera;
    EditText etTitle;
    EditText etMessage;
    ImageView imageViewPhoto;
    Button btnSaveMessage, btnBackAdd;
    DBHelper dbHelper;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private String imagePath = null; // To store the local image path
    private int userID = -1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);

        dbHelper = new DBHelper(this);

        imageViewPhoto = findViewById(R.id.ivPhotoPrevAdd);
        btnPhotoAddGallery = findViewById(R.id.btnPhotoAddGallery);
        btnSaveMessage = findViewById(R.id.btnSaveMsgAdd);
        btnBackAdd = findViewById(R.id.btnBackAdd);
        etTitle = findViewById(R.id.etTitleAdd);
        etMessage = findViewById(R.id.etMessageAdd);
        btnPhotoAddCamera = findViewById(R.id.btnPhotoAddCamera);

        // Get userID from the intent
        userID = getIntent().getIntExtra("USER_ID", -1);

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        }

        // Gallery button click listener
        btnPhotoAddGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            }
        });

        // Camera button click listener
        btnPhotoAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        // Save button click listener
        btnSaveMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String message = etMessage.getText().toString();

                // Check if both a title and message has been entered, or wont be saved
                if (title.isEmpty() || message.isEmpty()) {
                    Toast.makeText(NewMsg.this, "Please fill in both title and message.", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert the message along with the image path
                    if (imagePath != null) {
                        boolean isSaved = dbHelper.insertMessage(userID, title, message, imagePath);
                        if (isSaved) {
                            Toast.makeText(NewMsg.this, "Message saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NewMsg.this, "Failed to save message.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewMsg.this, "Please add an image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Back button click listener
        btnBackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent blogListActivity = new Intent(NewMsg.this, BlogList.class);
                blogListActivity.putExtra("USER_ID", userID);
                startActivity(blogListActivity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                // Open an InputStream to load the selected image
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Set the selected image to the ImageView
                imageViewPhoto.setImageBitmap(selectedImage);

                // Save the image locally and store the path
                imagePath = saveImageToInternalStorage(selectedImage);
                Log.d("NewMsgActivity", "Saved Image Path: " + imagePath);

            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Display the captured photo in ImageView
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                imageViewPhoto.setImageBitmap(photo);

                // Save the camera photo locally and store the path
                imagePath = saveImageToInternalStorage(photo);
                Log.d("NewMsgActivity", "Saved Image Path: " + imagePath);
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = getApplicationContext().getFilesDir();
            File file = new File(directory, "image_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("SaveImage", "Error saving image: " + e.getMessage());
            return null;
        }
    }

    // Method to open camera and capture a photo
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // Handle the permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for camera, proceed with camera action
                Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied for camera
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
