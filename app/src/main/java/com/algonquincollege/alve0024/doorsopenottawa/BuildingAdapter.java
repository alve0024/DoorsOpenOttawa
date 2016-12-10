package com.algonquincollege.alve0024.doorsopenottawa;

/**
 * Created by leonardoalps on 2016-11-07.
 */

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;
    private LruCache<Integer, Bitmap> imageCache;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.item_building, parent, false);

        Building building = buildingList.get(position);
        // Set the name of the building
        TextView nameTextView = (TextView) item.findViewById(R.id.buildingName);
        nameTextView.setText(building.getName());

        // Set the address of the building
        TextView addressTextView = (TextView) item.findViewById(R.id.buildingAddress);
        addressTextView.setText(building.getAddress());

        // Set the image of the building
        ImageView img = (ImageView) item.findViewById(R.id.imageView);
        img.setImageBitmap(buildingList.get(position).getBitmap());

        Bitmap bitmap = imageCache.get(building.getBuildingId());
       if (bitmap != null){
            ImageView image = (ImageView) item.findViewById(R.id.imageView);
            image.setImageBitmap(buildingList.get(position).getBitmap());
        }
        else {
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = item;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return item;
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
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView);
            image.setImageBitmap(result.bitmap);
            //result.building.setBitmap(result.bitmap);
            imageCache.put(result.building.getBuildingId(), result.bitmap);
        }
    }
}


