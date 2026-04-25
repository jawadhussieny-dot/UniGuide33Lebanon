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
