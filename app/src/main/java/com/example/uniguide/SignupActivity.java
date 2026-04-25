package com.example.uniguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText       etUsername;
    private EditText       etEmail;
    private EditText       etPassword;
    private MaterialButton btnSignup;
    private TextView       tvError;
    private TextView       tvGoToLogin;
    private ProgressBar    progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername  = findViewById(R.id.etSignupUsername);
        etEmail     = findViewById(R.id.etSignupEmail);
        etPassword  = findViewById(R.id.etSignupPassword);
        btnSignup   = findViewById(R.id.btnSignup);
        tvError     = findViewById(R.id.tvSignupError);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.progressBarSignup);

        // SIGNUP button — calls signup.php → saves to MySQL
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email    = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showError(getString(R.string.error_fill_all));
                    return;
                }
                if (password.length() < 4) {
                    showError(getString(R.string.error_password_short));
                    return;
                }
                signupUser(username, email, password);
            }
        });

        // Go back to Login
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Volley call → signup.php?username=X&email=Y&password=Z
    // PHP hashes password SHA-256 and saves to MySQL users table
    // Returns new user ID if success, "exists" if username taken, "-1" if error
    private void signupUser(final String username,
                            final String email,
                            final String password) {
        showLoading(true);
        hideError();

        String url = Config.BASE_URL + "signup.php"
            + "?username=" + username
            + "&email="    + email
            + "&password=" + password;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
            Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showLoading(false);
                    String res = response.trim();

                    if (res.equals("exists")) {
                        showError("Username already taken. Choose another.");
                        return;
                    }
                    if (res.equals("-1") || res.equals("short")) {
                        showError("Signup failed. Please try again.");
                        return;
                    }

                    // Success — save session in SharedPreferences
                    int newUserId = Integer.parseInt(res);

                    SharedPreferences prefs = getSharedPreferences(
                        Config.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(Config.KEY_IS_LOGGED_IN, true);
                    editor.putInt(Config.KEY_USER_ID,    newUserId);
                    editor.putString(Config.KEY_USERNAME, username);
                    editor.putString(Config.KEY_EMAIL,    email);
                    editor.apply();

                    Toast.makeText(SignupActivity.this,
                        "Account created! Welcome " + username + "!",
                        Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(SignupActivity.this,
                        HomeActivity.class));
                    finish();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showLoading(false);
                    showError(getString(R.string.error_network));
                }
            }
        );
        queue.add(request);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignup.setEnabled(!show);
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
}
