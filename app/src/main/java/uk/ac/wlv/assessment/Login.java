package uk.ac.wlv.assessment;

import android.content.Intent;
import android.os.Bundle;
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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbHelper.checkUserExists(username)) {
                        Toast.makeText(Login.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isInserted = dbHelper.insertData(username, password);
                        if (isInserted) {
                            Toast.makeText(Login.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if(dbHelper.checkPassword(username, password)){
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent blogListActivity = new Intent(Login.this, BlogList.class);
                    startActivity(blogListActivity);
                }

            }
        });
    }
}