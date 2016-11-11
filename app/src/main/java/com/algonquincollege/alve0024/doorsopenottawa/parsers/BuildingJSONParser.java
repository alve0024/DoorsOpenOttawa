package com.algonquincollege.alve0024.doorsopenottawa.parsers;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonardoalps on 2016-11-07.
 */

public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {
        try {
            // Old
            //  JSONObject jsonResponse = new JSONObject(content);
            //  JSONArray buildingArray = jsonResponse.getJSONArray("buildings");

            // New
            JSONArray buildingArray = new JSONObject(content).getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();


            for (int i = 0; i < buildingArray.length(); i++) {
                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                building.setAddress(obj.getString("address"));
                building.setImage(obj.getString("image"));
                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}