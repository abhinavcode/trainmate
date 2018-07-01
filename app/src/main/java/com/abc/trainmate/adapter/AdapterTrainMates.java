package com.abc.trainmate.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abc.trainmate.Models.DataTrainMates;
import com.abc.trainmate.ProfileActivity;
import com.abc.trainmate.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abhinav on 3/2/18.
 */
public class AdapterTrainMates extends RecyclerView.Adapter<AdapterTrainMates.ViewHolder> {
    Activity context;


    private List<DataTrainMates> mDataset;



    public AdapterTrainMates(List<DataTrainMates> myDataset,Activity context1) {
        mDataset = myDataset;
        context=context1;
    }




    public void add(int position, DataTrainMates item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }
    public void remove(DataTrainMates item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public AdapterTrainMates.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_trainmates, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.name.setText(mDataset.get(position).getName());
        holder.bio.setText(mDataset.get(position).getBio());
//        Glide.with(holder.imageView.getContext()).load(mDataset.get(position).getImageURL()).asBitmap().centerCrop().into(holder.imageView);

        Glide.with(holder.imageView.getContext())
                .load(Uri.parse(mDataset.get(position).getImage())) // add your image url
//                .transform(new CircleTransform(holder.imageView.getContext())) // applying the image transformer
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation((Activity) context, holder.cardView, "transition_profile");
                Intent i=new Intent(context, ProfileActivity.class);
                i.putExtra("fbid",mDataset.get(position).getId());
                i.putExtras(options.toBundle());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mDataset!=null)
            return mDataset.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView name;
        public TextView bio;
        public CircleImageView imageView;
        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name_person);
            imageView =  v.findViewById(R.id.image_person);
            bio = v.findViewById(R.id.bio_person);
            cardView=v.findViewById(R.id.card_view_person);

        }
    }

    public class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(96, 96, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(96,96, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

}