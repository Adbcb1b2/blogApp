package uk.ac.wlv.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BlogList extends AppCompatActivity {

    FloatingActionButton btnNewMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list); // Ensure this layout file exists

        btnNewMsg =findViewById(R.id.btnAddMessage);

        btnNewMsg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent newMsgActivity = new Intent(BlogList.this, NewMsg.class);
                startActivity(newMsgActivity);
            }
        });
    }
}