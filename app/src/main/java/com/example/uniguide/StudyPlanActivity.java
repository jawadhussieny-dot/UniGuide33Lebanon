package com.example.uniguide;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.uniguide.models.StudyPlanCourse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudyPlanActivity extends AppCompatActivity {

    private LinearLayout dynamicContainer;
    private ProgressBar  pb;
    private TextView     tvStatus;
    private int          majorId, duration;
    private String       majorName, uniName, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        majorId   = getIntent().getIntExtra("major_id",  1);
        majorName = getIntent().getStringExtra("major_name");
        uniName   = getIntent().getStringExtra("uni_name");
        duration  = getIntent().getIntExtra("duration",  4);
        color     = getIntent().getStringExtra("color");

        // Apply university color to header
        LinearLayout header = findViewById(R.id.layoutStudyPlanHeader);
        header.setBackgroundColor(Color.parseColor(color));

        TextView tvSubtitle = findViewById(R.id.tvStudyPlanSubtitle);
        tvSubtitle.setText(majorName + "  ·  " + uniName);

        TextView tvDuration = findViewById(R.id.tvStudyPlanDuration);
        tvDuration.setText("Duration: " + duration + " Years");

        // Back button
        TextView btnBack = findViewById(R.id.btnStudyPlanBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        pb               = findViewById(R.id.progressBarStudyPlan);
        tvStatus         = findViewById(R.id.tvStudyPlanStatus);
        dynamicContainer = findViewById(R.id.dynamicStudyPlanContainer);

        // Load study plan from YOUR MySQL database
        loadStudyPlanFromDatabase();
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

    // Volley — calls getstudyplan.php?major_id=X
    // Returns ALL courses for this major from MySQL
    // Then builds the UI dynamically — Dynamic UI course concept
    private void loadStudyPlanFromDatabase() {
        pb.setVisibility(View.VISIBLE);
        String url = Config.BASE_URL + "getstudyplan.php?major_id=" + majorId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest req = new JsonArrayRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pb.setVisibility(View.GONE);
                    try {
                        List<StudyPlanCourse> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            list.add(new StudyPlanCourse(
                                obj.getInt("id"),
                                obj.getInt("major_id"),
                                obj.getInt("year"),
                                obj.getInt("semester"),
                                obj.getString("course_name"),
                                obj.optInt("credits", 3)
                            ));
                        }
                        if (list.isEmpty()) {
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setText(
                                "No courses found in database for this major.\n" +
                                "Add courses in phpMyAdmin → study_plan table.");
                        } else {
                            buildStudyPlanUI(list);
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

    // DYNAMIC UI — builds entire study plan in Java code
    // This is a key course concept — building views programmatically
    // Groups: Year → Semester → Course rows, all from database
    private void buildStudyPlanUI(List<StudyPlanCourse> courses) {
        dynamicContainer.removeAllViews();

        // Group courses by year then semester
        Map<Integer, Map<Integer, List<StudyPlanCourse>>> grouped =
            new LinkedHashMap<>();
        for (StudyPlanCourse c : courses) {
            if (!grouped.containsKey(c.getYear()))
                grouped.put(c.getYear(),
                    new LinkedHashMap<Integer, List<StudyPlanCourse>>());
            if (!grouped.get(c.getYear()).containsKey(c.getSemester()))
                grouped.get(c.getYear()).put(c.getSemester(),
                    new ArrayList<StudyPlanCourse>());
            grouped.get(c.getYear()).get(c.getSemester()).add(c);
        }

        for (Map.Entry<Integer, Map<Integer, List<StudyPlanCourse>>>
                yearEntry : grouped.entrySet()) {

            int year = yearEntry.getKey();

            // Year header — built in Java
            TextView yearHeader = new TextView(this);
            yearHeader.setText("🎓  Year " + year);
            yearHeader.setTextSize(18);
            yearHeader.setTypeface(null, Typeface.BOLD);
            yearHeader.setTextColor(Color.WHITE);
            yearHeader.setBackgroundColor(Color.parseColor(color));
            yearHeader.setPadding(20, 16, 20, 16);
            LinearLayout.LayoutParams yhP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            yhP.setMargins(0, 20, 0, 0);
            yearHeader.setLayoutParams(yhP);
            dynamicContainer.addView(yearHeader);

            for (Map.Entry<Integer, List<StudyPlanCourse>> semEntry
                    : yearEntry.getValue().entrySet()) {

                int                  semNum     = semEntry.getKey();
                List<StudyPlanCourse> semCourses = semEntry.getValue();

                // Semester header — built in Java
                TextView semHeader = new TextView(this);
                semHeader.setText("  Semester " + semNum);
                semHeader.setTextSize(15);
                semHeader.setTypeface(null, Typeface.BOLD);
                semHeader.setTextColor(Color.parseColor(color));
                semHeader.setBackgroundColor(Color.parseColor("#E3F2FD"));
                semHeader.setPadding(20, 12, 20, 12);
                LinearLayout.LayoutParams shP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                shP.setMargins(0, 2, 0, 0);
                semHeader.setLayoutParams(shP);
                dynamicContainer.addView(semHeader);

                // Courses box
                LinearLayout semBox = new LinearLayout(this);
                semBox.setOrientation(LinearLayout.VERTICAL);
                semBox.setBackgroundColor(Color.WHITE);

                for (int i = 0; i < semCourses.size(); i++) {
                    StudyPlanCourse course = semCourses.get(i);

                    // Course row — built in Java
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding(20, 16, 20, 16);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setBackgroundColor(i % 2 == 0
                        ? Color.WHITE : Color.parseColor("#FAFAFA"));

                    // Course number
                    TextView num = new TextView(this);
                    num.setText((i + 1) + ".");
                    num.setTextSize(14);
                    num.setTypeface(null, Typeface.BOLD);
                    num.setTextColor(Color.parseColor(color));
                    num.setMinWidth(40);
                    row.addView(num);

                    // Course name — from YOUR database
                    TextView cName = new TextView(this);
                    cName.setText(course.getCourseName());
                    cName.setTextSize(14);
                    cName.setTextColor(Color.parseColor("#333333"));
                    LinearLayout.LayoutParams cnP = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    cName.setLayoutParams(cnP);
                    row.addView(cName);

                    // Credits
                    TextView cr = new TextView(this);
                    cr.setText(course.getCredits() + " cr");
                    cr.setTextSize(11);
                    cr.setTextColor(Color.parseColor(color));
                    cr.setPadding(8, 0, 0, 0);
                    row.addView(cr);

                    semBox.addView(row);

                    // Divider between courses
                    if (i < semCourses.size() - 1) {
                        View div = new View(this);
                        div.setBackgroundColor(Color.parseColor("#F0F0F0"));
                        LinearLayout.LayoutParams dp =
                            new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        dp.setMargins(56, 0, 0, 0);
                        div.setLayoutParams(dp);
                        semBox.addView(div);
                    }
                }
                dynamicContainer.addView(semBox);
            }
        }

        // Summary footer
        TextView summary = new TextView(this);
        summary.setText("✅  Total: " + courses.size() + " courses in this program");
        summary.setTextSize(13);
        summary.setTextColor(Color.WHITE);
        summary.setBackgroundColor(Color.parseColor("#2E7D32"));
        summary.setGravity(Gravity.CENTER);
        summary.setPadding(20, 14, 20, 14);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        sp.setMargins(0, 20, 0, 0);
        summary.setLayoutParams(sp);
        dynamicContainer.addView(summary);
    }
}
