package com.example.uniguide;

public class Config {

   
// XAMPP URL — 10.0.2.2 = your computer from inside the emulator 
   public static final String BASE_URL = "http://10.1.1.205/uniguide/";
    // SharedPreferences file name
    public static final String PREFS_NAME = "UniGuidePref";

    // SharedPreferences keys
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID      = "userId";
    public static final String KEY_USERNAME     = "username";
    public static final String KEY_EMAIL        = "email";

    // University brand colors
    public static String getColor(int id) {
        switch (id) {
            case 1: return "#A6192E"; // AUB red
            case 2: return "#2E7D32"; // LAU green
            case 3: return "#0D47A1"; // USJ blue
            case 4: return "#C62828"; // LU red
            case 5: return "#1565C0"; // BAU blue
            case 6: return "#1E88E5"; // NDU blue
            default: return "#D4AF37"; // fallback
        }
    }
}
