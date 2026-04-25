package com.example.uniguide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uniguide.adapters.UniversityAdapter;
import com.example.uniguide.models.University;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView          listView;
    private ProgressBar       progressBar;
    private TextView          tvStatus;
    private TextView          tvWelcome;
    private EditText          etSearch;
    private UniversityAdapter adapter;

    private List<University> allUniversities = new ArrayList<>();
    private List<University> filteredList    = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView    = findViewById(R.id.listViewUniversities);
        progressBar = findViewById(R.id.progressBarHome);
        tvStatus    = findViewById(R.id.tvHomeStatus);
        tvWelcome   = findViewById(R.id.tvWelcome);
        etSearch    = findViewById(R.id.etSearch);

        // SharedPreferences — read saved username to show welcome message
        SharedPreferences prefs = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString(Config.KEY_USERNAME, "Guest");
        tvWelcome.setText("Welcome, " + username + "!");

        // Setup adapter
        adapter = new UniversityAdapter(this, filteredList);
        listView.setAdapter(adapter);

        // Click university → UniversityDetailActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                University u = filteredList.get(position);
                Intent intent = new Intent(HomeActivity.this,
                    UniversityDetailActivity.class);
                intent.putExtra("id",          u.getId());
                intent.putExtra("name",        u.getName());
                intent.putExtra("city",        u.getCity());
                intent.putExtra("website",     u.getWebsite());
                intent.putExtra("description", u.getDescription());
                intent.putExtra("founded",     u.getFounded());
                intent.putExtra("type",        u.getType());
                intent.putExtra("language",    u.getLanguage());
                intent.putExtra("color",       Config.getColor(u.getId()));
                startActivity(intent);
            }
        });

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                filterList(s.toString());
            }
        });

        // Logout button in toolbar
        TextView btnLogout = findViewById(R.id.btnToolbarLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });

        // Load universities from YOUR MySQL database
        loadUniversitiesFromDatabase();
    }

    // ACTION BAR MENU — course concept
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Refresh");
        menu.add(0, 2, 0, "About");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            // Refresh list from database
            allUniversities.clear();
            filteredList.clear();
            adapter.updateList(filteredList);
            tvStatus.setText("  Refreshing...");
            loadUniversitiesFromDatabase();
            return true;
        }
        if (item.getItemId() == 2) {
            // About — AlertDialog course concept
            new AlertDialog.Builder(this)
                .setTitle("About UniGuide Lebanon")
                .setMessage(
                    "UniGuide Lebanon helps students\n" +
                    "explore Lebanese universities,\n" +
                    "majors, study plans, dorms,\n" +
                    "and recommended books.\n\n" +
                    "CSC431 — Mobile Development\n" +
                    "Powered by PHP + MySQL + Volley")
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // AlertDialog for logout confirmation — course concept
    private void confirmLogout() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            })
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    // SharedPreferences — clear session on logout
    private void logout() {
        SharedPreferences prefs = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Volley — calls getuniversities.php
    // Returns ALL universities from YOUR MySQL database
    private void loadUniversitiesFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Config.BASE_URL + "getuniversities.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        allUniversities.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            allUniversities.add(new University(
                                obj.getInt("id"),
                                obj.getString("name"),
                                obj.getString("city"),
                                obj.getString("website"),
                                obj.getString("description"),
                                obj.optString("founded",  ""),
                                obj.optString("type",     ""),
                                obj.optString("language", "")
                            ));
                        }
                        filteredList.clear();
                        filteredList.addAll(allUniversities);
                        adapter.updateList(filteredList);
                        tvStatus.setText("  🏫  " + allUniversities.size()
                            + " Universities — Tap to Explore");
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText("  ⚠️  Error: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    tvStatus.setText("  ⚠️  " + getString(R.string.error_network));
                }
            }
        );
        queue.add(request);
    }

    private void filterList(String query) {
        filteredList.clear();
        for (University u : allUniversities) {
            if (u.getName().toLowerCase().contains(query.toLowerCase()) ||
                u.getCity().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(u);
            }
        }
        adapter.updateList(filteredList);
    }
}
