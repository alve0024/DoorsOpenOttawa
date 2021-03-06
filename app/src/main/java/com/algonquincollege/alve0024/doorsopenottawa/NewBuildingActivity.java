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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



/**
 * NewBuildingActivity allows the user to input a new Building
 *
 * Everything is handled at MainActivity, it's just returned the
 * intent result from the user.
 *
 * Create - Add a new building to the server. However the name is going to be
 *          ALWAYS alve0024
 * Edit - Edit any field except the name of the building alve0024 and the ID
 *
 */

public class NewBuildingActivity extends Activity {

    /* Declaration of the EditTexts (Name is fixed and can not be changed  */
    private EditText mAddressEditText;
    private EditText mImageEditText;
    private EditText mDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_building);

        /* Connect with the view */
        mAddressEditText = (EditText) findViewById(R.id.addressTxt);
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionTxt);
        mImageEditText = (EditText) findViewById(R.id.imageTxt);
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

        setResult(1, intent);
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
        intent.putExtra("nameBuilding", "alve0024");
        intent.putExtra("addressBuilding", mAddressEditText.getText().toString());
        intent.putExtra("imageBuilding", mImageEditText.getText().toString());
        intent.putExtra("descriptionBuilding", mDescriptionEditText.getText().toString());

        setResult(1, intent);
        finish();
    }
}
