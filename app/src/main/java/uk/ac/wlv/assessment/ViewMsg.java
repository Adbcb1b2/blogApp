package uk.ac.wlv.assessment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ViewMsg extends AppCompatActivity {
    EditText title_input, message_input;
    Button update_button, share_button;
    String id, title, message, imagePath;
    ImageView imageView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);

        title_input = findViewById(R.id.etTitleViewMsg);
        message_input = findViewById(R.id.etMessageViewMsg);
        update_button = findViewById(R.id.btnSaveMsgViewMsg);
        imageView = findViewById(R.id.ivPhotoPrevViewMsg);
        share_button = findViewById(R.id.btnShareEmail);

        getAndSetIntentData(); // Put the data in the text fields
        Log.d("View Message Activity", imagePath);

        // Update button click listener
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(ViewMsg.this);
                boolean result = dbHelper.updateMessage(id, (title_input.getText()).toString(), (message_input.getText()).toString()); // Write new data to db
                if (result) {
                    Toast.makeText(ViewMsg.this, "Message Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Share button click listener
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = message_input.getText().toString();
                String subjectText = title_input.getText().toString();

                // Create the email intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain"); // Set type to email

                // Add the subject and body text to the intent
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
                shareIntent.putExtra(Intent.EXTRA_TEXT, messageText);

                // Check if the imagePath is valid and add the image to the email
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Uri imageUri = Uri.fromFile(imageFile); // Get the URI of the image file
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri); // Attach image
                    }
                }else{
                    Toast.makeText(ViewMsg.this, "Unable to add image", Toast.LENGTH_SHORT).show();
                }

                // Start the intent chooser
                try {
                    startActivity(Intent.createChooser(shareIntent, "Choose an email client"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewMsg.this, "No email app installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Get data passed from previous activity
    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("message")) {

            // Getting the data from the intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            message = getIntent().getStringExtra("message");
            imagePath = getIntent().getStringExtra("imagePath");

            // Setting intent data to text fields
            title_input.setText(title);
            message_input.setText(message);

            // Set the image, if there is one
            if (imagePath != null && !imagePath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.image_placeholder);
                }
            } else {
                imageView.setImageResource(R.drawable.image_placeholder);
            }
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
