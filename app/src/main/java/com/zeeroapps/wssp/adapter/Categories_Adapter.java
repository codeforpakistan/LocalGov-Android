package com.zeeroapps.wssp.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zeeroapps.wssp.Model.ModelCategories;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.utils.Constants;

import java.util.ArrayList;




public class Categories_Adapter extends BaseAdapter {

    ArrayList<ModelCategories> modelClasses;
    private Activity context;

    public Categories_Adapter(Activity context, ArrayList<ModelCategories> modelClasses){
        //Getting all the values
        this.context = context;
        this.modelClasses = modelClasses;

    }

    @Override
    public int getCount() {
        return modelClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return modelClasses.get(position).getId();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Viewholder {

        TextView title ;
        ImageView imageView;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Viewholder viewholder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {

            viewholder = new Viewholder();
            convertView = inflater.inflate(R.layout.categories_items, null);

            viewholder.title = (TextView) convertView.findViewById(R.id.text1);
            viewholder.imageView = (ImageView) convertView.findViewById(R.id.image1);

            convertView.setTag(viewholder);

        } else {

            viewholder = (Viewholder) convertView.getTag();
        }


        viewholder.title.setText(modelClasses.get(position).getComplaint_types());
        Glide.with(context).load(modelClasses.get(position).getImage())
                .into(viewholder.imageView);


        Log.e("imagesURL", modelClasses.get(position).getComplaint_types()+" "
        + modelClasses.get(position).getImage());



        return convertView;
    }
}
