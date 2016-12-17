/*********************************************************************************************
 *   Doors Open Ottawa - List information about the Buildings with the Doors Open            *
 *                                                                                           *
 *   @author Leonardo Alps (alve0024@algonquinlive.com)                                      *
 *                                                                                           *
 *   Supervision: Gerald.Hurdle@AlgonquinCollege.com                                         *
 *                                                                                           *
 *   Algonquin College - All right reserved!                                                 *
 *                                                                                           *
 *********************************************************************************************/

package com.algonquincollege.alve0024.doorsopenottawa;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Purpose: Provide an easy way to add/remove items from SharedPreferences
 *
 * SharedPreferences used as Local Storage
 */

public class LocalStorage {
    public static final String PREFS_NAME = "MYAPP";
    public static final String KEY = "KEY_FAV";

    android.content.SharedPreferences.Editor editor;

    SharedPreferences settings;

    Set<String> favorites;

    public LocalStorage(Context context) {
        super();
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        favorites = new HashSet<String>();
        favorites = settings.getStringSet(KEY, new ArraySet<String>());
    }

    private void saveFavorites(Context context, Set<String> favorites){
        Set<String> fav = new HashSet<String>();
        editor = settings.edit();
        for(String s:favorites) {
            fav.add(s);
        }
        editor.putStringSet(KEY, fav);
        editor.commit();
    }

    public void addFavorite(Context context, String buildingId){
        favorites.add(buildingId);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, String buildingId) {
        if (favorites != null) {
            favorites.remove(buildingId);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<String> getFavorites() {
        List<String> favoritesList;
        Set<String> newSet;

        if (settings.contains(KEY)) {
            newSet = settings.getStringSet(KEY, new ArraySet<String>());
            String[] favoriteItems= newSet.toArray(new String[newSet.size()]);
            favoritesList = Arrays.asList(favoriteItems);
            favoritesList = new ArrayList<>(favoritesList);
        } else
            return null;

        return (ArrayList<String>) favoritesList;
    }

}