/**
 *   Doors Open Ottawa - List information about the Buildings with the Doors Open
 *
 *   @author Leonardo Alps (alve0024@algonquinlive.com)
 *
 */

package com.algonquincollege.alve0024.doorsopenottawa;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;
import com.algonquincollege.alve0024.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.alve0024.doorsopenottawa.HttpManager;
import com.algonquincollege.alve0024.doorsopenottawa.HttpMethod;
import com.algonquincollege.alve0024.doorsopenottawa.RequestPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ABOUT_DIALOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    // URL to my RESTful API Service hosted on Bluemix account
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    private ProgressBar progressBar;
    private List<GetTask> tasks;
    private List<Building> buildingList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        tasks = new ArrayList<>();

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);

                Intent intent = new Intent(getListView().getContext(), DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("buildingName", theSelectedBuilding.getName());
                intent.putExtra("buildingAddress", theSelectedBuilding.getAddress());
                intent.putExtra("buildingDescription", theSelectedBuilding.getDescription());

                // Loop to read the OpenHour of the selected building
                String openHours = "";
                for (int i=0; i<theSelectedBuilding.getOpenHours().size(); i++) {
                    openHours += theSelectedBuilding.getOpenHours().get(i)+"\n";
                }

                intent.putExtra("buildingOpenHours", openHours);
                startActivity(intent);
            }
        });


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Toast.makeText(getApplicationContext(), "Swipe Refresh Layout", Toast.LENGTH_LONG).show();
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        // Run Thread and load the list of building
                        requestData( REST_URI );
                    }
                }
        );
        // Run Thread and load the list of building
        requestData( REST_URI );
//        searchMe();
    }


    private void searchMe() {
        Building building = new Building();
        int size = buildingList.size();
        for(int i = 0; i < size ; i++) {
            building = buildingList.get(i);
            String name = building.getName();
            if (name == "alve0024") {
                Toast.makeText(getApplicationContext(), "I found you!", Toast.LENGTH_LONG).show();
            }
        }
    }


//    @Override
//    protected void onDestroy(){
//        // Cleanup code comes here!
//    }


    private void requestData(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);

        if (isOnline()) {
            GetTask getTask = new GetTask();
            getTask.execute(getPackage);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    protected void updateDisplay() {
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager conn_manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conn_manager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting()) ? true : false;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(MainActivity.this, "Swipe Refresh Layout", Toast.LENGTH_LONG).show();
    }


    private class GetTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0], "alve0024", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            buildingList = BuildingJSONParser.parseFeed(result);
            updateDisplay();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
        } else
        if (id == R.id.action_add) {
//            Toast.makeText(MainActivity.this, "Added building", Toast.LENGTH_LONG).show();
            createBuilding();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBuilding() {
        String filePath = "images/building.jpg";
        Building building = new Building();
        building.setBuildingId(0);
        building.setName("alve0024");
        building.setAddress("1385 Woodroffe Ave.");
        building.setImage(filePath);
        building.setDescription("My building description!");

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri(REST_URI);
        pkg.setParam("buildingId", building.getBuildingId() + "");
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
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

}
