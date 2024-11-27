package uk.ac.wlv.assessment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BlogList extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton btnNewMsg;

    DBHelper dbHelper;
    ArrayList<String> message_id, message_title, message, image_path;
    CustomAdapter customAdapter;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list); // Ensure this layout file exists

        // Get the user ID from the Intent, or set to minus 1
        userID = getIntent().getIntExtra("USER_ID", -1);
        Log.d("BlogListActivity", "User ID = " + userID);

        btnNewMsg =findViewById(R.id.btnAddMessage);
        recyclerView=findViewById(R.id.messagesView);

        // Add message on-click listener
        btnNewMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent newMsgActivity = new Intent(BlogList.this, NewMsg.class);
                newMsgActivity.putExtra("USER_ID", userID);
                startActivity(newMsgActivity);
            }
        });

        dbHelper = new DBHelper(this);

        // Initialise array lists
        message_id = new ArrayList<>();
        message_title = new ArrayList<>();
        message = new ArrayList<>();
        image_path = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(BlogList.this,message_id, message_title, message, image_path);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlogList.this));

    }

    void storeDataInArrays(){
        Cursor cursor = dbHelper.getMessagesByUserId(userID);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show(); // If no data returned
        }else{
            while (cursor.moveToNext()){
                message_id.add(cursor.getString(0));
                message_title.add(cursor.getString(1));
                message.add(cursor.getString(2));
                image_path.add(cursor.getString(3));
            }
        }
    }
}