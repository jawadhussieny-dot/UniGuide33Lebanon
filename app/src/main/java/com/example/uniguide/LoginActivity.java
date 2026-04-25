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
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText       etUsername;
    private EditText       etPassword;
    private MaterialButton btnLogin;
    private TextView       tvError;
    private TextView       tvGoToSignup;
    private ProgressBar    progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername   = findViewById(R.id.etLoginUsername);
        etPassword   = findViewById(R.id.etLoginPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        tvError      = findViewById(R.id.tvLoginError);
        tvGoToSignup = findViewById(R.id.tvGoToSignup);
        progressBar  = findViewById(R.id.progressBarLogin);

        // LOGIN button — calls login.php → checks MySQL
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    showError(getString(R.string.error_fill_all));
                    return;
                }
                loginUser(username, password);
            }
        });

        // Go to Signup screen
        tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    // Volley call → login.php?username=X&password=Y
    // PHP hashes password SHA-256 and checks MySQL users table
    // Returns JSON user object if success, "-1" if wrong credentials
    private void loginUser(final String username, final String password) {
        showLoading(true);
        hideError();

        String url = Config.BASE_URL + "login.php"
            + "?username=" + username
            + "&password=" + password;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
            Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showLoading(false);
                    String res = response.trim();

                    if (res.equals("-1")) {
                        showError("Wrong username or password. Try again.");
                        return;
                    }

                    try {
                        JSONObject user = new JSONObject(res);

                        // SharedPreferences — save login session
                        SharedPreferences prefs = getSharedPreferences(
                            Config.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(Config.KEY_IS_LOGGED_IN, true);
                        editor.putInt(Config.KEY_USER_ID,
                            user.getInt("id"));
                        editor.putString(Config.KEY_USERNAME,
                            user.getString("username"));
                        editor.putString(Config.KEY_EMAIL,
                            user.getString("email"));
                        editor.apply();

                        Toast.makeText(LoginActivity.this,
                            "Welcome back " + user.getString("username") + "!",
                            Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this,
                            HomeActivity.class));
                        finish();

                    } catch (Exception e) {
                        showError("Error: " + e.getMessage());
                    }
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
        btnLogin.setEnabled(!show);
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
}
