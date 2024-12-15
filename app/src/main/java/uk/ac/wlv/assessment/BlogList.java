package uk.ac.wlv.assessment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BlogList extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton btnNewMsg;
    SearchView searchView;

    DBHelper dbHelper;
    ArrayList<String> message_id, message_title, message, image_path;
    CustomAdapter customAdapter;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list); // Ensure this layout file exists

        // Get the user ID from the Intent
        userID = getIntent().getIntExtra("USER_ID", -1);
        Log.d("BlogListActivity", "User ID = " + userID);

        // Initializing the views
        btnNewMsg = findViewById(R.id.btnAddMessage);
        recyclerView = findViewById(R.id.messagesView);
        searchView = findViewById(R.id.searchView);

        // Set up the floating action button to add a new message
        btnNewMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newMsgActivity = new Intent(BlogList.this, NewMsg.class);
                newMsgActivity.putExtra("USER_ID", userID);
                startActivity(newMsgActivity);
            }
        });

        // Set up DBHelper and initialize array lists for data storage
        dbHelper = new DBHelper(this);
        message_id = new ArrayList<>();
        message_title = new ArrayList<>();
        message = new ArrayList<>();
        image_path = new ArrayList<>();

        // Store the data in the arrays
        storeDataInArrays();

        // Set up the RecyclerView and CustomAdapter
        customAdapter = new CustomAdapter(BlogList.this, this, message_id, message_title, message, image_path);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlogList.this));

        // Implement search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMessages(newText);
                return true;
            }
        });
    }

    // Refresh the activity when a new message is added
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // Refreshes the activity
        }
    }

    // Method to fetch data from the database and populate arrays
    void storeDataInArrays() {
        Cursor cursor = dbHelper.getMessagesByUserId(userID);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show(); // If no data is found
        } else {
            while (cursor.moveToNext()) {
                message_id.add(cursor.getString(0));
                message_title.add(cursor.getString(1));
                message.add(cursor.getString(2));
                image_path.add(cursor.getString(3));
            }
        }
    }

    // Method to filter messages based on search query
    private void filterMessages(String query) {
        ArrayList<String> filteredMessageId = new ArrayList<>();
        ArrayList<String> filteredMessageTitle = new ArrayList<>();
        ArrayList<String> filteredMessage = new ArrayList<>();
        ArrayList<String> filteredImagePath = new ArrayList<>();

        // Loop through all messages and filter based on the query
        for (int i = 0; i < message_title.size(); i++) {
            if (message_title.get(i).toLowerCase().contains(query.toLowerCase()) ||
                    message.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredMessageId.add(message_id.get(i));
                filteredMessageTitle.add(message_title.get(i));
                filteredMessage.add(message.get(i));
                filteredImagePath.add(image_path.get(i));
            }
        }

        // Update the adapter with filtered data
        customAdapter.updateData(filteredMessageId, filteredMessageTitle, filteredMessage, filteredImagePath);
    }
}
