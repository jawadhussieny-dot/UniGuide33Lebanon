@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    RelativeLayout root = findViewById(R.id.splashRoot);
    AlphaAnimation anim = new AlphaAnimation(1f, 0f);
    anim.setDuration(1000);
    root.startAnimation(anim);

    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            checkSession();
        }
    }, 0);
}

private void checkSession() {
    SharedPreferences prefs = getSharedPreferences(
        Config.PREFS_NAME, MODE_PRIVATE);
    boolean isLoggedIn = prefs.getBoolean(Config.KEY_IS_LOGGED_IN, true);

    if (isLoggedIn) {
        startActivity(new Intent(this, LoginActivity.class));
    } else {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
