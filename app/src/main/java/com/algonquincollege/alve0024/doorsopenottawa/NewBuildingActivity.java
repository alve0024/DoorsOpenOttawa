package com.algonquincollege.alve0024.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;
import com.algonquincollege.alve0024.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.alve0024.doorsopenottawa.HttpManager;
import com.algonquincollege.alve0024.doorsopenottawa.HttpMethod;
import com.algonquincollege.alve0024.doorsopenottawa.RequestPackage;

import java.util.List;


/**
 * Created by leonardoalps on 2016-12-13.
 */

public class NewBuildingActivity extends Activity {
    private EditText nameTxt;
    private EditText addressTxt;
    private EditText imageTxt;
    private EditText descriptionTxt;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_building);


        nameTxt = (EditText) findViewById(R.id.nameTxt);
        addressTxt = (EditText) findViewById(R.id.addressTxt);
        descriptionTxt = (EditText) findViewById(R.id.descriptionTxt);
        imageTxt = (EditText) findViewById(R.id.imageTxt);


        saveButton = (Button) findViewById(R.id.saveBtn);
        cancelButton = (Button) findViewById(R.id.saveBtn);

        saveButton.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                createBuilding(nameTxt.getText().toString(),
                        addressTxt.getText().toString(),
                        imageTxt.getText().toString(),
                        descriptionTxt.getText().toString());
            }
        });


//        cancelButton.setOnClickListener( new View.OnClickListener(){
//            public void onClick(View v) {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void createBuilding(String name, String address, String image, String description ) {
        String filePath = "images/building.jpg";
        Building building = new Building();

        building.setName(name);
        building.setAddress(address);
        building.setImage(image);
        building.setDescription(description);

//        building.setName("alve0024");
//        building.setAddress("1385 Woodroffe Ave.");
//        building.setImage(filePath);
//        building.setDescription("My building description!");

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri(REST_URI);
        pkg.setParam("name", building.getName());
        pkg.setParam("address", building.getAddress());
        pkg.setParam("image", building.getImage());
        pkg.setParam("description", building.getDescription());

        DoTask postTask = new DoTask();
        postTask.execute(pkg);
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {
            String content = HttpManager.getData(params[0], "alve0024", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            progressBar.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(NewBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


}
