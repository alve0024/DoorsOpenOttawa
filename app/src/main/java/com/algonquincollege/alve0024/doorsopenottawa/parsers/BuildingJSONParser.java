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

package com.algonquincollege.alve0024.doorsopenottawa.parsers;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * After the buildings are retrieved from the server this class is created and
 * parseFeed is called to parse the JSON file creating a List of Building
 */
public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {
        try {
            JSONArray buildingArray = new JSONObject(content).getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {
                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();
                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                building.setAddress(obj.getString("address"));
                building.setDescription(obj.getString("description"));
                building.setImage(obj.getString("image"));
                JSONArray openHours = obj.getJSONArray("open_hours");
                for (int j = 0; j < openHours.length(); j++) {
                    building.addDate(openHours.getJSONObject(j).getString("date"));
                }
                buildingList.add(building);
            }
            return buildingList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
