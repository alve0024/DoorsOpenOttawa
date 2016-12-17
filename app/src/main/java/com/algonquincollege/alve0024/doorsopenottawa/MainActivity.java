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

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;
import com.algonquincollege.alve0024.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Purpose: Provide the List of Buildings from Open Door Ottawa.
 *
 * - Request the buildings from the server
 * - Create a new building into the server
 * - Update a building from the server
 * - Delete a building from the server
 * - Save on SharedPreferences the favorite buildings
 * - Search a building
 * - Use asynchronous thread to request and submit data to the server
 *
 */
public class MainActivity extends ListActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ABOUT_DIALOG_TAG;
    static {
        ABOUT_DIALOG_TAG = "About Dialog";
    }

    // URL to my RESTful API Service hosted on Bluemix account
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static LocalStorage mLocalStorage;

    // Field
    private ProgressBar mProgressBar;
    private List<GetTask> mTasks;
    private List<Building> mBuildingList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BuildingAdapter mBuildingAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Visual indicator of progress when it is fetching data from the server
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Refresh the contents of the view via a vertical swipe gesture
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mTasks = new ArrayList<>();
        mLocalStorage = new LocalStorage(getApplicationContext());

        // Single selection && register this ListActivity as the event handler
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        /**
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        // Run Thread and load the list of building
                        requestData( REST_URI );
                    }
                }
        );

        // Run Thread and load the list of building
        requestData( REST_URI );
    }

    /**
     * Called after finish to retrieve the data and display on the screen
     */
    protected void checkFavoriteBuilding() {
        for(int i = 0; i <= mBuildingList.size()-1; i++) {
            int buildingId = mBuildingList.get(i).getBuildingId();

            if (isFavorite(String.valueOf(buildingId))) {
                mBuildingList.get(i).setFavorite(true);
            } else {
                mBuildingList.get(i).setFavorite(false);
            }
        }
    }

    /**
     * Called to check at Local Storage if the building is favorite
     * @param buildingId
     * @return
     */
    public boolean isFavorite(String buildingId) {
        boolean result = false;
        if (mLocalStorage != null) {
            List<String> favorites = mLocalStorage.getFavorites();
            if (favorites != null) {
                for (String code : favorites) {
                    if (code.equals(buildingId)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get the result from the Intent. The result can come from two different places,
     * NewBuildingActivity or EditBuildingActivity where it is called when the user long click
     * on the item.
     *
     * @param requestCode: Identify where the result is coming from
     *                   1 - It is from the NewBuildingActivity
     *                   2 - It is from the EditBuildingActivity
     *
     * @param resultCode
     * @param data - An Extra parameter RESULT is used to identify if the save or cancel
     *               button was tapped.
     *                  SAVE - It will return some values and a building must be created or
     *                         edited.
     *                  CANCEL - It Handles properly and nothing actually happens.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 1 = Result came from NewBuildingActivity
        if(requestCode == 1) {
            if(data != null) {
                String result = data.getStringExtra("RESULT");
                if (result.equalsIgnoreCase("SAVE")) {
                    createBuilding(
                            data.getStringExtra("addressBuilding"),
                            data.getStringExtra("imageBuilding"),
                            data.getStringExtra("descriptionBuilding")
                    );
                } else  {
                    // Means the user tapped the cancel button
                }
            }
        // 2 = Result came from EditBuildingActivity
        } else if(requestCode == 2) {
            if (data != null) {
                String result = data.getStringExtra("RESULT");
                if (result.equalsIgnoreCase("SAVE")) {
                    updateBuilding(
                            data.getStringExtra("idBuilding"),
                            data.getStringExtra("addressBuilding"),
                            data.getStringExtra("descriptionBuilding")
                    );
                } else {
                    // Means the user tapped the cancel button
                }
            }
        }
    }

    /**
     * Implementation of the Interface SwipeRefreshLayout.OnRefreshListener where
     * refresh this list keeping the data up-to-dated with the server
     */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        ((BuildingAdapter) getListAdapter()).refreshList();
    }

    /**
     * Responsible to inflate the main menu resource defined on menu/menu_main.xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // SearchManager class provides access to the system search services.
        // It'S not instantiate directly but through context.getSystemService(Context.SEARCH_SERVICE)
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // SearchView is a widget that provides a user interface for the user to enter a search
        // query and submit a request to a search provider.
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        /**
         * Callbacks for changes to the query text.
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * @param s the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let
             *         the SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String s) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
                ((BuildingAdapter) getListAdapter()).refreshList();
                return false;
            }

            /**
             * Called when the query text is changed by the user.
             * @param s the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing
             *         any suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String s) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
                ((BuildingAdapter) getListAdapter()).refreshList();
                return false;
            }
        });
        return true;
    }

    /**
     * Called when the user selects an item from the options menu (including action items in
     * the app bar).
     * @param item Passes the MenuItem selected which can be indentify by calling getItemId()
     * @return When you successfully handle a menu item, return true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Inflate the about dialog
        if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
            // Inflate the NewBuildingActivity
        } else if (id == R.id.action_add) {
            createNewBuildingScreen();
        }

        // Handles the check menu where the list of buildings can be sorted.
        if (item.isCheckable()) {
            // If the list is null there is nothing to be sorted
            if ( mBuildingList == null ) {
                return true;
            }

            // Which sort menu item did the user pick?
            switch(id) {
                case R.id.action_sort_name_asc:
                    Collections.sort(mBuildingList, new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort(mBuildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            return lhs.getName().toLowerCase().compareTo( rhs.getName().toLowerCase() );
                        }
                    }));
                    break;
            }
            // Remember which sort option the user picked
            item.setChecked( true );
            // Re-fresh the list to show the sort order
            ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called from the main menu, it just create an intent inflating the NewBuildingActivity
     * identifying this operation by the code 1. Then waits for a RESULT flag that can be
     *      SAVE - Returning the data of the new building to be sent to the server
     *      CANCEL - Which does not do anything
     */
    private void createNewBuildingScreen() {
        Intent intent = new Intent(this, NewBuildingActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Responsible to request data from the server. It needs to be used carefully because it
     * is one of the most expensive operation of the application.
     * @param uri - REST_URL with the address of the server must be passed
     */
    private void requestData(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);

        // If the user has connection to the internet the GetTask Thread is called
        if (isOnline()) {
            GetTask getTask = new GetTask();
            getTask.execute(getPackage);
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Called after the GetTask Thread finish to retrieve the list of building from the server
     * it's used to update the ListView and provide a cursor to it.
     */
    protected void updateDisplay() {
        mBuildingAdapter = new BuildingAdapter(this, R.layout.item_building, mBuildingList);
        // Provide the cursor for the list view.
        setListAdapter(mBuildingAdapter);
        checkFavoriteBuilding();
    }

    /**
     * Check if the user is online. It is called right before the app make any request to the
     * server.
     * @return if the user is connected or not.
     */
    protected boolean isOnline() {
        ConnectivityManager conn_manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conn_manager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting()) ? true : false;
    }

    /**
     *  createBuilding method is used to created a package with the buildings's
     *  information provided by the user
     *
     *  The name of the building will always be alve0024 and cannot be changed
     *
     * @param address - address of the building (required)
     * @param image - a string with file (required)
     * @param description - description of the building (required)
     */
    private void createBuilding(String address, String image, String description) {
        Building building = new Building();
        // The name is fixed using my user name alve0024
        building.setName("alve0024");
        building.setAddress(address);
        building.setImage(image);
        building.setDescription(description);

        // Create a package with the building information
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri(REST_URI);
        // Set parameters to the package
        pkg.setParam("name", building.getName());
        pkg.setParam("address", building.getAddress());
        pkg.setParam("image", building.getImage());
        pkg.setParam("description", building.getDescription());

        // Before send the package to the server check if the user has internet connection
        if (isOnline()) {
            DoTask postTask = new DoTask();
            postTask.execute(pkg);
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update a particular building's information.
     * Limitation: For education purpose only the address and description of the
     * building is allowed and only from my building - alve0024
     * @param id - Building ID
     * @param address
     * @param description
     */
    private void updateBuilding(String id, String address, String description) {
        Building building = new Building();
        building.setBuildingId(Integer.parseInt(id));
        building.setAddress( address );
        building.setDescription( description );

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.PUT );
        pkg.setUri( REST_URI + "/"+id);
        pkg.setParam("address", building.getAddress());
        pkg.setParam("description", building.getDescription());

        if (isOnline()) {
            DoTask putTask = new DoTask();
            putTask.execute( pkg );
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete a particular building.
     * Usually called from openDeleteBuildingDialog()
     * Limitation: For education purpose only my buiding alve0024 is allowed the be deleted
     * @param id - Building ID
     */
    private void deleteBuilding(int id) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        // DELETE the building with Id BUILDING_ID
        pkg.setUri(REST_URI + "/"+id+"");

        if (isOnline()) {
            DoTask deleteTask = new DoTask();
            deleteTask.execute( pkg );
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }



    public void openDeleteBuildingDialog(final int id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want delete?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteBuilding(id);
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Thread responsible to retrieve data from the server parse it into mBuildingList
     * and display on the screen calling updateDisplay() method.
     */
    private class GetTask extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected void onPreExecute() {
            if (mTasks.size() == 0) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mTasks.add(this);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0], "alve0024", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            mTasks.remove(this);
            if (mTasks.size() == 0) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            // If no data returned assumes that the web service is unavailable.
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
            mBuildingList = BuildingJSONParser.parseFeed(result);
            updateDisplay();
//            getListView().setSelection(getListView().getAdapter().getCount()-1);
        }
    }

    /**
     * Thread responsible to do all the other operations like INSERT, UPDATE adn DELETE
     */
    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {
            String content = HttpManager.getData(params[0], "alve0024", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            requestData(REST_URI);
            mProgressBar.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

}
