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

        getAndSetIntentData();

        // Handle gallery button click
        btnPhotoViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
            }
        });

        // Handle camera button click
        btnPhotoViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ViewMsg.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ViewMsg.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                } else {
                    openCamera();
                }
            }
        });

        // Update button click listener
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                String messageText = message_input.getText().toString();
                String subjectText = title_input.getText().toString();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                shareIntent.putExtra(Intent.EXTRA_TEXT, messageText);

                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Uri imageUri = Uri.fromFile(imageFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    }
                }

                try {
                    startActivity(Intent.createChooser(shareIntent, "Choose an email client"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewMsg.this, "No email app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                imagePath = saveImageToInternalStorage(selectedImage);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            imageView.setImageBitmap(photo);
            imagePath = saveImageToInternalStorage(photo);
        }
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("message")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            imagePath = getIntent().getStringExtra("imagePath");

            title_input.setText(title);
            message_input.setText(message);

            if (imagePath != null && !imagePath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(bitmap != null ? bitmap : BitmapFactory.decodeResource(getResources(), R.drawable.image_placeholder));
            } else {
                imageView.setImageResource(R.drawable.image_placeholder);
            }
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
