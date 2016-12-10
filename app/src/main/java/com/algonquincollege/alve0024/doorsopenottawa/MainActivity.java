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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;
import com.algonquincollege.alve0024.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String ABOUT_DIALOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    // URL to my RESTful API Service hosted on Bluemix account
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    private ProgressBar progressBar;
    private List<MyTask> tasks;
    private List<Building> buildingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

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

        // Run Thread and load the list of building
        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    protected void onDestroy(){
//        // Cleanup code comes here!
//    }


    private void requestData(String uri) {
        new MyTask().execute(uri);
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

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {
            String content = HttpManager.getData(params[0], "alve0024", "password" );
            buildingList = BuildingJSONParser.parseFeed(content);
            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            updateDisplay();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

}
