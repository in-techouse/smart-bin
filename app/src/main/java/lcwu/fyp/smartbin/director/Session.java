package lcwu.fyp.smartbin.director;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import lcwu.fyp.smartbin.model.User;

public class Session {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public Session(Context c) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        editor = preferences.edit();
        gson = new Gson();
    }

    public void setSession(User user) {
        String value = gson.toJson(user);
        editor.putString("user", value);
        editor.commit();
    }

    public void destroySession() {
        editor.remove("user");
        editor.commit();
    }

    public User getUser() {
        User user = new User();
        try {

            String value = preferences.getString("user", "*");

            if (value == null || value.equals("*")) {
                user = null;
            } else {
                user = gson.fromJson(value, User.class);
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }


}
