package uk.ac.wlv.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    DBHelper dbHelper;
    EditText etUsername, etPassword;
    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        dbHelper = new DBHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // REGISTER BUTTON
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username, password from fields.
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // If username or password isn't entered
                if (username.isEmpty() || password.isEmpty()) {
                    // Notify user to enter data into both fields.
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                } else {
                    // Check a user exists with the entered name
                    if (dbHelper.checkUserExists(username)) {
                        // Notify user the username is taken
                        Toast.makeText(Login.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                        etUsername.setText("");
                        etPassword.setText("");
                    } else {
                        // If username isn't take, insert data
                        boolean isInserted = dbHelper.insertData(username, password);
                        if (isInserted) {
                            // Insert successful - User registered
                            Toast.makeText(Login.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                            etUsername.setText("");
                            etPassword.setText("");
                        } else {
                            // Insert unsuccessful - User not registered
                            Toast.makeText(Login.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // LOGIN BUTTON
        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Get username and password from text fields
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Check if there's data in the username/password fields
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if(dbHelper.checkPassword(username, password)){ // Check password and username match
                    // Get the user ID
                    int userID = dbHelper.getUserID(username, password);
                    Log.d("LoginActivity", "User ID: " + userID); // TEST

                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // Launch the activity
                    Intent blogListActivity = new Intent(Login.this, BlogList.class);
                    blogListActivity.putExtra("USER_ID", userID); // Add userID to intent
                    startActivity(blogListActivity);

                }else{
                    // Alert user of incorrect details
                    Toast.makeText(Login.this, "Incorrect details. Please try again.", Toast.LENGTH_SHORT).show();
                    etUsername.setText("");
                    etPassword.setText("");

                }

            }
        });
    }
}