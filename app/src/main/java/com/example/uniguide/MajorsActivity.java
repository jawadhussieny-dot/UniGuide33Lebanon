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
                        tvStatus.setText("No majors found in database.");
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
