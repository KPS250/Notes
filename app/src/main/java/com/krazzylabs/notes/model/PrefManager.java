package com.krazzylabs.notes.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kshit on 4/22/2017.
 */

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "notes";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String VIEW_SWITCH = "ViewSwitch";
    private static final String DISPLAY_SCREEN = "DisplayScreen";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setDefaultViewSwitch() {
        Boolean flag = getDefaultViewSwitch();

        if(flag)
         editor.putBoolean(VIEW_SWITCH, false);
        else
            editor.putBoolean(VIEW_SWITCH, true);
        editor.commit();
    }

    public Boolean getDefaultViewSwitch() {
        return pref.getBoolean(VIEW_SWITCH, false);
    }

    // For Display Screen
    public void setDisplayScreen(String screen) {
        editor.putString(DISPLAY_SCREEN, screen);
        editor.commit();
    }

    public String getDisplayScreen() {
        return pref.getString(DISPLAY_SCREEN, "NoteList");
    }

}
