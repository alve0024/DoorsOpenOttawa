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

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * EditBuildingActivity allows the user to edit the building information
 * except the name and Id.
 *
 * Everything is handled at MainActivity, it's just returned the
 * intent result from the user.
 *
 * Create - Add a new building to the server. However the name is going to be
 *          ALWAYS alve0024
 * Edit - Edit any field except the name of the building alve0024 and the ID
 *
 */

public class EditBuildingActivity extends Activity {

    private int mIdBuilding;
    private TextView mNameLabel;
    private EditText mAddressEditText;
    private EditText mDescriptionEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_building);

        /* Connect with the view */
        mNameLabel = (TextView) findViewById(R.id.nameLabel);
        mAddressEditText = (EditText) findViewById(R.id.addressTxt);
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionTxt);
        
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIdBuilding = bundle.getInt("buildingId");
            mNameLabel.setText(bundle.getString("buildingName"));
            mAddressEditText.setText(bundle.getString("buildingAddress"));
            mDescriptionEditText.setText(bundle.getString("buildingDescription"));
        }
    }

    /**
     * cancelBuilding is called when the Cancel Button is tapped.
     * It is declared using the android:onClick on new_building.xml.
     * It will return to MainActivity.java the Intent Result.
     *
     * RESULT = CANCEL
     * RESULT = SAVE
     *
     * @param v - The View passed as a param
     */
    public void cancelBuilding(View v) {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "CANCEL");

        setResult(2, intent);
        finish();
    }

    /**
     * saveBuilding is called when the Cancel Button is tapped.
     * It is declared using the android:onClick on new_building.xml.
     * It will return to MainActivity.java the Intent Result as -1
     *
     * RESULT = CANCEL
     * RESULT = SAVE
     *
     * @param v - The View passed as a param
     */

    public void saveBuilding(View v) {
        Intent intent = new Intent();
        intent.putExtra("RESULT", "SAVE");
        intent.putExtra("idBuilding", mIdBuilding+"");
        intent.putExtra("addressBuilding", mAddressEditText.getText().toString());
        intent.putExtra("descriptionBuilding", mDescriptionEditText.getText().toString());

        setResult(2, intent);
        finish();
    }
}
