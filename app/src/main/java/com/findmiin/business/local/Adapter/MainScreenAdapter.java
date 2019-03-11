package com.findmiin.business.local.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.findmiin.business.local.R;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

//import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by JonIC on 2017-06-20.
 */
public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.ViewHolder> {
    private ArrayList<String> mItemArrayList;
    private Context context;
    private int flag;

    public MainScreenAdapter(Context context, ArrayList<String> ItemArrayList1, int flag) {
        this.mItemArrayList = ItemArrayList1;
        this.context = context;
        this.flag = flag;
    }

    @Override
    public MainScreenAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_screen_photo_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainScreenAdapter.ViewHolder viewHolder, final int i) {

        if(flag == 1){
            String path = mItemArrayList.get(i);
            int resourceid = Integer.parseInt(path);
            viewHolder.imgFrist.setImageResource(resourceid);
        }else if(flag ==2){
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.main_sports)
                    .considerExifParams(true)
                    .build();
            String path = mItemArrayList.get(i);
            ImageLoader.getInstance().displayImage(path, viewHolder.imgFrist, options);
        }
    }

    @Override
    public int getItemCount() {
        return mItemArrayList.size();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_container;
        private RoundedImageView imgFrist;

        public ViewHolder(View view) {
            super(view);
            item_container= (LinearLayout) view.findViewById(R.id.item_container);
            imgFrist= (RoundedImageView) view.findViewById(R.id.imgfirst);
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = wm.getDefaultDisplay().getWidth();

            int viewWidth =(int)(screenWidth * 0.9);
            ViewGroup.LayoutParams layoutParams = item_container.getLayoutParams();
//            LayoutUtils.setSize(item_container, viewWidth, layoutParams.height, true);
        }
    }

}