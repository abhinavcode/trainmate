package com.abc.trainmate.adapter;

/**
 * Created by abhinav on 4/2/18.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.trainmate.Models.DataTrainMates;
import com.abc.trainmate.R;
import com.abc.trainmate.SearchProfile;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AutoCompleteCustomArrayAdapter extends ArrayAdapter<DataTrainMates> {
    ArrayList<DataTrainMates> DataTrainMatess, tempDataTrainMates, suggestions;
    Context context;

    public AutoCompleteCustomArrayAdapter(Context context, ArrayList<DataTrainMates> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.DataTrainMatess = objects;
        this.tempDataTrainMates = new ArrayList<DataTrainMates>(objects);
        this.suggestions = new ArrayList<DataTrainMates>(objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataTrainMates DataTrainMate = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_autocomplete, parent, false);
        }
        TextView txtDataTrainMates = (TextView) convertView.findViewById(R.id.name);
        ImageView ivDataTrainMatesImage = (ImageView) convertView.findViewById(R.id.image);
        RelativeLayout relativeLayout=convertView.findViewById(R.id.adapter);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchProfile.class);
                intent.putExtra("fbid",DataTrainMate.getId());
                Log.d("CLICK SEARCH",DataTrainMate.getName());
                getContext().startActivity(intent);
            }
        });
        if (txtDataTrainMates != null)
            txtDataTrainMates.setText(DataTrainMate.getName() );
        if (ivDataTrainMatesImage != null )
            Glide.with(getContext()).load(DataTrainMate.getImage()).into(ivDataTrainMatesImage);
        // Now assign alternate color for rows
//        if (position % 2 == 0)
//            convertView.setBackgroundColor(getContext().getColor(R.color.odd));
//        else
//            convertView.setBackgroundColor(getContext().getColor(R.color.even));

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            DataTrainMates DataTrainMates = (DataTrainMates) resultValue;
            return DataTrainMates.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (DataTrainMates people : tempDataTrainMates) {
                    if (people.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<DataTrainMates> c = (ArrayList<DataTrainMates>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (DataTrainMates cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };
} 
        
        
//        extends ArrayAdapter<DataTrainMates> {
//
//    final String TAG = "AutocompleteCustomArrayAdapter.java";
//
//    Context mContext;
//    int layoutResourceId;
//    DataTrainMates data[];
//
//    public AutoCompleteCustomArrayAdapter(Context mContext, int layoutResourceId, DataTrainMates data[]) {
//
//        super(mContext, layoutResourceId, data);
//
//        this.layoutResourceId = layoutResourceId;
//        this.mContext = mContext;
//        this.data = data;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        try{
//
//            /*
//             * The convertView argument is essentially a "ScrapView" as described is Lucas post
//             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
//             * It will have a non-null value when ListView is asking you recycle the row layout.
//             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
//             */
//            if(convertView==null){
//                // inflate the layout
//                LayoutInflater inflater = ((Landing) mContext).getLayoutInflater();
//                convertView = inflater.inflate(layoutResourceId, parent, false);
//            }
//
//            // object item based on the position
//            DataTrainMates objectItem = data[position];
//
//            // get the TextView and then set the text (item name) and tag (item ID) values
//            TextView textViewItem = (TextView) convertView.findViewById(R.id.name);
//            textViewItem.setText(objectItem.getName());
//            ImageView imageView=convertView.findViewById(R.id.image);
//            Glide.with(mContext).load(objectItem.getImage()).into(imageView);
//            // in case you want to add some style, you can do something like:
////            textViewItem.setBackgroundColor(Color.CYAN);
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return convertView;
//
//    }
//}
