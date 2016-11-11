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
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.alve0024.doorsopenottawa.model.Building;

/**
 * Purpose: customize the Planet cell for each planet displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
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

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display planet name in the TextView widget
        Building building = buildingList.get(position);
        String imageUrl = building.getImage();
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(imageUrl);

        // TODO: Display planet photo in ImageView widget
        // Lazy initialize a planet's bitmap image
        if (imageUrl != null) {
            Log.i( "PLANETS", building.getName() + "\tbitmap in memory" );
            ImageView image = (ImageView) view.findViewById(R.id.imageView);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(imageUrl);
            image.setImageDrawable(bitmapDrawable);
        }
        else {
            Log.i( "BUILDING", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
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
//            result.building.setImage(result.bitmap);
        }
    }
}
