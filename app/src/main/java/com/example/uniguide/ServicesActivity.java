package com.example.uniguide;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uniguide.adapters.BookAdapter;
import com.example.uniguide.adapters.DormAdapter;
import com.example.uniguide.models.Book;
import com.example.uniguide.models.Dorm;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private LinearLayout servicesContainer;
    private ProgressBar  pb;
    private int          uniId;
    private String       uniName, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        uniId   = getIntent().getIntExtra("uni_id", 1);
        uniName = getIntent().getStringExtra("uni_name");
        color   = getIntent().getStringExtra("color");

        // Apply university color to header
        LinearLayout header = findViewById(R.id.layoutServicesHeader);
        header.setBackgroundColor(Color.parseColor(color));

        TextView tvUniName = findViewById(R.id.tvServicesUniName);
        tvUniName.setText(uniName);

        pb                = findViewById(R.id.progressBarServices);
        servicesContainer = findViewById(R.id.servicesContainer);

        // Back button
        TextView btnBack = findViewById(R.id.btnServicesBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        // Load dorms then books from YOUR MySQL database
        loadDormsFromDatabase();
    }

    // ACTION BAR MENU — course concept
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Back");
        menu.add(0, 2, 0, "Contact Info");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            finish();
            return true;
        }
        if (item.getItemId() == 2) {
            // AlertDialog — course concept
            new AlertDialog.Builder(this)
                .setTitle("Housing Contact")
                .setMessage("For housing inquiries at " + uniName +
                    ",\nvisit the official website or contact\n" +
                    "the student services office directly.")
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Volley — calls getdorms.php?university_id=X
    // Returns ALL dorms for this university from MySQL
    private void loadDormsFromDatabase() {
        pb.setVisibility(View.VISIBLE);
        String url = Config.BASE_URL + "getdorms.php?university_id=" + uniId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pb.setVisibility(View.GONE);
                    try {
                        List<Dorm> dormList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            dormList.add(new Dorm(
                                obj.getInt("id"),
                                obj.getInt("university_id"),
                                obj.getString("dorm_name"),
                                obj.getString("location"),
                                obj.getString("price_range"),
                                obj.optString("description", "")
                            ));
                        }
                        buildDormsSection(dormList);
                        loadBooksFromDatabase(); // load books after dorms
                    } catch (Exception e) {
                        pb.setVisibility(View.GONE);
                        addText("Error loading dorms: " + e.getMessage(), "#C62828");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pb.setVisibility(View.GONE);
                    addText(getString(R.string.error_network), "#C62828");
                }
            }
        );
        queue.add(req);
    }

    // Volley — calls getbooks.php?major_id=1
    // Returns recommended books from MySQL
    private void loadBooksFromDatabase() {
        String url = Config.BASE_URL + "getbooks.php?major_id=1";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        List<Book> bookList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            bookList.add(new Book(
                                obj.getInt("id"),
                                obj.getInt("major_id"),
                                obj.getString("book_name"),
                                obj.getString("author"),
                                obj.optString("edition", "")
                            ));
                        }
                        buildBooksSection(bookList);
                    } catch (Exception e) {
                        addText("Error loading books.", "#999999");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    addText("Books not available.", "#999999");
                }
            }
        );
        queue.add(req);
    }

    private void buildDormsSection(List<Dorm> dorms) {
        TextView title = new TextView(this);
        title.setText("🏠  Student Housing and Dorms");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor(color));
        title.setPadding(8, 8, 8, 14);
        servicesContainer.addView(title);

        if (dorms.isEmpty()) {
            addText("No dorms found in database.", "#999999");
            return;
        }

        DormAdapter adapter = new DormAdapter(this, dorms, color);
        for (int i = 0; i < adapter.getCount(); i++) {
            View row = adapter.getView(i, null, servicesContainer);
            servicesContainer.addView(row);
        }
    }

    private void buildBooksSection(List<Book> books) {
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        servicesContainer.addView(divider);

        TextView title = new TextView(this);
        title.setText("📚  Recommended Books");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor(color));
        title.setPadding(8, 24, 8, 14);
        servicesContainer.addView(title);

        if (books.isEmpty()) {
            addText("No books found in database.", "#999999");
            return;
        }

        BookAdapter adapter = new BookAdapter(this, books);
        for (int i = 0; i < adapter.getCount(); i++) {
            View row = adapter.getView(i, null, servicesContainer);
            servicesContainer.addView(row);
        }
    }

    private void addText(String msg, String textColor) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor(textColor));
        tv.setPadding(8, 8, 8, 8);
        servicesContainer.addView(tv);
    }
}
