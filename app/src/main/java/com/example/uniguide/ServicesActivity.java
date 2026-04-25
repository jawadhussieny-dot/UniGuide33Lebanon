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
