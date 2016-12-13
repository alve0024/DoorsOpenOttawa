package com.algonquincollege.alve0024.doorsopenottawa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



/**
 * Created by leonardoalps on 2016-12-13.
 */

public class NewBuildingActivity extends Activity {
    private TextView buildingName;
    private TextView buildingAddress;
    private TextView buildingImage;
    private TextView buildingDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        buildingName = (TextView) findViewById(R.id.nameTxt);
        buildingAddress = (TextView) findViewById(R.id.addressTxt);
        buildingDescription = (TextView) findViewById(R.id.descriptionTxt);
        buildingImage = (TextView) findViewById(R.id.imageTxt);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            buildingName.setText(bundle.getString("buildingName"));
            buildingAddress.setText(bundle.getString("buildingAddress"));
            buildingDescription.setText(bundle.getString("buildingDescription"));
            buildingImage.setText(bundle.getString("buildingOpenHours"));

        }

    }


}
