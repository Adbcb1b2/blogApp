package uk.ac.wlv.assessment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;

public class BlogList extends AppCompatActivity {

    // User Interface components
    RecyclerView recyclerView;
    FloatingActionButton btnNewMsg, btnDeleteSelected, btnShare;
    SearchView searchView;

    DBHelper dbHelper; // For database operations
    ArrayList<String> message_id, message_title, message, image_path; // To hold message data
    CustomAdapter customAdapter;
    int userID; // To store userID passed from login activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        // Get the user ID from the login activity - to be used for database operations throughout app
        userID = getIntent().getIntExtra("USER_ID", -1);
        // Log.d("BlogListActivity", "User ID = " + userID); TEST - verify userID is passed correctly

        // Initialise UI components
        btnNewMsg = findViewById(R.id.btnAddMessage);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnShare = findViewById(R.id.btnShareSM);
        recyclerView = findViewById(R.id.messagesView);
        searchView = findViewById(R.id.searchView);

        // New message click listener
        // Opens the NewMsg activity, passes the userID to enable insertion of new message into the database
        btnNewMsg.setOnClickListener(view -> {
            Intent newMsgActivity = new Intent(BlogList.this, NewMsg.class); // Source activtiy, destination activity
            newMsgActivity.putExtra("USER_ID", userID); // Pass the userID to new activity
            startActivity(newMsgActivity); // Launch activity
        });

        // When delete button has been pressed, call delete function
        btnDeleteSelected.setOnClickListener(view -> deleteSelectedMessages());

        btnShare.setOnClickListener(v -> {
            // Get the selected message IDs from the adapter
            HashSet<String> selectedMessages = customAdapter.getSelectedMessages();

            if (!selectedMessages.isEmpty()) {
                StringBuilder shareContent = new StringBuilder();

                // Loop through the selected messages by their IDs
                for (String messageId : selectedMessages) {
                    // Find the index of the selected message ID in the message_id list
                    int index = message_id.indexOf(messageId);

                    // If the message ID exists in the list, retrieve the corresponding title and message
                    if (index != -1) {
                        String title = message_title.get(index);
                        String content = message.get(index);

                        // Append the title and content to the share content
                        shareContent.append("Title: ").append(title).append("\n").append(content).append("\n\n");
                    }
                }

                // Create the share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                // Add the content to the intent
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());

                // Open the share dialog
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // Initialise database
        dbHelper = new DBHelper(this);

        // Load data into arrays
        message_id = new ArrayList<>();
        message_title = new ArrayList<>();
        message = new ArrayList<>();
        image_path = new ArrayList<>();
        storeDataInArrays();

        // Set up the recycler view
        customAdapter = new CustomAdapter(BlogList.this, this, message_id, message_title, message, image_path);

        // Set up the share and delete button visibility, depending on whether messages are selected
        customAdapter.setOnSelectionChangeListener(hasSelection -> {
            if (hasSelection) {
                btnShare.setVisibility(View.VISIBLE);  // Show FAB when messages are selected
                btnDeleteSelected.setVisibility(View.VISIBLE);
            } else {
                btnShare.setVisibility(View.GONE);  // Hide FAB when no messages are selected
                btnDeleteSelected.setVisibility(View.GONE);

            }
        });
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlogList.this));

        /// Set up searchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMessages(newText); // Filter messages depending on search criteria as it is entered
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate(); // Refresh activity
        }
    }

    // Function to load data into arrays
    void storeDataInArrays() {
        Cursor cursor = dbHelper.getMessagesByUserId(userID);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                message_id.add(cursor.getString(0));
                message_title.add(cursor.getString(1));
                message.add(cursor.getString(2));
                image_path.add(cursor.getString(3));
            }
        }
    }

    // Function to filter messages depending on search criteria
    private void filterMessages(String query) {
        ArrayList<String> filteredMessageId = new ArrayList<>();
        ArrayList<String> filteredMessageTitle = new ArrayList<>();
        ArrayList<String> filteredMessage = new ArrayList<>();
        ArrayList<String> filteredImagePath = new ArrayList<>();

        String lowerCaseQuery = query.trim().toLowerCase(); // Trim and lowercase the query
        for (int i = 0; i < message_title.size(); i++) {
            // Convert current message and title to lowercase
            String title = message_title.get(i).toLowerCase();
            String content = message.get(i).toLowerCase();
            // If the query is contained in either the title or content of the current message
            if (title.contains(lowerCaseQuery) || content.contains(lowerCaseQuery)) {
                // Add messages to the filtered lists
                filteredMessageId.add(message_id.get(i));
                filteredMessageTitle.add(message_title.get(i));
                filteredMessage.add(message.get(i));
                filteredImagePath.add(image_path.get(i));
            }
        }

        // Update the adapter with the filtered list
        customAdapter.updateData(filteredMessageId, filteredMessageTitle, filteredMessage, filteredImagePath);
    }

    // Function to delete selected messages from the database
    private void deleteSelectedMessages() {
        HashSet<String> selectedMessages = customAdapter.getSelectedMessages();
        if (!selectedMessages.isEmpty()) {
            dbHelper.deleteMessagesByIds(new ArrayList<>(selectedMessages)); // Delete message from the database
            Toast.makeText(this, "Messages deleted", Toast.LENGTH_SHORT).show();
            recreate(); // Refresh the activity to reload the updated list
        } else {
            Toast.makeText(this, "No messages selected", Toast.LENGTH_SHORT).show();
        }
    }


}
