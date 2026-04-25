private void signupUser(final String username,
                        final String email,
                        final String password) {
    showLoading(true);
    hideError();

    String url = Config.BASE_URL + "signup.php"
        + "?username=" + username
        + "&email=" + email
        + "&password=" + password;

    RequestQueue queue = Volley.newRequestQueue(this);
    StringRequest request = new StringRequest(
        Request.Method.GET, url,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showLoading(false);
                String res = response.trim();

                if (res.equals("exists")) {
                    showError("Username already taken. Choose another.");
                    return;
                }

                if (res.equals("-1") || res.equals("short")) {
                    showError("Signup failed. Please try again.");
                    return;
                }

                int newUserId = Integer.parseInt(res);

                SharedPreferences prefs = getSharedPreferences(
                    Config.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Config.KEY_IS_LOGGED_IN, true);
                editor.putInt(Config.KEY_USER_ID, newUserId);
                editor.putString(Config.KEY_USERNAME, username);
                editor.putString(Config.KEY_EMAIL, email);
                editor.apply();

                Toast.makeText(SignupActivity.this,
                    "Account created! Welcome " + username + "!",
                    Toast.LENGTH_SHORT).show();

                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                finish();
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
