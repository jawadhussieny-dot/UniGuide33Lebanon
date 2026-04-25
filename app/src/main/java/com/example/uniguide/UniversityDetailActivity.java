package com.example.uniguide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class UniversityDetailActivity extends AppCompatActivity {

    private int    id;
    private String name, city, website, description;
    private String founded, type, language, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_detail);

        @SuppressLint({"MissingInflatedId", "CutPasteId"}) TextView tvUniCity = findViewById(R.id.tvDetailCity);

        // Receive data passed via Intent from HomeActivity
        id          = getIntent().getIntExtra("id", 1);
        name        = getIntent().getStringExtra("name");
        city        = getIntent().getStringExtra("city");
        website     = getIntent().getStringExtra("website");
        description = getIntent().getStringExtra("description");
        founded     = getIntent().getStringExtra("founded");
        type        = getIntent().getStringExtra("type");
        language    = getIntent().getStringExtra("language");
        color       = getIntent().getStringExtra("color");

        // SharedPreferences — save last viewed university
        SharedPreferences prefs = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
            .putString("last_viewed_university", name)
            .putInt("last_viewed_university_id", id)
            .apply();

        // Apply university color to header
        LinearLayout header = findViewById(R.id.layoutDetailHeader);
        header.setBackgroundColor(Color.parseColor(color));

        // ══════════════════════════════════════════
        // CAMPUS PHOTO PLACEHOLDER
        // After saving uni_1_campus.jpg etc. to res/drawable/:
        //
        // android.widget.ImageView imgBanner = findViewById(R.id.imgDetailBanner);
        // imgBanner.setVisibility(View.VISIBLE);
        // int campusRes = getResources().getIdentifier(
        //     "uni_" + id + "_campus", "drawable", getPackageName());
        // if (campusRes != 0)
        //     imgBanner.setImageResource(campusRes);
        // ══════════════════════════════════════════

        // Back button
        TextView btnBack = findViewById(R.id.btnDetailBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        // Fill data — ALL comes from YOUR MySQL database
        TextView tvName = findViewById(R.id.tvDetailName);
        tvName.setText(name);

        TextView tvCity = findViewById(R.id.tvDetailCity);
        tvCity.setText("📍 " + city);

        TextView tvFounded = findViewById(R.id.tvDetailFounded);
        tvFounded.setText(founded != null && !founded.isEmpty() ? founded : "N/A");
        tvFounded.setTextColor(Color.parseColor(color));

        TextView tvType = findViewById(R.id.tvDetailType);
        tvType.setText(type != null && !type.isEmpty() ? type : "University");
        tvType.setTextColor(Color.parseColor(color));

        TextView tvLanguage = findViewById(R.id.tvDetailLanguage);
        tvLanguage.setText(language != null && !language.isEmpty() ? language : "");
        tvLanguage.setTextColor(Color.parseColor(color));

        // Description — from YOUR database
        TextView tvDesc = findViewById(R.id.tvDetailDescription);
        tvDesc.setText(description);

        // BUTTON 1 — View Majors
        MaterialButton btnMajors = findViewById(R.id.btnViewMajors);
        btnMajors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityDetailActivity.this,
                    MajorsActivity.class);
                intent.putExtra("uni_id",   id);
                intent.putExtra("uni_name", name);
                intent.putExtra("color",    color);
                startActivity(intent);
            }
        });

        // BUTTON 2 — Student Services
        MaterialButton btnServices = findViewById(R.id.btnStudentServices);
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityDetailActivity.this,
                    ServicesActivity.class);
                intent.putExtra("uni_id",   id);
                intent.putExtra("uni_name", name);
                intent.putExtra("color",    color);
                startActivity(intent);
            }
        });

        String name = getIntent().getStringExtra("name");
        String city = getIntent().getStringExtra("city");

        tvUniCity.setText("📍 " + city);

        tvUniCity.setOnClickListener(v -> {
            String query = name + ", " + city + ", Lebanon";
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(fallbackIntent);
            }
        });

        // BUTTON 3 — Open website with AlertDialog confirmation
        MaterialButton btnWebsite = findViewById(R.id.btnOpenWebsite);
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog — course concept
                new AlertDialog.Builder(UniversityDetailActivity.this)
                    .setTitle("Open Website")
                    .setMessage("Open the official website of " + name + "?")
                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(website)));
                            } catch (Exception e) {
                                Toast.makeText(UniversityDetailActivity.this,
                                    "Cannot open browser",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            }
        });
    }

    // ACTION BAR MENU — course concept
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Share");
        menu.add(0, 2, 0, "Back");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out " + name + "!\n" + website);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        }
        if (item.getItemId() == 2) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
