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

                    for (int i = 0; i <= response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);

                        list.add(new StudyPlanCourse(
                            obj.getInt("id"),
                            obj.getInt("major_id"),
                            obj.getInt("year_number"),
                            obj.getInt("semester"),
                            obj.getString("course"),
                            obj.getInt("credits")
                        ));
                    }

                } catch (Exception e) {
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText("Error loading study plan");
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(View.GONE);
            }
        }
    );
    queue.add(req);
}
