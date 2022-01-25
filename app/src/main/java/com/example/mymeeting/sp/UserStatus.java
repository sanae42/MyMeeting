package com.example.mymeeting.sp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class UserStatus {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public void UserStatus(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void login(int id, String name, String password, Boolean rememberPassword){
        editor = pref.edit();
        editor.putInt("id",id);
        editor.putString("name",name);
        editor.putString("password",password);
        editor.putBoolean("rememberPassword",rememberPassword);

        editor.putBoolean("login",true);
    }

    public void logout(){
        editor.putBoolean("login",false);
    }

    public int getId(){
        return pref.getInt("id",-1);
    }

    public String getName(){
        return pref.getString("name",null);
    }

    public String getPassword(){
        return pref.getString("password",null);
    }

    public Boolean getRememberPassword(){
        return pref.getBoolean("rememberPassword",false);
    }

    public Boolean getLogin(){
        return pref.getBoolean("login",false);
    }
}
