package com.example.uniguide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uniguide.adapters.MajorAdapter;
import com.example.uniguide.models.Major;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MajorsActivity extends AppCompatActivity {

    private ListView     listView;
    private ProgressBar  pb;
    private TextView     tvStatus;
    private MajorAdapter adapter;
    private List<Major>  majorsList = new ArrayList<>();

    private int    uniId;
    private String uniName, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_majors);

        uniId   = getIntent().getIntExtra("uni_id", 1);
        uniName = getIntent().getStringExtra("uni_name");
        color   = getIntent().getStringExtra("color");

        // Apply university color to header
        LinearLayout header = findViewById(R.id.layoutMajorsHeader);
        header.setBackgroundColor(Color.parseColor(color));

        TextView tvUniName = findViewById(R.id.tvMajorsUniName);
        tvUniName.setText(uniName);

        listView = findViewById(R.id.listViewMajors);
        pb       = findViewById(R.id.progressBarMajors);
        tvStatus = findViewById(R.id.tvMajorsStatus);

        // Back button
        TextView btnBack = findViewById(R.id.btnMajorsBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        adapter = new MajorAdapter(this, majorsList, color);
        listView.setAdapter(adapter);

        // Click major → StudyPlanActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Major m = majorsList.get(position);
                Intent intent = new Intent(MajorsActivity.this,
                    StudyPlanActivity.class);
                intent.putExtra("major_id",   m.getId());
                intent.putExtra("major_name", m.getMajorName());
                intent.putExtra("uni_name",   uniName);
                intent.putExtra("duration",   m.getDuration());
                intent.putExtra("color",      color);
                startActivity(intent);
            }
        });

        // Load majors from YOUR MySQL database
        loadMajorsFromDatabase();
    }

    // ACTION BAR MENU — course concept
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Back");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    // Volley — calls getmajors.php?university_id=X
    // Returns ALL majors for this university from MySQL
    private void loadMajorsFromDatabase() {
        pb.setVisibility(View.VISIBLE);
        String url = Config.BASE_URL + "getmajors.php?university_id=" + uniId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pb.setVisibility(View.GONE);
                    try {
                        majorsList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            majorsList.add(new Major(
                                obj.getInt("id"),
                                obj.getInt("university_id"),
                                obj.getString("major_name"),
                                obj.getString("faculty"),
                                obj.getInt("duration")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                        if (majorsList.isEmpty()) {
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setText(
                                "No majors found in database.");
                        }
                    } catch (Exception e) {
                        pb.setVisibility(View.GONE);
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("Error: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pb.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText(getString(R.string.error_network));
                }
            }
        );
        queue.add(req);
    }
}
