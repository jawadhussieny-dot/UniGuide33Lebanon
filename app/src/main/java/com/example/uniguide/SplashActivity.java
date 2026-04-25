@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    RelativeLayout root = findViewById(R.id.splashRoot);
    AlphaAnimation anim = new AlphaAnimation(0f, 1f);
    anim.setDuration(1000);
    root.startAnimation(anim);

    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            checkSession();
        }
    }, 2500);
}

private void checkSession() {
    SharedPreferences prefs = getSharedPreferences(
        Config.PREFS_NAME, MODE_PRIVATE);
    boolean isLoggedIn = prefs.getBoolean(Config.KEY_IS_LOGGED_IN, false);

    if (isLoggedIn) {
        startActivity(new Intent(this, HomeActivity.class));
    } else {
        startActivity(new Intent(this, LoginActivity.class));
    }
    finish();
}
