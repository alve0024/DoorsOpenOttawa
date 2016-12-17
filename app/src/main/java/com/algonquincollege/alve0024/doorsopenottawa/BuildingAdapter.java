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


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.util.LruCache;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;

/**
 * Purpose: customize the Building cell for each building displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<Building>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on LazyLoad in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable {

    private Context context;
    private List<Building> mBuildingList;
    private List<Building> buildingListForRefresh;
    public CheckBox mFavoriteCheckBox;
    private LruCache<Integer, Bitmap> imageCache;
    private ArrayList<Integer> mFavoriteArrayList = new ArrayList<Integer>();
    LocalStorage mLocalStorage = new LocalStorage(getContext());

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mBuildingList = objects;
        this.buildingListForRefresh = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }


    public void refreshList() {

        mBuildingList = buildingListForRefresh;
        notifyDataSetChanged();
    }

    // ViewHolders pattern as the unit of recycling and reuse
    private static class ViewHolder {
        ImageView sBuildingImageView;
        TextView sBuildingNameTextView;
        TextView sBuildingAddressTextView;
        CheckBox sBuildingFavoriteCheckBox;
        TextView sBuildingIdTextView;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_building, parent, false);
            holder = new ViewHolder();
            holder.sBuildingNameTextView = (TextView) convertView.findViewById(R.id.buildingName);
            holder.sBuildingAddressTextView = (TextView) convertView.findViewById(R.id.buildingAddress);
            holder.sBuildingFavoriteCheckBox = (CheckBox) convertView.findViewById(R.id.favButton);
            holder.sBuildingImageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.sBuildingIdTextView = (TextView) convertView.findViewById(R.id.buildingId);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Building building = mBuildingList.get(position);
        final int buildingId = mBuildingList.get(position).getBuildingId();


        holder.sBuildingNameTextView.setText(building.getName());
        holder.sBuildingAddressTextView.setText(building.getAddress());
        holder.sBuildingIdTextView.setText(building.getBuildingId()+"");
        holder.sBuildingImageView.setImageBitmap(mBuildingList.get(position).getBitmap());
        holder.sBuildingFavoriteCheckBox.setChecked(building.getFavorite());

        holder.sBuildingFavoriteCheckBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if (holder.sBuildingFavoriteCheckBox.isChecked()) {
                    MainActivity.mLocalStorage.addFavorite(context, String.valueOf(buildingId));
                } else {
                    MainActivity.mLocalStorage.removeFavorite(context, String.valueOf(buildingId));
                }
            }
        });

        Bitmap bitmap = imageCache.get(building.getBuildingId());
       if (bitmap != null){
            ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
            image.setImageBitmap(mBuildingList.get(position).getBitmap());
        }
        else {

            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = convertView;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        /**
         * Event listener to handle a "simple" item's tap on the ListView and inflate a details
         * screen (DetailActivity) and display more information of the building
         */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select the position the user tapped on the ListView
                Building selectedBuilding = mBuildingList.get(position);
                // Set an intent to pass the building's informating selected to DetailActivity
                Intent intent = new Intent(context, DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("buildingName", selectedBuilding.getName());
                intent.putExtra("buildingAddress", selectedBuilding.getAddress());
                intent.putExtra("buildingDescription", selectedBuilding.getDescription());

                // Loop to read the OpenHour of the selected building
                String openHours = "";
                for (int i=0; i<selectedBuilding.getOpenHours().size(); i++) {
                    openHours += selectedBuilding.getOpenHours().get(i)+"\n";
                }
                intent.putExtra("buildingOpenHours", openHours);
                context.startActivity(intent);
            }
        });


        /**
         * Event listener to handle a "long" item's tap on the ListView and inflates a
         * EditBuildingActivity. Using Intent, it passes the name, address and description
         * that can be edited and start an activity waiting for a result that calls the event
         * onActivityResult.
         *
         * It is used requestCode 2 to identify this operation
         */
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.menu_detail, popup.getMenu());
                // Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        Building selectedBuilding = mBuildingList.get(position);
                        // Inflate EditBuildingActivity
                        if (id == R.id.action_edit) {
                            Intent intent = new Intent(context, EditBuildingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("buildingId", selectedBuilding.getBuildingId());
                            intent.putExtra("buildingName", selectedBuilding.getName());
                            intent.putExtra("buildingAddress", selectedBuilding.getAddress());
                            intent.putExtra("buildingDescription", selectedBuilding.getDescription());
                            ((MainActivity) getContext()).startActivityForResult(intent, 2);
                            // Delete the selected building
                        } else if (id == R.id.action_delete) {
                            ((MainActivity) getContext()).openDeleteBuildingDialog(selectedBuilding.getBuildingId());
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
        return convertView;
    }


    // container for AsyncTask params
    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }


    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {
        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {
            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            if (result == null) return;
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView);
            image.setImageBitmap(result.bitmap);
            //result.building.setBitmap(result.bitmap);
            imageCache.put(result.building.getBuildingId(), result.bitmap);
        }
    }

    @Override
    public int getCount() {
        return mBuildingList.size();
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint.length()>0){
                    mBuildingList = (List<Building>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Building> FilteredArrayNames = new ArrayList<>();
                if (constraint.length() > 0) {
                    // perform your search here using the searchConstraint String.
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mBuildingList.size(); i++) {
                        String dataNames = mBuildingList.get(i).getName();
                        if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrayNames.add(mBuildingList.get(i));
                        }
                    }
                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                }
                else{
                    results.count = mBuildingList.size();
                    results.values = mBuildingList.size();
                }
                return results;
            }
        };
        return filter;
    }
}


