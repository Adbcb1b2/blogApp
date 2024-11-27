package uk.ac.wlv.assessment;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class NewMsg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);

        // Get userID from the intent
        int userID = getIntent().getIntExtra("USER_ID", -1);

        Log.d("NewMsgActivity", "User ID: " + userID); //TEST


    }
}
